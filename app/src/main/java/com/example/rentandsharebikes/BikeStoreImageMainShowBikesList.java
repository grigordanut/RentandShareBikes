package com.example.rentandsharebikes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BikeStoreImageMainShowBikesList extends AppCompatActivity{

    private DatabaseReference databaseReference;
    private ValueEventListener bikeStoreEventListener;

    private RecyclerView bikeStoreRecyclerView;
    private BikeStoreAdapterMainShowBikesList bikeStoreAdapterMainShowBikesList;

    private List<BikeStores> bikeStoresList;

    private TextView tVBikeStoresImgShowBikesListMain;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_store_image_main_show_bikes_list);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Bike Stores available Main");

        progressDialog = new ProgressDialog(this);
        progressDialog.show();

        tVBikeStoresImgShowBikesListMain = findViewById(R.id.tvBikeStoresImgShowBikesListMain);

        bikeStoreRecyclerView = (RecyclerView) findViewById(R.id.evRecyclerView);
        bikeStoreRecyclerView.setHasFixedSize(true);
        bikeStoreRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        bikeStoresList = new ArrayList<>();

        bikeStoreAdapterMainShowBikesList = new BikeStoreAdapterMainShowBikesList(BikeStoreImageMainShowBikesList.this, bikeStoresList);
        bikeStoreRecyclerView.setAdapter(bikeStoreAdapterMainShowBikesList);
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
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                bikeStoresList.clear();

                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                        BikeStores bikeStores = postSnapshot.getValue(BikeStores.class);
                        assert bikeStores != null;
                        bikeStores.setBikeStore_Key(postSnapshot.getKey());
                        bikeStoresList.add(bikeStores);
                    }

                    bikeStoreAdapterMainShowBikesList.notifyDataSetChanged();

                    if (bikeStoresList.size() == 1) {
                        tVBikeStoresImgShowBikesListMain.setText(bikeStoresList.size() + " Bike Store available");
                    }
                    else {
                        tVBikeStoresImgShowBikesListMain.setText(bikeStoresList.size() + " Bike Stores available");
                    }
                }
                else {
                    tVBikeStoresImgShowBikesListMain.setText("No Bike Stores available!!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikeStoreImageMainShowBikesList.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

        progressDialog.dismiss();
    }
}
