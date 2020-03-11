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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddBikeStore extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private ValueEventListener bikeStoreDBEventListener;

    private TextInputEditText etStoreLocation;
    private TextInputEditText etStoreAddress;
    private TextInputEditText etStoreNumberSlots;

    private TextView tvStoreNumber;

    private String etStore_Location, etStore_Address;
    private int tvStore_Number, etStore_NrSlots;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bike_store);

        tvStoreNumber = (TextView)findViewById(R.id.tvBikeStoreNumber);

        etStoreLocation = (TextInputEditText)findViewById(R.id.etBikeStoreLocation);
        etStoreAddress = (TextInputEditText) findViewById(R.id.etBikeStoreAddress);
        etStoreNumberSlots = (TextInputEditText) findViewById(R.id.etBikeStoreNrSlots);

        progressDialog = new ProgressDialog(this);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Bike Stores");

        Button buttonSaveBikeStore = (Button)findViewById(R.id.btnSaveBikeStore);
        buttonSaveBikeStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvStore_Number = Integer.parseInt(tvStoreNumber.getText().toString().trim());
                etStore_Location = etStoreLocation.getText().toString().trim();
                etStore_Address = etStoreAddress.getText().toString().trim();
                etStore_NrSlots = Integer.parseInt(etStoreNumberSlots.getText().toString());

                if(TextUtils.isEmpty(etStore_Location)){
                    etStoreLocation.setError("Enter store Location");
                    etStoreLocation.requestFocus();
                }

                else if (TextUtils.isEmpty(etStore_Address)){
                    etStoreAddress.setError("Please enter store Address");
                    etStoreAddress.requestFocus();
                }

                else if(TextUtils.isEmpty(String.valueOf(etStore_NrSlots))){
                    etStoreNumberSlots.setError("Please enter the number of slots");
                    etStoreNumberSlots.requestFocus();
                }

                else {
                    progressDialog.setMessage("Add Bike Store");
                    progressDialog.show();
                    String storeID = databaseReference.push().getKey();

                    BikeStore bike_store = new BikeStore(tvStore_Number, etStore_Location, etStore_Address, etStore_NrSlots);

                    assert storeID != null;
                    databaseReference.child(storeID).setValue(bike_store).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                etStoreLocation.setText("");
                                etStoreAddress.setText("");
                                etStoreNumberSlots.setText("");

                                Toast.makeText(AddBikeStore.this, "Bike Store Added", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(AddBikeStore.this, AdminPage.class));

                            }
                            else{
                                Toast.makeText(AddBikeStore.this, "Filed to add bike Store", Toast.LENGTH_LONG).show();
                            }
                            progressDialog.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddBikeStore.this,e.getMessage(), Toast.LENGTH_SHORT).show();
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
        loadStoreBikes();
    }

    private void loadStoreBikes(){
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Bike Stores");
        bikeStoreDBEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    BikeStore bike_Store = postSnapshot.getValue(BikeStore.class);
                    assert bike_Store != null;
                    tvStore_Number = Integer.parseInt(String.valueOf(bike_Store.getBikeStore_Number()+1));
                    tvStoreNumber.setText(String.valueOf(tvStore_Number));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
