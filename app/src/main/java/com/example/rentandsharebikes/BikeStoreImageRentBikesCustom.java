package com.example.rentandsharebikes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BikeStoreImageRentBikesCustom extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private ValueEventListener bikeStoreEventListener;

    private RecyclerView bikeStoreRecyclerView;
    private BikeStoreAdapterRentBikesCustom bikeStoreAdapterRentBikesCustom;

    private List<BikeStore> bikeStoreList;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_store_image_rent_bikes_custom);

        bikeStoreRecyclerView = (RecyclerView) findViewById(R.id.evRecyclerView);
        bikeStoreRecyclerView.setHasFixedSize(true);
        bikeStoreRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressDialog = new ProgressDialog(this);
        bikeStoreList = new ArrayList<>();

        progressDialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        loadBikeStoresListCustomer();
    }

    private void loadBikeStoresListCustomer(){
        //initialize the bike store database
        databaseReference = FirebaseDatabase.getInstance().getReference("Bike Stores");

        bikeStoreEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    BikeStore bikeStore = postSnapshot.getValue(BikeStore.class);
                    assert bikeStore != null;
                    bikeStore.setStoreKey(postSnapshot.getKey());
                    bikeStoreList.add(bikeStore);
                }
                bikeStoreAdapterRentBikesCustom = new BikeStoreAdapterRentBikesCustom(BikeStoreImageRentBikesCustom.this, bikeStoreList);
                bikeStoreRecyclerView.setAdapter(bikeStoreAdapterRentBikesCustom);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikeStoreImageRentBikesCustom.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}
