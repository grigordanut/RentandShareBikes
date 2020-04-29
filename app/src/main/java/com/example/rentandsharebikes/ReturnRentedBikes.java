package com.example.rentandsharebikes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class ReturnRentedBikes extends AppCompatActivity {

    //Access database Rent Bikes table
    private FirebaseStorage bikesStShowRentedBikes;
    private DatabaseReference databaseRefShowRentedBikes;
    private ValueEventListener showRentedBikesEventListener;

    //Remove bikes from Rent Bikes table
    private DatabaseReference databaseRefRemoveBikes;

    //Save bikes to Bikes table
    private StorageReference storageRefReturnBike;
    DatabaseReference databaseRefReturnBike;

    //Access database Customers table
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseRefCustomer;

    private StorageTask bikesReturnTask;

    private TextInputEditText etFNameReturnBikes, etLNameReturnBikes;
    private TextView tVReturnBikes, tVDateReturnBike, tVStoreNameReturnBikes, tVCondReturnBikes, tVModelReturnBikes, tVManufactReturnBikes, tVPriceReturnBikes;
    private CheckBox cBoxRetSameStore, cBoxRetDiffStore;
    private EditText etBikeStoreReturn;

    //variables for data received
    private String etFName_ReturnBikes, etLName_ReturnBikes;
    private String tVDate_ReturnBikes, storeName_ReturnBikes, tVCond_ReturnBikes, tVModel_ReturnBikes, tVManufact_ReturnBikes,img_ReturnBikes;

    private double tVPrice_ReturnBikes;

    private ImageView ivReturnBikes;

    String bike_StoreNameRentedBikesSame = "";
    String bike_StoreNameRentedBikesDiff = "";
    String bike_CusIdRentedBikes = "";
    String bike_KeyRentedBike = "";
    String bikeKey_ReturnBike = "";

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_rented_bikes);

        progressDialog = new ProgressDialog(ReturnRentedBikes.this);

        firebaseAuth = FirebaseAuth.getInstance();

        //initialise variables
        tVReturnBikes = (TextView) findViewById(R.id.tvReturnBikes);

        LocalDate localDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            localDate = LocalDate.now();
        }
        DateTimeFormatter formatter = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("dd/MM/YYYY");
        }
        String insertDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            insertDate = localDate.format(formatter);
        }
        tVDateReturnBike = (TextView) findViewById(R.id.tvDateReturnBike);
        tVDateReturnBike.setText(insertDate);

        etFNameReturnBikes = (TextInputEditText) findViewById(R.id.etFirstNameReturnBikes);
        etFNameReturnBikes.setEnabled(false);
        etLNameReturnBikes = (TextInputEditText) findViewById(R.id.etLastNameReturnBikes);
        etLNameReturnBikes.setEnabled(false);

        etBikeStoreReturn = (EditText) findViewById(R.id.etBikeReturnStore);
        etBikeStoreReturn.setEnabled(false);

        ivReturnBikes = (ImageView) findViewById(R.id.imgShowReturnBikes);
        tVStoreNameReturnBikes = (TextView) findViewById(R.id.tvReturnBikesStoreName);
        tVCondReturnBikes = (TextView) findViewById(R.id.tvReturnBikesCond);
        tVModelReturnBikes = (TextView) findViewById(R.id.tvReturnBikesModel);
        tVManufactReturnBikes = (TextView) findViewById(R.id.tvReturnBikesManufact);
        tVPriceReturnBikes = (TextView) findViewById(R.id.tvReturnBikesPrice);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            bike_StoreNameRentedBikesSame = bundle.getString("BStoreSame");
            bike_StoreNameRentedBikesDiff = bundle.getString("BStoreDiff");
            bike_KeyRentedBike = bundle.getString("BKey");
        }

        etBikeStoreReturn.setText(bike_StoreNameRentedBikesDiff);

        cBoxRetSameStore = (CheckBox) findViewById(R.id.cbReturnBikeSameStore);
        cBoxRetSameStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cBoxRetSameStore.isChecked()) {
                    cBoxRetDiffStore.setChecked(false);
                    etBikeStoreReturn.setText(bike_StoreNameRentedBikesSame);
                }
            }
        });

        cBoxRetDiffStore = (CheckBox) findViewById(R.id.cbReturnBikeDiffStore);
        cBoxRetDiffStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cBoxRetDiffStore.isChecked()) {
                    cBoxRetSameStore.setChecked(false);
                    Intent intent = new Intent(ReturnRentedBikes.this, BikeStoreImageReturnBikeDifferentStore.class);
                    intent.putExtra("BStoreSame", bike_StoreNameRentedBikesSame);
                    intent.putExtra("BKey", bike_KeyRentedBike);
                    startActivity(intent);
                }
            }
        });

        Button buttonReturnBikes = (Button)findViewById(R.id.btnReturnBike);
        buttonReturnBikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bikesReturnTask != null && bikesReturnTask.isInProgress()) {
                    Toast.makeText(ReturnRentedBikes.this, "Return Bikes in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadBikesNoPicture();
                }
            }
        });

        progressDialog.show();
    }

    private void uploadBikesNoPicture() {
        storageRefReturnBike = FirebaseStorage.getInstance().getReference("Bikes");
        databaseRefReturnBike = FirebaseDatabase.getInstance().getReference("Bikes");

        final String etStoreName_ReturnBikesVal = Objects.requireNonNull(etBikeStoreReturn.getText()).toString().trim();

        if (TextUtils.isEmpty(etStoreName_ReturnBikesVal)) {
            alertReturnBikeStore();
        } else {
            storeName_ReturnBikes = etBikeStoreReturn.getText().toString().trim();
            tVCond_ReturnBikes = tVCondReturnBikes.getText().toString().trim();
            tVModel_ReturnBikes = tVModelReturnBikes.getText().toString().trim();
            tVManufact_ReturnBikes = tVManufactReturnBikes.getText().toString().trim();
            tVPrice_ReturnBikes = Double.parseDouble(tVPriceReturnBikes.getText().toString().trim());
            img_ReturnBikes = ivReturnBikes.toString();


            progressDialog.setTitle("The bike is rented");
            progressDialog.show();

            bikeKey_ReturnBike = databaseRefReturnBike.push().getKey();

            Bikes return_Bikes = new Bikes(tVCond_ReturnBikes, tVModel_ReturnBikes, tVManufact_ReturnBikes, tVPrice_ReturnBikes, img_ReturnBikes, storeName_ReturnBikes, bikeKey_ReturnBike);

            assert bikeKey_ReturnBike != null;
            databaseRefReturnBike.child(bikeKey_ReturnBike).setValue(return_Bikes).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(ReturnRentedBikes.this, CustomerPageRentBikes.class));
                        Toast.makeText(ReturnRentedBikes.this, "Bike Returned successfully", Toast.LENGTH_SHORT).show();
                        deleteDataNoPicture();
                        finish();
                    }
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(ReturnRentedBikes.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void deleteDataNoPicture() {
        databaseRefRemoveBikes = FirebaseDatabase.getInstance().getReference().child("Rent Bikes");
        Query query = databaseRefRemoveBikes.orderByChild("bike_RentKey").equalTo(bike_KeyRentedBike);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ds.getRef().removeValue();
                }
                progressDialog.dismiss();
                Toast.makeText(ReturnRentedBikes.this, "Rented Bike removed", Toast.LENGTH_SHORT).show();
                //startActivity(new Intent(UpdateEve.this, UserPage.class));
                //finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ReturnRentedBikes.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void alertReturnBikeStore(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("The return Bike Store can not be empty.");
        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        loadCustomerDetailsRentBikes();
        loadBikesListReturn();
        progressDialog.dismiss();
    }

    //Display customer details
    public void loadCustomerDetailsRentBikes() {
        //retrieve data from database into text views
        databaseRefCustomer = FirebaseDatabase.getInstance().getReference("Customers");
        databaseRefCustomer.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"SetTextI18n", "NewApi"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //retrieve data from database
                for (DataSnapshot dsUser : dataSnapshot.getChildren()) {
                    final FirebaseUser custom_Details = firebaseAuth.getCurrentUser();

                    final Customers custom_data = dsUser.getValue(Customers.class);

                    assert custom_Details != null;
                    assert custom_data != null;
                    if (Objects.requireNonNull(custom_Details.getEmail()).equalsIgnoreCase(custom_data.getEmail_Customer())) {
                        tVReturnBikes.setText("Welcome: " + custom_data.getfName_Customer() + " " + custom_data.getlName_Customer());
                        etFNameReturnBikes.setText(custom_data.getfName_Customer());
                        etLNameReturnBikes.setText(custom_data.getlName_Customer());
                        bike_CusIdRentedBikes = custom_Details.getUid();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ReturnRentedBikes.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Display bike details
    private void loadBikesListReturn() {
        //initialize the bike storage database
        bikesStShowRentedBikes = FirebaseStorage.getInstance();
        databaseRefShowRentedBikes = FirebaseDatabase.getInstance().getReference("Rent Bikes");

        showRentedBikesEventListener = databaseRefShowRentedBikes.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    RentBikes return_Bikes = postSnapshot.getValue(RentBikes.class);
                    assert return_Bikes != null;
                    return_Bikes.setBike_RentKey(postSnapshot.getKey());
                    if (return_Bikes.getBike_RentKey().equals(bike_KeyRentedBike)) {

                        tVStoreNameReturnBikes.setText(return_Bikes.getStoreLocation_RentBikes());
                        tVCondReturnBikes.setText(return_Bikes.getBikeCond_RentBikes());
                        tVModelReturnBikes.setText(return_Bikes.getBikeModel_RentBikes());
                        tVManufactReturnBikes.setText(return_Bikes.getBikeManufact_RentBikes());
                        tVPriceReturnBikes.setText(String.valueOf(return_Bikes.getBikePrice_RentBikes()));

                        //receive data from the other activity
                        Picasso.get()
                                .load(return_Bikes.getBikeImage_RentBike())
                                .placeholder(R.mipmap.ic_launcher)
                                .fit()
                                .centerCrop()
                                .into(ivReturnBikes);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ReturnRentedBikes.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
