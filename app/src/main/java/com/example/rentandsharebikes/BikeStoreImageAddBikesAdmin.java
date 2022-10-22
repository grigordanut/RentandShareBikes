package com.example.rentandsharebikes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BikeStoreImageAddBikesAdmin extends AppCompatActivity implements BikeStoreAdapterAdmin.OnItemClickListener {
    private DatabaseReference databaseReference;
    private ValueEventListener bikeStoreEventListener;

    private RecyclerView bikeStoreRecyclerView;
    private BikeStoreAdapterAdmin bikeStoreAdapterAdmin;

    private List<BikeStores> bikeStoresList;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_store_image_add_bikes_admin);

        progressDialog = new ProgressDialog(this);
        progressDialog.show();

        bikeStoreRecyclerView = findViewById(R.id.evRecyclerView);
        bikeStoreRecyclerView.setHasFixedSize(true);
        bikeStoreRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        bikeStoresList = new ArrayList<>();

        bikeStoreAdapterAdmin = new BikeStoreAdapterAdmin(BikeStoreImageAddBikesAdmin.this, bikeStoresList);
        bikeStoreRecyclerView.setAdapter(bikeStoreAdapterAdmin);
        bikeStoreAdapterAdmin.setOnItmClickListener(BikeStoreImageAddBikesAdmin.this);
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
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    BikeStores bikeStores = postSnapshot.getValue(BikeStores.class);
                    assert bikeStores != null;
                    bikeStores.setBikeStore_Key(postSnapshot.getKey());
                    bikeStoresList.add(bikeStores);
                }

                bikeStoreAdapterAdmin.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikeStoreImageAddBikesAdmin.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        BikeStores selected_Store = bikeStoresList.get(position);
        Intent store_Intent = new Intent(BikeStoreImageAddBikesAdmin.this, AddBikeRent.class);
        store_Intent.putExtra("SName", selected_Store.getBikeStore_Location());
        store_Intent.putExtra("SKey", selected_Store.getBikeStore_Key());
        startActivity(store_Intent);
    }
}
