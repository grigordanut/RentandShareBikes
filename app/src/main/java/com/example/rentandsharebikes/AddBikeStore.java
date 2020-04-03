package com.example.rentandsharebikes;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class AddBikeStore extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private ValueEventListener bikeStoresEventListener;

    private EditText etStoreLocation;
    private EditText etStoreAddress;
    private EditText etStoreLatitude;
    private EditText etStoreLongitude;
    private EditText etStoreNumberSlots;

    private TextView tvStoreNumber;

    private String etStore_Location, etStore_Address;
    private int tvStore_Number, etStore_NrSlots;

    private double etStore_Latitude, etStore_Longitude;

    String store_Address, store_Latitude, store_Longitude;

    private ProgressDialog progressDialog;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bike_store);

        getIntent().hasExtra("Address");
        store_Address = Objects.requireNonNull(getIntent().getExtras()).getString("Address");

        getIntent().hasExtra("Latitude");
        store_Latitude = Objects.requireNonNull(getIntent().getExtras()).getString("Latitude");

        getIntent().hasExtra("Longitude");
        store_Longitude = Objects.requireNonNull(getIntent().getExtras()).getString("Longitude");

        tvStoreNumber = (TextView) findViewById(R.id.tvBikeStoreNumber);
        etStoreLocation = findViewById(R.id.etBikeStoreLocation);
        etStoreAddress = findViewById(R.id.etBikeStoreAddress);
        etStoreLatitude = findViewById(R.id.etBikeStoreLatitude);
        etStoreLongitude = findViewById(R.id.etBikeStoreLongitude);
        etStoreNumberSlots = findViewById(R.id.etBikeStoreNrSlots);

        etStoreAddress.setText(store_Address);
        etStoreLatitude.setText(store_Latitude);
        etStoreLongitude.setText(store_Longitude);

        progressDialog = new ProgressDialog(this);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Bike Stores");

        Button buttonSaveBikeStore = findViewById(R.id.btnSaveBikeStore);
        buttonSaveBikeStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String store_locationValidation = etStoreLocation.getText().toString().trim();
                final String store_AddressValidation = etStoreAddress.getText().toString().trim();
                final String store_LatitudeValidation = etStoreLatitude.getText().toString().trim();
                final String store_LongitudeValidation = etStoreLongitude.getText().toString().trim();
                final String store_NrSlotsValidation = etStoreNumberSlots.getText().toString();

                if (TextUtils.isEmpty(store_locationValidation)) {
                    etStoreLocation.setError("Enter store Location");
                    etStoreLocation.requestFocus();
                } else if (TextUtils.isEmpty(store_AddressValidation)) {
                    etStoreAddress.setError("Please enter store Address");
                    etStoreAddress.requestFocus();
                } else if (TextUtils.isEmpty(store_LatitudeValidation)) {
                    etStoreLatitude.setError("Please enter store Latitude");
                    etStoreLatitude.requestFocus();
                } else if (TextUtils.isEmpty(store_LongitudeValidation)) {
                    etStoreLongitude.setError("Please enter store Longitude");
                    etStoreLongitude.requestFocus();
                } else if (TextUtils.isEmpty(store_NrSlotsValidation)) {
                    etStoreNumberSlots.setError("Please enter the number of slots");
                    etStoreNumberSlots.requestFocus();
                } else {

                    tvStore_Number = Integer.parseInt(tvStoreNumber.getText().toString().trim());
                    etStore_Location = etStoreLocation.getText().toString().trim();
                    etStore_Address = etStoreAddress.getText().toString().trim();
                    etStore_Latitude = Double.parseDouble(Objects.requireNonNull(etStoreLatitude.getText()).toString().trim());
                    etStore_Longitude = Double.parseDouble(etStoreLongitude.getText().toString().trim());
                    etStore_NrSlots = Integer.parseInt(etStoreNumberSlots.getText().toString());

                    progressDialog.setMessage("Add Bike Store");
                    progressDialog.show();
                    String storeID = databaseReference.push().getKey();

                    BikeStore bike_store = new BikeStore(tvStore_Number, etStore_Location, etStore_Address, etStore_Latitude, etStore_Longitude, etStore_NrSlots);

                    assert storeID != null;
                    databaseReference.child(storeID).setValue(bike_store).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                tvStoreNumber.setText("");
                                etStoreLocation.setText("");
                                etStoreAddress.setText("");
                                etStoreNumberSlots.setText("");

                                startActivity(new Intent(AddBikeStore.this, AdminPage.class));

                                Toast.makeText(AddBikeStore.this, "Bike Store Added", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(AddBikeStore.this, "Filed to add bike Store", Toast.LENGTH_LONG).show();
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
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onStart() {
        super.onStart();
        incrementNumberBikeStores();
    }

    private void incrementNumberBikeStores() {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Bike Stores");
        bikeStoresEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    BikeStore bike_Store = postSnapshot.getValue(BikeStore.class);
                    assert bike_Store != null;
                    tvStore_Number = Integer.parseInt(String.valueOf(bike_Store.getBikeStore_Number() + 1));
                    tvStoreNumber.setText(String.valueOf(tvStore_Number));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AddBikeStore.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
