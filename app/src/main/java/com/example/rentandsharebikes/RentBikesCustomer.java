package com.example.rentandsharebikes;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Objects;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

public class RentBikesCustomer extends AppCompatActivity {

    private static final int PICK_PICTURE = 100;
    private static final int TAKE_PICTURE = 101;

    private FirebaseAuth firebaseAuth;

    private DatabaseReference databaseRefRentBikes;
    private StorageTask bikesRentTask;

    //Delete bike details from Bikes database
    private StorageReference storageRefRemoveBikes;
    private DatabaseReference databaseRefRemoveBikes;

    //Retrieve data from Customers database
    private DatabaseReference databaseRefCustomer;

    private TextInputEditText etFNameRentBikes, etLNameRentBikes, etPhoneNoRentBikes, etEmailRentBikes;
    private TextView tVRentBikes, tVStoreNameRentBikes, tVCondRentBikes, tVModelRentBikes, tVManufactRentBikes, tVPriceRentBikes;

    private EditText eTextDateRentBike;

    //variables for data received
    private String eTextDate_RentBike, etFName_RentBikes, etLName_RentBikes, etPhoneNo_RentBikes, etEmail_RentBikes;
    private String tVStoreName_RentBikes, tVCond_RentBikes, tVModel_RentBikes, tVManufact_RentBikes;

    private double tVPrice_rentBikes;

    private ImageView ivRentBikes;

    private Uri imageUri;

    String bike_StoreNameRentBikes = "";
    String bike_StoreKeyRentBikes = "";
    String bike_CondRentBikes = "";
    String bike_ModelRentBikes = "";
    String bike_ManufactRentBikes = "";
    String bike_PriceRentBikes = "";
    String bike_ImageRentBikes = "";
    String bike_CusIdRentBikes = "";
    String bike_Key = "";

    private ProgressDialog progressDialog;


