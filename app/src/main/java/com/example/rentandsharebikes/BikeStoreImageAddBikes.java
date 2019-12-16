package com.example.rentandsharebikes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BikeStoreImageAddBikes extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ValueEventListener bikeStoreDBEventListener;

    private RecyclerView bikeStoreRecyclerView;
    private BikeStoreAdapterAddBikes bikeStoreAdapterAddBikes;

    private List<BikeStore> bikeStoreList;

    private ProgressDialog progressDialog;
    private Button buttonBackAddBikeStore;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_store_image_add_bikes);

        firebaseDatabase = FirebaseDatabase.getInstance();

        bikeStoreRecyclerView = (RecyclerView) findViewById(R.id.evRecyclerView);
        bikeStoreRecyclerView.setHasFixedSize(true);
        bikeStoreRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressDialog = new ProgressDialog(this);
        bikeStoreList = new ArrayList<>();

        progressDialog.show();

        //check if the events list is empty and add a new event
        if(databaseReference == null){
            databaseReference = FirebaseDatabase.getInstance().getReference("Bike stores");
        }

        bikeStoreDBEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    BikeStore bikeStore = postSnapshot.getValue(BikeStore.class);
                    assert bikeStore != null;
                    bikeStore.setStoreKey(postSnapshot.getKey());
                    bikeStoreList.add(bikeStore);
                }

                bikeStoreAdapterAddBikes = new BikeStoreAdapterAddBikes(BikeStoreImageAddBikes.this, bikeStoreList);
                bikeStoreRecyclerView.setAdapter(bikeStoreAdapterAddBikes);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
