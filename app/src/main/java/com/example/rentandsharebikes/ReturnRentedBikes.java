package com.example.rentandsharebikes;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
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

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

public class ReturnRentedBikes extends AppCompatActivity {

    //Access database Customers table
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseRefCustomer;

    //Display Rented bikes details from Rent Bikes database
    private FirebaseStorage firebaseStShowRentedBikes;
    private DatabaseReference databaseRefShowRentedBikes;
    private ValueEventListener eventListenerShowRentedBikes;

    //Delete rented bikes from RentedBikes Database;
    private DatabaseReference databaseRefRemoveRentBikes;

    //Retrieve data from Bike Stores database
    private DatabaseReference databaseRefBikeStores;

    //Return bikes to Bikes database
    private DatabaseReference databaseRefReturnBikes;

    private BikeStores bike_Stores;

    private TextInputEditText etFNameReturnBikes, etLNameReturnBikes;
    private TextView tVReturnBikes, tVStoreNameReturnBikes, tVCondReturnBikes, tVModelReturnBikes, tVManufactReturnBikes, tVPriceReturnBikes, tVSelectBikeStore;
    private EditText eTDateOfRentBike, eTDateReturnBike, eTRentDurationBike, eTReturnTotalHours, eTReturnTotalPricePay, etBikeStoreReturn;

    //variables for data received
    private String storeName_ReturnBikes, tVCond_ReturnBikes, tVModel_ReturnBikes, tVManufact_ReturnBikes;

    private Double tVPrice_ReturnBikes, eTReturn_TotalPricePay;
    private Double totalHours = 0.00;

    private ImageView ivReturnBikes;

    private List<BikeStores> listBikeStores;

    //Receive Bike image
    private String img_RentedBike;

    //Received BikeRentedKey of rented bike from BikeImageCustomerShowBikesRented
    private String rentedBike_Key = "";

    //BikeStoreKey and BikeStoreName that the bike is returning
    private String returnBikeStore_Key = "";
    private String returnBikeStore_Name = "";

    private ProgressDialog progressDialog;

