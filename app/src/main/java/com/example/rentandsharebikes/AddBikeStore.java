package com.example.rentandsharebikes;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddBikeStore extends AppCompatActivity {

    //Declare Bike Store database variables (Retrieve data)
    private DatabaseReference dbRefStoreCheck;

    //Declare Bike Store database variables (Upload data)
    private DatabaseReference dbRefStoreUpload;

    private EditText etStoreLocation, etStoreAddress, etStoreLatitude, etStoreLongitude, etStoreNumberSlots;

    private String etStore_Location, etStore_Address;
    private int etStore_NrSlots;

    private double etStore_Latitude, etStore_Longitude;

    private ProgressDialog progressDialog;

    private List<BikeStores> bikeStoresList;
    private List<BikeStores> bikeStoresListCheck;

    private String store_Key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bike_store);

        bikeStoresList = new ArrayList<>();
        bikeStoresListCheck = new ArrayList<>();

        //Retrieve Bike Store data from Bike Stores table
        dbRefStoreCheck = FirebaseDatabase.getInstance().getReference("Bike Stores");

        //Create Bike Store Store table into database
        dbRefStoreUpload = FirebaseDatabase.getInstance().getReference("Bike Stores");

        //Receive Bike Store address details from other activity
        getIntent().hasExtra("Address");
        String store_Address = Objects.requireNonNull(getIntent().getExtras()).getString("Address");

        //Receive Bike Store latitude details from other activity
        getIntent().hasExtra("Latitude");
        String store_Latitude = Objects.requireNonNull(getIntent().getExtras()).getString("Latitude");

        //Receive Bike Store longitude details from other activity
        getIntent().hasExtra("Longitude");
        String store_Longitude = Objects.requireNonNull(getIntent().getExtras()).getString("Longitude");

        etStoreLocation = findViewById(R.id.etBikeStoreLocation);
        etStoreAddress = findViewById(R.id.etBikeStoreAddress);
        etStoreLatitude = findViewById(R.id.etBikeStoreLatitude);
        etStoreLongitude = findViewById(R.id.etBikeStoreLongitude);
        etStoreNumberSlots = findViewById(R.id.etBikeStoreNrSlots);

        etStoreAddress.setText(store_Address);
        etStoreLatitude.setText(store_Latitude);
        etStoreLongitude.setText(store_Longitude);

        progressDialog = new ProgressDialog(this);

        //Save Bike Store data into database
        Button buttonSaveBikeStore = findViewById(R.id.btnSaveBikeStore);
        buttonSaveBikeStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBikesStoreName();
            }
        });
    }

    //Check if the Bike Store name already exist in Bike Store database
    private void checkBikesStoreName() {
        final String etStore_LocCheckStore = etStoreLocation.getText().toString().trim();

        dbRefStoreCheck.orderByChild("bikeStore_Location").equalTo(etStore_LocCheckStore)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    alertDialogStoreExist();
                } else {
                    uploadBikeSoreData();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AddBikeStore.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Upload Bike Store data into database
    private void uploadBikeSoreData() {

        progressDialog.dismiss();
        if (validateBikeStoreDetails()) {

            //Read entered Bike Store data
            etStore_Location = etStoreLocation.getText().toString().trim();
            etStore_Address = etStoreAddress.getText().toString().trim();
            etStore_Latitude = Double.parseDouble(Objects.requireNonNull(etStoreLatitude.getText()).toString().trim());
            etStore_Longitude = Double.parseDouble(etStoreLongitude.getText().toString().trim());
            etStore_NrSlots = Integer.parseInt(etStoreNumberSlots.getText().toString());

            progressDialog.setMessage("Add Bike Store");
            progressDialog.show();

            String storeID = dbRefStoreUpload.push().getKey();
            store_Key = storeID;
            assert storeID != null;
            BikeStores bike_store = new BikeStores(etStore_Location, etStore_Address, etStore_Latitude, etStore_Longitude, etStore_NrSlots, store_Key);

            dbRefStoreUpload.child(storeID).setValue(bike_store).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {

                        etStoreLocation.setText("");
                        etStoreAddress.setText("");
                        etStoreNumberSlots.setText("");

                        startActivity(new Intent(AddBikeStore.this, AdminPage.class));

                        Toast.makeText(AddBikeStore.this, "Bike Store Added", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AddBikeStore.this, "Filed to add Bike Store", Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddBikeStore.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    //Validate Bike Store data
    private Boolean validateBikeStoreDetails() {

        boolean result = false;

        //Read entered Bike Store data
        final String store_LocationVal = etStoreLocation.getText().toString().trim();
        final String store_AddressVal = etStoreAddress.getText().toString().trim();
        final String store_LatitudeVal = etStoreLatitude.getText().toString().trim();
        final String store_LongitudeVal = etStoreLongitude.getText().toString().trim();
        final String store_NrSlotsVal = etStoreNumberSlots.getText().toString();

        //Validate Bike Store details
        if (TextUtils.isEmpty(store_LocationVal)) {
            etStoreLocation.setError("Enter store Location");
            etStoreLocation.requestFocus();
        } else if (TextUtils.isEmpty(store_AddressVal)) {
            etStoreAddress.setError("Please enter store Address");
            etStoreAddress.requestFocus();
        } else if (TextUtils.isEmpty(store_LatitudeVal)) {
            etStoreLatitude.setError("Please enter store Latitude");
            etStoreLatitude.requestFocus();
        } else if (TextUtils.isEmpty(store_LongitudeVal)) {
            etStoreLongitude.setError("Please enter store Longitude");
            etStoreLongitude.requestFocus();
        } else if (TextUtils.isEmpty(store_NrSlotsVal)) {
            etStoreNumberSlots.setError("Please enter the number of slots");
            etStoreNumberSlots.requestFocus();
        } else {
            result = true;
        }
        return result;
    }

    //Notify if the Bike Store name already exist in database
    public void alertDialogStoreExist() {
        final String etStore_LocCheckAlert = etStoreLocation.getText().toString().trim();
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("The Bike Store " + etStore_LocCheckAlert + " already exist");
        alertDialogBuilder.setPositiveButton("OK",
                (arg0, arg1) -> {
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
