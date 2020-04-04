package com.example.rentandsharebikes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BikeStoreImageShowStoresListCustomer extends AppCompatActivity implements BikeStoreAdapterShowStoresListCustomer.OnItemClickListener{

    private DatabaseReference databaseReference;
    private ValueEventListener bikeStoreDBEventListener;

    private RecyclerView bikeStoreRecyclerView;
    private BikeStoreAdapterShowStoresListCustomer bikeStoreAdapterShowStoresListCustomer;

    public List<BikeStore> bikeStoreList;

    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_store_image_show_stores_list_customer);

        bikeStoreRecyclerView = (RecyclerView) findViewById(R.id.evRecyclerView);
        bikeStoreRecyclerView.setHasFixedSize(true);
        bikeStoreRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressDialog = new ProgressDialog(this);
        bikeStoreList = new ArrayList<BikeStore>();

        progressDialog.show();
    }

    //Action of the menu onClick
    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, "Press long click to show more action: ", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onShowMapStoreClick(int position) {
        startActivity(new Intent(BikeStoreImageShowStoresListCustomer.this, MapsActivity.class));
    }

    @Override
    public void onUpdateStoreClick(int position) {

        Intent intent = new Intent(BikeStoreImageShowStoresListCustomer.this, UpdateBikeStoreDetails.class);
        BikeStore selected_BikeStore = bikeStoreList.get(position);
        intent.putExtra("SNumber", selected_BikeStore.getBikeStore_Number());
        intent.putExtra("SLocation", selected_BikeStore.getBikeStore_Location());
        intent.putExtra("SAddress", selected_BikeStore.getBikeStore_Address());
        intent.putExtra("SNrSlots", selected_BikeStore.getBikeStore_NumberSlots());
        startActivity(intent);
    }

    private void loadBikeStoresList() {
        //initialize the bikeStore database

        databaseReference = FirebaseDatabase.getInstance().getReference("Bike Stores");

        bikeStoreDBEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bikeStoreList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    BikeStore bikeStore = postSnapshot.getValue(BikeStore.class);
                    assert bikeStore != null;
                    bikeStore.setStoreKey(postSnapshot.getKey());
                    bikeStoreList.add(bikeStore);
                }

                bikeStoreAdapterShowStoresListCustomer = new BikeStoreAdapterShowStoresListCustomer(BikeStoreImageShowStoresListCustomer.this, bikeStoreList);
                bikeStoreRecyclerView.setAdapter(bikeStoreAdapterShowStoresListCustomer);
                bikeStoreAdapterShowStoresListCustomer.setOnItmClickListener(BikeStoreImageShowStoresListCustomer.this);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikeStoreImageShowStoresListCustomer.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        loadBikeStoresList();
    }
}
