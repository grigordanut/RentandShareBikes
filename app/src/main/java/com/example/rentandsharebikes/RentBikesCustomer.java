package com.example.rentandsharebikes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Objects;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

public class RentBikesCustomer extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    private StorageReference storageRefRentBikes;
    private DatabaseReference databaseRefRentBikes;
    private DatabaseReference databaseRefRentBikesRemove;
    private DatabaseReference databaseRefCustomer;

    private DatabaseReference databaseRefBikes;
    private StorageTask bikesRentTask;

    private TextInputEditText etFNameRentBikes, etLNameRentBikes, etPhoneNoRentBikes, etEmailRentBikes;
    private TextView tVRentBikes, tVStoreNameRentBikes, tVCondRentBikes, tVModelRentBikes, tVManufactRentBikes, tVPriceRentBikes;

    private EditText eTextDateRentBike;

    //variables for data received
    private String eTextDate_RentBike, etFName_RentBikes, etLName_RentBikes, etPhoneNo_RentBikes, etEmail_RentBikes;
    private String tVStoreName_RentBikes, tVCond_RentBikes, tVModel_RentBikes, tVManufact_RentBikes;
    private String etCollectBike_Date, etReturnBike_Date;

    private double tVPrice_rentBikes;

    private ImageView ivRentBikes;

    String bike_StoreNameRentBikes = "";
    String bike_StoreKeyRentBikes = "";
    String bike_CondRentBikes = "";
    String bike_ModelRentBikes = "";
    String bike_ManufactRentBikes = "";
    String bike_PriceRentBikes = "";
    String bike_ImageRentBikes = "";
    String bike_CusIdRentBikes = "";
    String bikeKey_RentBike = "";
    String bikeKey_RentedBike = "";

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_bikes_customer);

        progressDialog = new ProgressDialog(RentBikesCustomer.this);

        firebaseAuth = FirebaseAuth.getInstance();

        storageRefRentBikes = FirebaseStorage.getInstance().getReference("Rent Bikes");
        databaseRefRentBikes = FirebaseDatabase.getInstance().getReference("Rent Bikes");

        //initialise variables
        tVRentBikes = (TextView) findViewById(R.id.tvRentBikes);
        eTextDateRentBike = (EditText) findViewById(R.id.etDateRentBike);
        eTextDateRentBike.setEnabled(false);

        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String dateRent = date.format(formatter);
        eTextDateRentBike.setText(dateRent);

        etFNameRentBikes = (TextInputEditText) findViewById(R.id.etFirstNameRentBikes);
        etLNameRentBikes = (TextInputEditText) findViewById(R.id.etLastNameRentBikes);
        etPhoneNoRentBikes = (TextInputEditText) findViewById(R.id.etPhoneNoRentBikes);
        etEmailRentBikes = (TextInputEditText) findViewById(R.id.etEmailRentBikes);

        ivRentBikes = (ImageView) findViewById(R.id.imgShowRentBikes);
        tVStoreNameRentBikes = (TextView) findViewById(R.id.tvRentBikesStoreName);
        tVCondRentBikes = (TextView) findViewById(R.id.tvRentBikesCond);
        tVModelRentBikes = (TextView) findViewById(R.id.tvRentBikesModel);
        tVManufactRentBikes = (TextView) findViewById(R.id.tvRentBikesManufact);
        tVPriceRentBikes = (TextView) findViewById(R.id.tvRentBikesPrice);

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
            bikeKey_RentBike = bundle.getString("BKey");
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

        Button buttonRentBike = findViewById(R.id.btnRentBike);
        buttonRentBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bikesRentTask != null && bikesRentTask.isInProgress()) {
                    Toast.makeText(RentBikesCustomer.this, "Rent bike in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadRentBikeData();
                }
            }
        });
    }

    private void uploadRentBikeData() {
        progressDialog.dismiss();
        if(validateBikeRentDetails()){
            eTextDate_RentBike = eTextDateRentBike.getText().toString().trim();
            etFName_RentBikes = etFNameRentBikes.getText().toString().trim();
            etLName_RentBikes = etLNameRentBikes.getText().toString().trim();
            etPhoneNo_RentBikes = etPhoneNoRentBikes.getText().toString().trim();
            etEmail_RentBikes = etEmailRentBikes.getText().toString().trim();
            tVStoreName_RentBikes = tVStoreNameRentBikes.getText().toString().trim();
            tVCond_RentBikes = tVCondRentBikes.getText().toString().trim();
            tVModel_RentBikes = tVModelRentBikes.getText().toString().trim();
            tVManufact_RentBikes = tVManufactRentBikes.getText().toString().trim();
            tVPrice_rentBikes = Double.parseDouble(tVPriceRentBikes.getText().toString().trim());
            progressDialog.setTitle("The bike is rented");
            progressDialog.show();
            String rent_BikesId = databaseRefRentBikes.push().getKey();
            bikeKey_RentedBike = rent_BikesId;
            RentBikes rent_Bikes = new RentBikes(eTextDate_RentBike, etFName_RentBikes, etLName_RentBikes,
                    etPhoneNo_RentBikes, etEmail_RentBikes, tVStoreName_RentBikes, bike_StoreKeyRentBikes,
                    tVCond_RentBikes, tVModel_RentBikes, tVManufact_RentBikes, tVPrice_rentBikes, bike_ImageRentBikes,
                    bike_CusIdRentBikes, bikeKey_RentedBike);
            assert rent_BikesId != null;
            databaseRefRentBikes.child(rent_BikesId).setValue(rent_Bikes)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        startActivity(new Intent(RentBikesCustomer.this, CustomerPageRentBikes.class));
                        Toast.makeText(RentBikesCustomer.this, "Rent Bike successfully", Toast.LENGTH_SHORT).show();
                        deleteBikeData();
                        finish();
                    }
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(RentBikesCustomer.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
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

    private void deleteBikeData() {
        databaseRefRentBikesRemove = FirebaseDatabase.getInstance().getReference().child("Bikes");
        Query query = databaseRefRentBikesRemove.orderByChild("bike_Key").equalTo(bikeKey_RentBike);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ds.getRef().removeValue();
                }
                //progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RentBikesCustomer.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

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
                for (DataSnapshot dsUser : dataSnapshot.getChildren()) {
                    final FirebaseUser custom_Details = firebaseAuth.getCurrentUser();

                    final Customers custom_data = dsUser.getValue(Customers.class);

                    assert custom_Details != null;
                    assert custom_data != null;
                    if (Objects.requireNonNull(custom_Details.getEmail()).equalsIgnoreCase(custom_data.getEmail_Customer())) {
                        tVRentBikes.setText("Welcome: "+custom_data.getfName_Customer()+" "+custom_data.getlName_Customer());
                        etFNameRentBikes.setText(custom_data.getfName_Customer());
                        etLNameRentBikes.setText(custom_data.getlName_Customer());
                        etPhoneNoRentBikes.setText(custom_data.getPhoneNumb_Customer());
                        etEmailRentBikes.setText(custom_data.getEmail_Customer());
                        bike_CusIdRentBikes = custom_Details.getUid();
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