    @SuppressLint("UnsafeDynamicallyLoadedCode")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_bikes_customer);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Rent bikes customer");

        progressDialog = new ProgressDialog(RentBikesCustomer.this);

        firebaseAuth = FirebaseAuth.getInstance();

        databaseRefRentBikes = FirebaseDatabase.getInstance().getReference("Rent Bikes");

        //initialise variables
        tVRentBikes = findViewById(R.id.tvRentBikes);
        eTextDateRentBike = findViewById(R.id.etDateRentBike);
        eTextDateRentBike.setEnabled(false);

        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String dateRent = date.format(formatter);
        eTextDateRentBike.setText(dateRent);

        etFNameRentBikes = findViewById(R.id.etFirstNameRentBikes);
        etLNameRentBikes = findViewById(R.id.etLastNameRentBikes);
        etPhoneNoRentBikes = findViewById(R.id.etPhoneNoRentBikes);
        etEmailRentBikes = findViewById(R.id.etEmailRentBikes);

        ivRentBikes = findViewById(R.id.imgShowRentBikes);
        tVStoreNameRentBikes = findViewById(R.id.tvRentBikesStoreName);
        tVCondRentBikes = findViewById(R.id.tvRentBikesCond);
        tVModelRentBikes = findViewById(R.id.tvRentBikesModel);
        tVManufactRentBikes = findViewById(R.id.tvRentBikesManufact);
        tVPriceRentBikes = findViewById(R.id.tvRentBikesPrice);

        //receive data from the other activity
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            bike_CondRentBikes = bundle.getString("BCondition");
            bike_ModelRentBikes = bundle.getString("BModel");
            bike_ManufactRentBikes = bundle.getString("BManufact");
            bike_PriceRentBikes = bundle.getString("BPrice");
            bike_ImageRentBikes = bundle.getString("BImage");
            bike_StoreNameRentBikes = bundle.getString("BStoreName");
            bike_StoreKeyRentBikes = bundle.getString("BStoreKey");
            bike_Key = bundle.getString("BKey");
        }

        Picasso.get()
                .load(bike_ImageRentBikes)
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(ivRentBikes);

        tVStoreNameRentBikes.setText(bike_StoreNameRentBikes);
        tVCondRentBikes.setText(bike_CondRentBikes);
        tVModelRentBikes.setText(bike_ModelRentBikes);
        tVManufactRentBikes.setText(bike_ManufactRentBikes);
        tVPriceRentBikes.setText(String.valueOf(bike_PriceRentBikes));

        Button btn_RentBike = findViewById(R.id.btnRentBike);
        btn_RentBike.setOnClickListener(v -> {
            uploadRentBikesData();
        });
    }

    public void uploadRentBikesData() {

        if (validateBikeRentDetails()) {

            eTextDate_RentBike = eTextDateRentBike.getText().toString().trim();
            etFName_RentBikes = Objects.requireNonNull(etFNameRentBikes.getText()).toString().trim();
            etLName_RentBikes = Objects.requireNonNull(etLNameRentBikes.getText()).toString().trim();
            etPhoneNo_RentBikes = Objects.requireNonNull(etPhoneNoRentBikes.getText()).toString().trim();
            etEmail_RentBikes = Objects.requireNonNull(etEmailRentBikes.getText()).toString().trim();
            tVStoreName_RentBikes = tVStoreNameRentBikes.getText().toString().trim();
            tVCond_RentBikes = tVCondRentBikes.getText().toString().trim();
            tVModel_RentBikes = tVModelRentBikes.getText().toString().trim();
            tVManufact_RentBikes = tVManufactRentBikes.getText().toString().trim();
            tVPrice_rentBikes = Double.parseDouble(tVPriceRentBikes.getText().toString().trim());

            progressDialog.setTitle("The Bike is renting!!");
            progressDialog.show();

            String bikeKey_RentedBike = databaseRefRentBikes.push().getKey();

            RentedBikes rent_Bikes = new RentedBikes(eTextDate_RentBike, etFName_RentBikes,
                    etLName_RentBikes, etPhoneNo_RentBikes, etEmail_RentBikes,
                    tVStoreName_RentBikes, bike_StoreKeyRentBikes, tVCond_RentBikes,
                    tVModel_RentBikes, tVManufact_RentBikes, tVPrice_rentBikes, bike_ImageRentBikes,
                    bike_CusIdRentBikes);

            assert bikeKey_RentedBike != null;
            databaseRefRentBikes.child(bikeKey_RentedBike).setValue(rent_Bikes)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                deleteBikesData();

                                startActivity(new Intent(RentBikesCustomer.this, CustomerPageRentBikes.class));
                                Toast.makeText(RentBikesCustomer.this, "Rented bike successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RentBikesCustomer.this, "Rented bike successfully", Toast.LENGTH_SHORT).show();
                        }
                    });

            progressDialog.dismiss();

            // Get the data from an ImageView as bytes
//            ivRentBikes.setDrawingCacheEnabled(true);
//            ivRentBikes.buildDrawingCache();
//            Bitmap bitmap = ivRentBikes.getDrawingCache();
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//            byte[] data = baos.toByteArray();