    private BikeStoreAdapterReturnRentedBikes bikeStoreAdapterReturnRentedBikes;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_rented_bikes);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Return rented bikes");

        progressDialog = new ProgressDialog(ReturnRentedBikes.this);

        listBikeStores = new ArrayList<>();

        firebaseAuth = FirebaseAuth.getInstance();

        //Retrieve data from RentedBikes database (Bikes rented by Customer)
        firebaseStShowRentedBikes = getInstance();
        databaseRefShowRentedBikes = FirebaseDatabase.getInstance().getReference("Rent Bikes");

        //Retrieve data from Customers database (Customer that rented bikes)
        databaseRefCustomer = FirebaseDatabase.getInstance().getReference("Customers");

        //Retrieve data from BikesSores database
        databaseRefBikeStores = FirebaseDatabase.getInstance().getReference("Bike Stores");

        //initialise variables
        tVReturnBikes = findViewById(R.id.tvReturnBikes);
        tVSelectBikeStore = findViewById(R.id.tvSelectBikeStore);

        //Date of Rent
        eTDateOfRentBike = findViewById(R.id.etDateOfRentBike);
        eTDateOfRentBike.setEnabled(false);

        //Date of Return
        eTDateReturnBike = findViewById(R.id.etDateReturnBike);
        eTDateReturnBike.setEnabled(false);

        //Duration Time
        eTRentDurationBike = findViewById(R.id.etRentDurationBike);
        eTRentDurationBike.setEnabled(false);

        //Total Hours
        eTReturnTotalHours = findViewById(R.id.etReturnTotalHours);
        eTReturnTotalHours.setEnabled(false);

        eTReturnTotalPricePay = findViewById(R.id.etReturnTotalPricePay);
        eTReturnTotalHours.setEnabled(false);

        etFNameReturnBikes = findViewById(R.id.etFirstNameReturnBikes);
        etFNameReturnBikes.setEnabled(false);
        etLNameReturnBikes = findViewById(R.id.etLastNameReturnBikes);
        etLNameReturnBikes.setEnabled(false);

        etBikeStoreReturn = findViewById(R.id.etBikeReturnStore);
        etBikeStoreReturn.setEnabled(false);

        ivReturnBikes = findViewById(R.id.imgShowReturnBikes);
        tVStoreNameReturnBikes = findViewById(R.id.tvReturnBikesStoreName);
        tVCondReturnBikes = findViewById(R.id.tvReturnBikesCond);
        tVModelReturnBikes = findViewById(R.id.tvReturnBikesModel);
        tVManufactReturnBikes = findViewById(R.id.tvReturnBikesManufact);
        tVPriceReturnBikes = findViewById(R.id.tvReturnBikesPrice);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            rentedBike_Key = bundle.getString("BikeRentedKey");
        }

        tVSelectBikeStore.setOnClickListener(v -> {
            etBikeStoreReturn.setText("");
            getBikeStoreName();
        });

        Button buttonReturnBikes = findViewById(R.id.btnReturnBike);
        buttonReturnBikes.setOnClickListener(v -> {

            returnRentedBike();

        });
    }

    public void getBikeStoreName() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ReturnRentedBikes.this);

        bikeStoreAdapterReturnRentedBikes = new BikeStoreAdapterReturnRentedBikes(ReturnRentedBikes.this, R.layout.image_bike_store_return_rented_bike, listBikeStores);

        databaseRefBikeStores.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                listBikeStores.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    bike_Stores = postSnapshot.getValue(BikeStores.class);

                    assert bike_Stores != null;

                    bike_Stores.setBikeStore_Key(postSnapshot.getKey());
                    listBikeStores.add(bike_Stores);
                }

                bikeStoreAdapterReturnRentedBikes.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ReturnRentedBikes.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        alertDialogBuilder
                .setTitle("Select the Bike Store!!")
                .setCancelable(false)
                .setSingleChoiceItems(bikeStoreAdapterReturnRentedBikes, -1, (dialog, id) -> {
                    bike_Stores = listBikeStores.get(id);
                })
                .setPositiveButton("NEXT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        returnBikeStore_Key = bike_Stores.getBikeStore_Key();
                        returnBikeStore_Name = bike_Stores.getBikeStore_Location();
                        etBikeStoreReturn.setText(returnBikeStore_Name);

                        Toast.makeText(ReturnRentedBikes.this, "The Bike Store successfully selected!!", Toast.LENGTH_SHORT).show();

                        dialog.dismiss();
                    }
                })

                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void returnRentedBike() {

        storeName_ReturnBikes = Objects.requireNonNull(etBikeStoreReturn.getText()).toString().trim();

        if (TextUtils.isEmpty(storeName_ReturnBikes)) {
            alertReturnBikeStoreEmpty();
        } else {
            storeName_ReturnBikes = etBikeStoreReturn.getText().toString().trim();
            tVCond_ReturnBikes = tVCondReturnBikes.getText().toString().trim();
            tVModel_ReturnBikes = tVModelReturnBikes.getText().toString().trim();
            tVManufact_ReturnBikes = tVManufactReturnBikes.getText().toString().trim();
            tVPrice_ReturnBikes = Double.parseDouble(tVPriceReturnBikes.getText().toString().trim());

            databaseRefReturnBikes = FirebaseDatabase.getInstance().getReference("Bikes");

            progressDialog.setTitle("The bike is returning to: \n" + storeName_ReturnBikes + " store!");
            progressDialog.show();

            String bikeKey_ReturnRentedBike = databaseRefReturnBikes.push().getKey();

            Bikes return_Bikes = new Bikes(tVCond_ReturnBikes, tVModel_ReturnBikes, tVManufact_ReturnBikes,
                    tVPrice_ReturnBikes, img_RentedBike, storeName_ReturnBikes, returnBikeStore_Key);

            assert bikeKey_ReturnRentedBike != null;
            databaseRefReturnBikes.child(bikeKey_ReturnRentedBike).setValue(return_Bikes)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                deleteRentedBikes();

                                startActivity(new Intent(ReturnRentedBikes.this, CustomerPageRentBikes.class));
                                Toast.makeText(ReturnRentedBikes.this, "Return Bike successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ReturnRentedBikes.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

            progressDialog.dismiss();

            // Get the data from an ImageView as bytes
//            ivReturnBikes.setDrawingCacheEnabled(true);
//            ivReturnBikes.buildDrawingCache();
//            Bitmap bitmap = ivReturnBikes.getDrawingCache();
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//            byte[] data = baos.toByteArray();

//            storageRefReturnBikes.putBytes(data)
//                    .addOnSuccessListener(taskSnapshot -> {
//                        storageRefReturnBikes.getDownloadUrl().addOnSuccessListener(uri -> {
//                            returnBike_Key = databaseRefReturnBikes.push().getKey();
//                            Bikes return_Bikes = new Bikes(tVCond_ReturnBikes, tVModel_ReturnBikes, tVManufact_ReturnBikes,
//                                    tVPrice_ReturnBikes, uri.toString(), storeName_ReturnBikes, returnBikeStore_Key);
//
//                            databaseRefReturnBikes.child(returnBike_Key).setValue(return_Bikes);
//                            startActivity(new Intent(ReturnRentedBikes.this, CustomerPageRentBikes.class));
//                            deleteRentedBikes();
//                            Toast.makeText(ReturnRentedBikes.this, "Return Bike successfully", Toast.LENGTH_SHORT).show();
//                            finish();
//                        });
//
//                    }).addOnFailureListener(e -> {
//                        progressDialog.dismiss();
//                        Toast.makeText(ReturnRentedBikes.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }).addOnProgressListener(taskSnapshot -> {
//                        //show upload progress
//                        double progress = 100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount();
//                        progressDialog.setMessage("Returned: " + (int) progress + "%");
//                        progressDialog.setProgress((int) progress);
//                    });
        }
    }

    private void deleteRentedBikes() {

        databaseRefRemoveRentBikes = FirebaseDatabase.getInstance().getReference().child("Rent Bikes");
        databaseRefRemoveRentBikes.child(rentedBike_Key).removeValue();
    }

    public void alertReturnBikeStoreEmpty() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setTitle("The return Bike Store is empty.")
                .setMessage("Please enter the return Bike Store.")
                .setPositiveButton("OK", (dialog, id) -> dialog.dismiss());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        loadCustomerDetailsRentedBikes();
        loadBikeDetailsRentedBike();
    }

    //Display customer details
    public void loadCustomerDetailsRentedBikes() {

        databaseRefCustomer.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"SetTextI18n", "NewApi"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //retrieve data from database
                for (DataSnapshot ds_User : dataSnapshot.getChildren()) {
                    FirebaseUser user_Db = firebaseAuth.getCurrentUser();

                    Customers custom_Data = ds_User.getValue(Customers.class);

                    assert user_Db != null;
                    assert custom_Data != null;
                    if (user_Db.getUid().equals(ds_User.getKey())) {
                        tVReturnBikes.setText("Welcome: " + custom_Data.getfName_Customer() + " " + custom_Data.getlName_Customer());
                        etFNameReturnBikes.setText(custom_Data.getfName_Customer());
                        etLNameReturnBikes.setText(custom_Data.getlName_Customer());
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
    private void loadBikeDetailsRentedBike() {

        eventListenerShowRentedBikes = databaseRefShowRentedBikes.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    RentedBikes rented_Bikes = postSnapshot.getValue(RentedBikes.class);
                    assert rented_Bikes != null;
                    rented_Bikes.setBikeKey_RentBikes(postSnapshot.getKey());
                    if (rented_Bikes.getBikeKey_RentBikes().equals(rentedBike_Key)) {
                        eTDateOfRentBike.setText(rented_Bikes.getDate_RentBikes());
                        tVStoreNameReturnBikes.setText(rented_Bikes.getStoreLocation_RentBikes());
                        tVCondReturnBikes.setText(rented_Bikes.getBikeCond_RentBikes());
                        tVModelReturnBikes.setText(rented_Bikes.getBikeModel_RentBikes());
                        tVManufactReturnBikes.setText(rented_Bikes.getBikeManufact_RentBikes());
                        tVPriceReturnBikes.setText(String.valueOf(rented_Bikes.getBikePrice_RentBikes()));

                        img_RentedBike = rented_Bikes.getBikeImage_RentBike();

                        calculateRentDurationPrice();

                        Picasso.get()
                                .load(rented_Bikes.getBikeImage_RentBike())
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    public void calculateRentDurationPrice() {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        String currentDateAndTime = sdf.format(new Date());
        eTDateReturnBike.setText(currentDateAndTime);

        String date1 = eTDateOfRentBike.getText().toString().trim();
        String date2 = eTDateReturnBike.getText().toString().trim();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        Duration diff = Duration.between(LocalDateTime.parse(date1, formatter),
                LocalDateTime.parse(date2, formatter));

        if (diff.isZero()) {
            Toast.makeText(ReturnRentedBikes.this, "The bike was rented 0m", Toast.LENGTH_SHORT).show();
        } else {
            long days = diff.toDays();
            if (days != 0) {
                diff = diff.minusDays(days);
            }
            long hours = diff.toHours();
            if (hours != 0) {
                diff = diff.minusHours(hours);
            }
            long minutes = diff.toMinutes();
            if (minutes != 0) {
                diff = diff.minusMinutes(minutes);
            }
//                            long seconds = diff.getSeconds();
//                            if (seconds != 0) {
//                                System.out.print("" + seconds + "s ");
//                            }

            eTRentDurationBike.setText(days + " d " + " " + hours + " h " + "" + minutes + " m");
            totalHours = (double) ((days * 24) + hours + (minutes / 60));
            eTReturnTotalHours.setText(String.valueOf(totalHours));
            tVPrice_ReturnBikes = Double.parseDouble(tVPriceReturnBikes.getText().toString().trim());
            eTReturn_TotalPricePay = totalHours * tVPrice_ReturnBikes;
            eTReturnTotalPricePay.setText("€ " + eTReturn_TotalPricePay);
        }
    }
}

