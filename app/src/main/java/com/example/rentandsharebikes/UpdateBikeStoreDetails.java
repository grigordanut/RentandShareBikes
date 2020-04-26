package com.example.rentandsharebikes;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class UpdateBikeStoreDetails extends AppCompatActivity {

    private DatabaseReference databaseReference;

    private EditText etNewStoreAddress, etNewStoreLatitude, etNewStoreLongitude, etNewStoreNoSlots;
    private TextView tvNewStoreLocation;
    private String tvNewStore_Location, etNewStore_Address;
    private int etNewStore_NoSlots;

    private double etNewStore_Latitude, etNewStore_Longitude;

    private ProgressDialog progressDialog;
    String store_Location = "";
    String store_Address = "";
    String updateStore_Latitude = "";
    String updateStore_Longitude = "";
    String updateStore_NrSlots = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_bike_store_details);

        getIntent().hasExtra("SLocation");
        store_Location = (getIntent().getExtras()).getString("SLocation");
        tvNewStoreLocation = (TextView) findViewById(R.id.tvUpdateStoreLocation);
        tvNewStoreLocation.setText(store_Location);

        tvNewStoreLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogStoreLocation();
            }
        });

        getIntent().hasExtra("SAddress");
        store_Address = (getIntent().getExtras()).getString("SAddress");
        etNewStoreAddress = (EditText) findViewById(R.id.etUpdateStoreAddress);
        etNewStoreAddress.setText(store_Address);

        getIntent().hasExtra("SLatitude");
        updateStore_Latitude = (getIntent().getExtras()).getString("SLatitude");
        etNewStoreLatitude = (EditText) findViewById(R.id.etUpdateStoreLatitude);
        etNewStoreLatitude.setText(String.valueOf(updateStore_Latitude));

        getIntent().hasExtra("SLongitude");
        updateStore_Longitude = (getIntent().getExtras()).getString("SLongitude");
        etNewStoreLongitude = (EditText) findViewById(R.id.etUpdateStoreLongitude);
        etNewStoreLongitude.setText(String.valueOf(updateStore_Longitude));

        getIntent().hasExtra("SNoSlots");
        updateStore_NrSlots = getIntent().getExtras().getString("SNoSlots");
        etNewStoreNoSlots = (EditText) findViewById(R.id.etUpdateStoreNoSlots);
        etNewStoreNoSlots.setText(String.valueOf(updateStore_NrSlots));

        progressDialog = new ProgressDialog(this);

        Button btnSaveBikeStoreUpdates = findViewById(R.id.btnSaveStoreUpdates);
        btnSaveBikeStoreUpdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBikeStoreDetails();
            }
        });
    }

    public void updateBikeStoreDetails() {

        final String newStore_AddressVal = etNewStoreAddress.getText().toString().trim();
        final String newStore_LatitudeVal = etNewStoreLatitude.getText().toString().trim();
        final String newStore_LongitudeVal = etNewStoreLongitude.getText().toString().trim();
        final String newStore_NrSlotsVal = etNewStoreNoSlots.getText().toString();

        if (TextUtils.isEmpty(newStore_AddressVal)) {
            etNewStoreAddress.setError("Please enter store Address");
            etNewStoreAddress.requestFocus();
        } else if (TextUtils.isEmpty(newStore_LatitudeVal)) {
            etNewStoreLatitude.setError("Please enter store Latitude");
            etNewStoreLatitude.requestFocus();
        } else if (TextUtils.isEmpty(newStore_LongitudeVal)) {
            etNewStoreLongitude.setError("Please enter store Longitude");
            etNewStoreLongitude.requestFocus();
        } else if (TextUtils.isEmpty(newStore_NrSlotsVal)) {
            etNewStoreNoSlots.setError("Please enter the number of slots");
            etNewStoreNoSlots.requestFocus();
        }
        else{
            progressDialog.setMessage("Update the Bike Store");
            progressDialog.show();

            tvNewStore_Location = tvNewStoreLocation.getText().toString().trim();
            etNewStore_Address = etNewStoreAddress.getText().toString().trim();
            etNewStore_Latitude = Double.parseDouble(Objects.requireNonNull(etNewStoreLatitude.getText()).toString().trim());
            etNewStore_Longitude = Double.parseDouble(etNewStoreLongitude.getText().toString().trim());
            etNewStore_NoSlots = Integer.parseInt(etNewStoreNoSlots.getText().toString());

            databaseReference = FirebaseDatabase.getInstance().getReference().child("Bike Stores");

            Query query = databaseReference.orderByChild("bikeStore_Location").equalTo(store_Location);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        ds.getRef().child("bikeStore_Address").setValue(etNewStore_Address);
                        ds.getRef().child("bikeStore_Latitude").setValue(etNewStore_Latitude);
                        ds.getRef().child("bikeStore_Location").setValue(tvNewStore_Location);
                        ds.getRef().child("bikeStore_Longitude").setValue(etNewStore_Longitude);
                        ds.getRef().child("bikeStore_NumberSlots").setValue(etNewStore_NoSlots);
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

    public void alertDialogStoreLocation() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("The Store Location cannot be modified because is stored in Bikes table ");
        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
