package com.example.rentandsharebikes;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddBikeStore extends AppCompatActivity {

    private DatabaseReference databaseRefCheck;
    private DatabaseReference databaseRefUpload;
    private ValueEventListener bikeStoreEventListener;

    private EditText etStoreLocation, etStoreAddress, etStoreLatitude, etStoreLongitude, etStoreNumberSlots;

    private String etStore_Location, etStore_Address;
    private int etStore_NrSlots;

    private double etStore_Latitude, etStore_Longitude;

    private ProgressDialog progressDialog;

    public List<BikeStore> bikeStoreList;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bike_store);

        bikeStoreList = new ArrayList<>();

        getIntent().hasExtra("Address");
        String store_Address = Objects.requireNonNull(getIntent().getExtras()).getString("Address");

        getIntent().hasExtra("Latitude");
        String store_Latitude = Objects.requireNonNull(getIntent().getExtras()).getString("Latitude");

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

        databaseRefUpload = FirebaseDatabase.getInstance().getReference().child("Bike Stores");
        databaseRefCheck = FirebaseDatabase.getInstance().getReference().child("Bike Stores");

        Button buttonSaveBikeStore = findViewById(R.id.btnSaveBikeStore);
        buttonSaveBikeStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadBikeSoreData();
                //checkStoreStoreName();
                //checkBikeStoreNew();
                //checkBikeStoreTest();
                //checkStackFlow();
                //loadStoreBikes();
            }
        });
    }

    private void loadBikeSoreData() {
        final String store_LocationVal = etStoreLocation.getText().toString().trim();
        final String store_AddressVal = etStoreAddress.getText().toString().trim();
        final String store_LatitudeVal = etStoreLatitude.getText().toString().trim();
        final String store_LongitudeVal = etStoreLongitude.getText().toString().trim();
        final String store_NrSlotsVal = etStoreNumberSlots.getText().toString();

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
            etStore_Location = etStoreLocation.getText().toString().trim();
            etStore_Address = etStoreAddress.getText().toString().trim();
            etStore_Latitude = Double.parseDouble(Objects.requireNonNull(etStoreLatitude.getText()).toString().trim());
            etStore_Longitude = Double.parseDouble(etStoreLongitude.getText().toString().trim());
            etStore_NrSlots = Integer.parseInt(etStoreNumberSlots.getText().toString());

            progressDialog.setMessage("Add Bike Store");
            progressDialog.show();
            String storeID = databaseRefUpload.push().getKey();

            BikeStore bike_store = new BikeStore(etStore_Location, etStore_Address, etStore_Latitude, etStore_Longitude, etStore_NrSlots);

            assert storeID != null;
            databaseRefUpload.child(storeID).setValue(bike_store).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {

                        etStoreLocation.setText("");
                        etStoreAddress.setText("");
                        etStoreNumberSlots.setText("");

                        startActivity(new Intent(AddBikeStore.this, AdminPage.class));

                        Toast.makeText(AddBikeStore.this, "Bike Store Added", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(AddBikeStore.this, "Filed to add Bike Store", Toast.LENGTH_LONG).show();
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

    private void checkStoreStoreName() {

        final String etStore_LocCheckStore = etStoreLocation.getText().toString().trim();

        databaseRefCheck = FirebaseDatabase.getInstance().getReference().child("Bike Stores");
        Query query = databaseRefCheck.orderByChild("bikeStore_Address").equalTo(etStore_LocCheckStore);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                    if (ds.getRef().child("bikeStore_Address").setValue(etStore_LocCheckStore)){
//
//                    }
                    alertDialogStoreExist();

                }
                progressDialog.dismiss();
                Toast.makeText(AddBikeStore.this, "Event removed", Toast.LENGTH_SHORT).show();
                //startActivity(new Intent(AddBikeStore.this, UserPage.class));
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AddBikeStore.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkBikeStoreNew() {
        final String etStore_LocCheckStore = etStoreLocation.getText().toString().trim();
        databaseRefCheck = FirebaseDatabase.getInstance().getReference().child("Bike Stores");
        bikeStoreEventListener = databaseRefCheck.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    BikeStore bike_Store = postSnapshot.getValue(BikeStore.class);
                    assert bike_Store != null;
                    if (etStore_LocCheckStore.equals(postSnapshot.getRef().child("bikeStore_Address").toString().contentEquals(etStore_LocCheckStore))) {
                        alertDialogStoreExist();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkBikeStoreTest() {

        alertDialogStoreExist();
        final String etStore_LocCheckStore = etStoreLocation.getText().toString().trim();
        Query query = FirebaseDatabase.getInstance().getReference().child("Bike Stores").orderByChild("bikeStore_Address");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if ((dataSnapshot.getChildrenCount() > 0) && (Objects.equals(dataSnapshot.getValue(), etStore_LocCheckStore))) {
//                    alertDialogStoreExist();
//                    // 1 or more users exist which have the username property "usernameToCheckIfExists"
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AddBikeStore.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkStackFlow() {
        final String etStore_LocCheckStore = etStoreLocation.getText().toString().trim();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Bike Stores");
        databaseReference.orderByChild("bikeStore_Address").equalTo(etStore_LocCheckStore)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            etStoreAddress.setText(etStore_LocCheckStore);
                            alertDialogStoreExist();
                        }
                        //Toast.makeText(Main3Activity.this, "Code exists", Toast.LENGTH_SHORT).show();


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public void alertDialogStoreExist() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("This Bike Store already exist");
        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onStart() {
        super.onStart();
        //loadStoreBikes();
    }

    private void loadStoreBikes() {

        final String etStore_LocCheckStore = etStoreLocation.getText().toString().trim();
        if (databaseRefCheck != null) {
            bikeStoreEventListener = databaseRefCheck.addValueEventListener(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        BikeStore bike_Store = postSnapshot.getValue(BikeStore.class);
                        assert bike_Store != null;
                        if (bike_Store.getBikeStore_Location().equals(etStore_LocCheckStore)) {
                            alertDialogStoreExist();
                        } else {
                            loadBikeSoreData();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else{
            loadBikeSoreData();
        }
    }
}
