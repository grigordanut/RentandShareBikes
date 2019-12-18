package com.example.rentandsharebikes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddBikeStore extends AppCompatActivity {

    private TextInputEditText locationBikeStore;
    private TextInputEditText addressBikeStore;
    private TextInputEditText numberSlots;

    private String loc_BikeStore,address_BikeStore, number_SlotsStore;

    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bike_store);

        locationBikeStore = (TextInputEditText)findViewById(R.id.etLocationBikeStore);
        addressBikeStore = (TextInputEditText) findViewById(R.id.etAddressBikeStore);
        numberSlots = (TextInputEditText) findViewById(R.id.etNrSlotsBikeStore);

        progressDialog = new ProgressDialog(this);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Bike stores");

        Button buttonSaveBikeStore = (Button)findViewById(R.id.btnSaveBikeStore);
        buttonSaveBikeStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            loc_BikeStore =  locationBikeStore.getText().toString().trim();
            address_BikeStore = addressBikeStore.getText().toString().trim();
            number_SlotsStore = numberSlots.getText().toString().trim();

            if(loc_BikeStore.isEmpty()){
                locationBikeStore.setError("Enter store Location");
                locationBikeStore.requestFocus();
            }

            else if (address_BikeStore.isEmpty()){
                addressBikeStore.setError("Please enter store Address");
                addressBikeStore.requestFocus();
            }

            else if(number_SlotsStore.isEmpty()){
                numberSlots.setError("Please enter the number of slots");
                numberSlots.requestFocus();
            }

            else {
                //String loc_BikeStore =  locationBikeStore.getText().toString().trim();
                //String address_BikeStore = addressBikeStore.getText().toString().trim();
                //String number_SlotsStore = numberSlots.getText().toString();
                String storeID = databaseReference.push().getKey();

                BikeStore bike_store = new BikeStore(loc_BikeStore, address_BikeStore, number_SlotsStore);

                assert storeID != null;
                databaseReference.child(storeID).setValue(bike_store).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            finish();
                            Toast.makeText(AddBikeStore.this, "Bike Store Added", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(AddBikeStore.this, BikeStoreImageShowStoresListAdmin.class));
                        }
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
}
