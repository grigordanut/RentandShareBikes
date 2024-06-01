package com.example.rentandsharebikes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
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

public class BikeStoreImageAdminAddBikes extends AppCompatActivity implements BikeStoreAdapterAdmin.OnItemClickListener {

    private DatabaseReference databaseReference;
    private ValueEventListener bikeStoreEventListener;

    private RecyclerView rvBikeStoreImgAdmin_AddBikes;
    private BikeStoreAdapterAdmin bikeStoreAdapterAdmin;

    private TextView tVBikeStoreImgAdminAddBikes;

    private List<BikeStores> listBikeStoresAddBikes;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_store_image_admin_add_bikes);

        Objects.requireNonNull(getSupportActionBar()).setTitle("ADMIN Bike Stores add bikes");

        progressDialog = new ProgressDialog(this);

        //initialize the bike store database
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Bike Stores");

        tVBikeStoreImgAdminAddBikes = findViewById(R.id.tvBikeStoreImgAdminAddBikes);

        rvBikeStoreImgAdmin_AddBikes= findViewById(R.id.rvBikeStoreImgAdminAddBikes);
        rvBikeStoreImgAdmin_AddBikes.setHasFixedSize(true);
        rvBikeStoreImgAdmin_AddBikes.setLayoutManager(new LinearLayoutManager(this));

        listBikeStoresAddBikes = new ArrayList<>();

        bikeStoreAdapterAdmin = new BikeStoreAdapterAdmin(BikeStoreImageAdminAddBikes.this, listBikeStoresAddBikes);
        rvBikeStoreImgAdmin_AddBikes.setAdapter(bikeStoreAdapterAdmin);
        bikeStoreAdapterAdmin.setOnItmClickListener(BikeStoreImageAdminAddBikes.this);
    }

    @Override
    public void onStart() {
        super.onStart();
        loadBikeStoresListAdmin();
    }

    private void loadBikeStoresListAdmin() {

        progressDialog.show();

        bikeStoreEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                listBikeStoresAddBikes.clear();

                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        BikeStores bikeStores = postSnapshot.getValue(BikeStores.class);
                        assert bikeStores != null;
                        bikeStores.setBikeStore_Key(postSnapshot.getKey());
                        listBikeStoresAddBikes.add(bikeStores);
                    }

                    tVBikeStoreImgAdminAddBikes.setText("Select the Bike Store");
                    bikeStoreAdapterAdmin.notifyDataSetChanged();
                }

                else {
                    tVBikeStoreImgAdminAddBikes.setText("No Bike Stores available!!");
                    bikeStoreAdapterAdmin.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikeStoreImageAdminAddBikes.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        progressDialog.dismiss();
    }

    @Override
    public void onItemClick(int position) {
        BikeStores selected_Store = listBikeStoresAddBikes.get(position);
        Intent store_Intent = new Intent(BikeStoreImageAdminAddBikes.this, AddBikeRent.class);
        store_Intent.putExtra("SName", selected_Store.getBikeStore_Location());
        store_Intent.putExtra("SKey", selected_Store.getBikeStore_Key());
        startActivity(store_Intent);
    }
}