//            storageRefRentBikes.putBytes(data)
//                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
//                            storageRefRentBikes.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                @Override
//                                public void onSuccess(@NonNull Uri uri) {
//                                    String rent_BikesId = databaseRefRentBikes.push().getKey();
//                                    bikeKey_RentedBike = rent_BikesId;
//                                    RentedBikes rent_Bikes = new RentedBikes(eTextDate_RentBike, etFName_RentBikes,
//                                            etLName_RentBikes, etPhoneNo_RentBikes, etEmail_RentBikes,
//                                            tVStoreName_RentBikes, bike_StoreKeyRentBikes, tVCond_RentBikes,
//                                            tVModel_RentBikes, tVManufact_RentBikes, tVPrice_rentBikes, uri.toString(),
//                                            bike_CusIdRentBikes, bikeKey_RentedBike);
//
//                                    assert rent_BikesId != null;
//                                    databaseRefRentBikes.child(rent_BikesId).setValue(rent_Bikes);
//                                    etFNameRentBikes.setText("");
//                                    etLNameRentBikes.setText("");
//                                    etPhoneNoRentBikes.setText("");
//                                    etEmailRentBikes.setText("");
//
//                                    ivRentBikes.setImageResource(R.drawable.image_add_bikes);
//
//                                    startActivity(new Intent(RentBikesCustomer.this, CustomerPageRentBikes.class));
//                                    deleteBikesData();
//                                    Toast.makeText(RentBikesCustomer.this, "Rented bike successfully", Toast.LENGTH_SHORT).show();
//                                    finish();
//                                }
//                            });
//                            progressDialog.dismiss();
//                        }
//                    })
//
//                    .addOnFailureListener(e -> {
//                        progressDialog.dismiss();
//                        Toast.makeText(RentBikesCustomer.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }).addOnProgressListener(taskSnapshot -> {
//                        //show rented progress
//                        double progress = 100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount();
//                        progressDialog.setMessage("Rented: " + (int) progress + "%");
//                        progressDialog.setProgress((int) progress);
//                    });
        }
    }

    private Boolean validateBikeRentDetails() {
        boolean result = false;

        final String etFName_RentBikesVal = Objects.requireNonNull(etFNameRentBikes.getText()).toString().trim();
        final String etLName_RentBikesVal = Objects.requireNonNull(etLNameRentBikes.getText()).toString().trim();
        final String etPhoneNo_RentBikesVal = Objects.requireNonNull(etPhoneNoRentBikes.getText()).toString().trim();
        final String etEmail_RentBikesVal = Objects.requireNonNull(etEmailRentBikes.getText()).toString().trim();

        if (TextUtils.isEmpty(etFName_RentBikesVal)) {
            etFNameRentBikes.setError("First name cannot be empty");
            etFNameRentBikes.requestFocus();
        } else if (TextUtils.isEmpty(etLName_RentBikesVal)) {
            etLNameRentBikes.setError("Last name cannot be empty");
            etLNameRentBikes.requestFocus();
        } else if (TextUtils.isEmpty(etPhoneNo_RentBikesVal)) {
            etPhoneNoRentBikes.setError("Last name cannot be empty");
            etPhoneNoRentBikes.requestFocus();
        } else if (TextUtils.isEmpty(etEmail_RentBikesVal)) {
            etEmailRentBikes.setError("Last name cannot be empty");
            etEmailRentBikes.requestFocus();
        } else {
            result = true;
        }

        return result;
    }

    private void deleteBikesData() {

        databaseRefRemoveBikes = FirebaseDatabase.getInstance().getReference().child("Bikes");
        databaseRefRemoveBikes.child(bike_Key).removeValue();
    }

//    private void deleteBikesDataOld() {
//        storageRefRemoveBikes = getInstance().getReferenceFromUrl(bike_ImageRentBikes);
//        storageRefRemoveBikes.delete().addOnSuccessListener(aVoid -> {
//
//            databaseRefRemoveBikes = FirebaseDatabase.getInstance().getReference().child("Bikes");
//            databaseRefRemoveBikes.child(bikeKey_RentBike).removeValue();
//        }).addOnFailureListener(e -> Toast.makeText(RentBikesCustomer.this, e.getMessage(), Toast.LENGTH_SHORT).show());
//    }

    @Override
    public void onStart() {
        super.onStart();
        loadCustomerDetailsRentBikes();
    }

    public void loadCustomerDetailsRentBikes() {

        //retrieve data from firebase database
        databaseRefCustomer = FirebaseDatabase.getInstance().getReference("Customers");
        databaseRefCustomer.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"SetTextI18n", "NewApi"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds_user : dataSnapshot.getChildren()) {
                    FirebaseUser user_Details = firebaseAuth.getCurrentUser();

                    Customers customer = ds_user.getValue(Customers.class);

                    assert user_Details != null;
                    assert customer != null;
                    if (user_Details.getUid().equals(ds_user.getKey())) {
                        tVRentBikes.setText("Welcome: " + customer.getfName_Customer() + " " + customer.getlName_Customer());
                        etFNameRentBikes.setText(customer.getfName_Customer());
                        etLNameRentBikes.setText(customer.getlName_Customer());
                        etPhoneNoRentBikes.setText(customer.getPhoneNumb_Customer());
                        etEmailRentBikes.setText(customer.getEmail_Customer());
                        bike_CusIdRentBikes = user_Details.getUid();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RentBikesCustomer.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
