package com.example.rentandsharebikes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

        Button btn_SaveBikeStoreUpdate = findViewById(R.id.btnSaveStoreUpdate);
        btn_SaveBikeStoreUpdate.setOnClickListener(v -> {
            updateBikeBikesStoreName();
            updateBikeStoreDetails();
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

            BikeStores bike_store = new BikeStores(etStore_LocationUp, etStore_AddressUp, etStore_LatitudeUp, etStore_LongitudeUp, etStore_NoSlotsUp);

            databaseRefStoreUpload = FirebaseDatabase.getInstance().getReference("Bike Stores");

            databaseRefStoreUpload.child(store_KeyUp).setValue(bike_store)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(UpdateBikeStoreDetails.this, AdminPage.class));
                            Toast.makeText(UpdateBikeStoreDetails.this, "Bike Store Updated", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(UpdateBikeStoreDetails.this, "Filed to update the Bike Store", Toast.LENGTH_SHORT).show();
                        }

                        progressDialog.dismiss();
                    })

                    .addOnFailureListener(e -> Toast.makeText(UpdateBikeStoreDetails.this, e.getMessage(), Toast.LENGTH_SHORT).show());
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

        Query queryStore = databaseRefBikeCheck.orderByChild("bikeStoreKey").equalTo(store_KeyUp);
        queryStore.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    final String etBike_BikeStoreLocUp = etStoreLocationUp.getText().toString().trim();
                    databaseRefBikeUpload = FirebaseDatabase.getInstance().getReference("Bikes");
                    databaseRefBikeUpload.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot dsBike : dataSnapshot.getChildren()) {
                                dsBike.getRef().child("bikeStoreName").setValue(etBike_BikeStoreLocUp);
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
