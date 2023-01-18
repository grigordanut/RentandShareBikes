package com.example.rentandsharebikes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
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

public class BikeStoreImageShowBikesListMain extends AppCompatActivity{

    private DatabaseReference databaseReference;
    private ValueEventListener bikeStoreEventListener;

    private RecyclerView bikeStoreRecyclerView;
    private BikeStoreAdapterShowBikesListMain bikeStoreAdapterShowBikesListMain;

    private List<BikeStores> bikeStoresList;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_store_image_show_bikes_list_main);

        progressDialog = new ProgressDialog(this);
        progressDialog.show();

        bikeStoreRecyclerView = (RecyclerView) findViewById(R.id.evRecyclerView);
        bikeStoreRecyclerView.setHasFixedSize(true);
        bikeStoreRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        bikeStoresList = new ArrayList<>();

        bikeStoreAdapterShowBikesListMain = new BikeStoreAdapterShowBikesListMain(BikeStoreImageShowBikesListMain.this, bikeStoresList);
        bikeStoreRecyclerView.setAdapter(bikeStoreAdapterShowBikesListMain);
    }

    @Override
    public void onStart() {
        super.onStart();
        loadBikeStoresListAdmin();
    }

    private void loadBikeStoresListAdmin(){
        //initialize the bike store database
        databaseReference = FirebaseDatabase.getInstance().getReference("Bike Stores");

        bikeStoreEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bikeStoresList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    BikeStores bikeStores = postSnapshot.getValue(BikeStores.class);
                    assert bikeStores != null;
                    bikeStores.setBikeStore_Key(postSnapshot.getKey());
                    bikeStoresList.add(bikeStores);
                }

                bikeStoreAdapterShowBikesListMain.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikeStoreImageShowBikesListMain.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}
