package com.example.rentandsharebikes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class UpdateBikeStoreDetails extends AppCompatActivity {

    //Save updated Bike Store data to database
    private DatabaseReference databaseRefStoreUpload;

    //Check Bike data into database
    private DatabaseReference databaseRefBikeCheck;

    //Save updated Bike Store name to Bike database
    private DatabaseReference databaseRefBikeUpload;

    private TextView tvStoreUp;

    private EditText etStoreLocationUp, etStoreAddressUp, etStoreLatitudeUp, etStoreLongitudeUp, etStoreNoSlotsUp;

    private String etStore_LocationUp, etStore_AddressUp;
    private int etStore_NoSlotsUp;

    private double etStore_LatitudeUp, etStore_LongitudeUp;

    private ProgressDialog progressDialog;

    private String store_LocationUp = "";
    private String store_AddressUp = "";
    private String store_LatitudeUp = "";
    private String store_LongitudeUp = "";
    private String store_NrSlotsUp = "";
    private String store_KeyUp = "";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_bike_store_details);

        progressDialog = new ProgressDialog(this);

        tvStoreUp = findViewById(R.id.tvStoreUpdate);

        etStoreLocationUp = findViewById(R.id.etStoreLocationUpdate);
        etStoreAddressUp = findViewById(R.id.etStoreAddressUpdate);
        etStoreLatitudeUp = findViewById(R.id.etStoreLatitudeUpdate);
        etStoreLongitudeUp = findViewById(R.id.etStoreLongitudeUpdate);
        etStoreNoSlotsUp = findViewById(R.id.etStoreNoSlotsUpdate);

        Bundle bundleStore = getIntent().getExtras();
        if (bundleStore != null) {
            store_LocationUp = bundleStore.getString("SLocation");
            store_AddressUp = bundleStore.getString("SAddress");
            store_LatitudeUp = bundleStore.getString("SLatitude");
            store_LongitudeUp = bundleStore.getString("SLongitude");
            store_NrSlotsUp = bundleStore.getString("SNoSlots");
            store_KeyUp = bundleStore.getString("SKey");
        }

        tvStoreUp.setText("Update " + store_LocationUp + " Bike Store details");

        etStoreLocationUp.setText(store_LocationUp);
        etStoreAddressUp.setText(store_AddressUp);
        etStoreLatitudeUp.setText(store_LatitudeUp);
        etStoreLongitudeUp.setText(store_LongitudeUp);
        etStoreNoSlotsUp.setText(store_NrSlotsUp);

        Button btnSaveBikeStoreUpdates = findViewById(R.id.btnSaveStoreUpdate);
        btnSaveBikeStoreUpdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBikeBikesStoreName();
                updateBikeStoreDetails();
            }
        });
    }

    public void updateBikeStoreDetails() {

        if (validateBikeStoreDetailsUp()) {

            etStore_LocationUp = etStoreLocationUp.getText().toString().trim();
            etStore_AddressUp = etStoreAddressUp.getText().toString().trim();
            etStore_LatitudeUp = Double.parseDouble(etStoreLatitudeUp.getText().toString().trim());
            etStore_LongitudeUp = Double.parseDouble(etStoreLongitudeUp.getText().toString().trim());
            etStore_NoSlotsUp = Integer.parseInt(etStoreNoSlotsUp.getText().toString().trim());

            progressDialog.setMessage("Update the Bike Store");
            progressDialog.show();

            databaseRefStoreUpload = FirebaseDatabase.getInstance().getReference("Bike Stores");
            Query queryStore = databaseRefStoreUpload.orderByChild("bikeStore_Key").equalTo(store_KeyUp);

            queryStore.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        ds.getRef().child("bikeStore_Location").setValue(etStore_LocationUp);
                        ds.getRef().child("bikeStore_Address").setValue(etStore_AddressUp);
                        ds.getRef().child("bikeStore_Latitude").setValue(etStore_LatitudeUp);
                        ds.getRef().child("bikeStore_Longitude").setValue(etStore_LatitudeUp);
                        ds.getRef().child("bikeStore_NumberSlots").setValue(etStore_NoSlotsUp);
                    }
                    progressDialog.dismiss();
                    Toast.makeText(UpdateBikeStoreDetails.this, "Bike Store Updated", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(UpdateBikeStoreDetails.this, AdminPage.class));
                    finish();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(UpdateBikeStoreDetails.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private Boolean validateBikeStoreDetailsUp() {
        boolean result = false;

        final String bikeStore_LocationVal = etStoreLocationUp.getText().toString().trim();
        final String bikeStore_AddressVal = etStoreAddressUp.getText().toString().trim();
        final String bikeStore_LatitudeVal = etStoreLatitudeUp.getText().toString().trim();
        final String bikeStore_LongitudeVal = etStoreLongitudeUp.getText().toString().trim();
        final String bikeStore_NrSlotsVal = etStoreNoSlotsUp.getText().toString().trim();

        if (TextUtils.isEmpty(bikeStore_LocationVal)) {
            etStoreLocationUp.setError("Please enter store Location");
            etStoreLocationUp.requestFocus();
        } else if (TextUtils.isEmpty(bikeStore_AddressVal)) {
            etStoreAddressUp.setError("Please enter store Address");
            etStoreAddressUp.requestFocus();
        } else if (TextUtils.isEmpty(bikeStore_LatitudeVal)) {
            etStoreLatitudeUp.setError("Please enter store Latitude");
            etStoreLatitudeUp.requestFocus();
        } else if (TextUtils.isEmpty(bikeStore_LongitudeVal)) {
            etStoreLongitudeUp.setError("Please enter store Longitude");
            etStoreLongitudeUp.requestFocus();
        } else if (TextUtils.isEmpty(bikeStore_NrSlotsVal)) {
            etStoreNoSlotsUp.setError("Please enter the number of slots");
            etStoreNoSlotsUp.requestFocus();
        } else {
            result = true;
        }
        return result;
    }

    private void updateBikeBikesStoreName() {
        databaseRefBikeCheck = FirebaseDatabase.getInstance().getReference("Bikes");
        databaseRefBikeCheck.orderByChild("bikeStoreName").equalTo(store_LocationUp)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            final String etBike_BikeStoreLocCheck = etStoreLocationUp.getText().toString().trim();
                            databaseRefBikeUpload = FirebaseDatabase.getInstance().getReference("Bikes");
                            databaseRefBikeUpload.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot dsBike : dataSnapshot.getChildren()) {
                                        dsBike.getRef().child("bikeStoreName").setValue(etBike_BikeStoreLocCheck);
                                    }
                                    progressDialog.dismiss();
                                    Toast.makeText(UpdateBikeStoreDetails.this, "Bike Store Updated", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(UpdateBikeStoreDetails.this, AdminPage.class));
                                    finish();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(UpdateBikeStoreDetails.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(UpdateBikeStoreDetails.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
