package com.example.rentandsharebikes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BikeStoreImageShowBikesListAdmin extends AppCompatActivity implements BikeStoreAdapterAdmin.OnItemClickListener {

    //Display the Bike Stores available
    private DatabaseReference dbRefBikeStores;
    private ValueEventListener bikeStoreEventListener;

    private RecyclerView bikeStoreRecyclerView;
    private BikeStoreAdapterAdmin bikeStoreAdapterAdmin;

    private TextView tVBikeStoresImage;

    private List<BikeStores> bikeStoresList;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_store_image_show_bikes_list_admin);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Bike Stores available");

        progressDialog = new ProgressDialog(this);
        progressDialog.show();

        tVBikeStoresImage = findViewById(R.id.tvBikeStoresImage);

        bikeStoreRecyclerView = findViewById(R.id.evRecyclerView);
        bikeStoreRecyclerView.setHasFixedSize(true);
        bikeStoreRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        bikeStoresList = new ArrayList<>();

        bikeStoreAdapterAdmin = new BikeStoreAdapterAdmin(BikeStoreImageShowBikesListAdmin.this, bikeStoresList);
        bikeStoreRecyclerView.setAdapter(bikeStoreAdapterAdmin);
        bikeStoreAdapterAdmin.setOnItmClickListener(BikeStoreImageShowBikesListAdmin.this);
    }

    @Override
    public void onStart() {
        super.onStart();
        loadBikeStoresListAdmin();
    }

    private void loadBikeStoresListAdmin() {

        //initialize the bike store database
        dbRefBikeStores = FirebaseDatabase.getInstance().getReference("Bike Stores");

        bikeStoreEventListener = dbRefBikeStores.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bikeStoresList.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        BikeStores bikeStores = postSnapshot.getValue(BikeStores.class);
                        assert bikeStores != null;
                        bikeStores.setBikeStore_Key(postSnapshot.getKey());
                        bikeStoresList.add(bikeStores);
                        tVBikeStoresImage.setText("Select the Bike Store");
                    }

                    bikeStoreAdapterAdmin.notifyDataSetChanged();
                } else {
                    tVBikeStoresImage.setText("No Bike Stores were found!!");
                }

                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikeStoreImageShowBikesListAdmin.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        BikeStores selected_Store = bikeStoresList.get(position);
        Intent store_Intent = new Intent(BikeStoreImageShowBikesListAdmin.this, BikeImageShowBikesListAdmin.class);
        store_Intent.putExtra("SName", selected_Store.getBikeStore_Location());
        store_Intent.putExtra("SKey", selected_Store.getBikeStore_Key());
        startActivity(store_Intent);
    }
}
