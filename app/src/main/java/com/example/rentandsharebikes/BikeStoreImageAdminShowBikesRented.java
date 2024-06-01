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

public class BikeStoreImageAdminShowBikesRented extends AppCompatActivity implements BikeStoreAdpterAdminShowBikesRented.OnItemClickListener{

    private DatabaseReference databaseReference;
    private ValueEventListener bikeStoreEventListener;

    private RecyclerView rvBikeStoreImgAdmin_ShowBikesRented;
    private BikeStoreAdpterAdminShowBikesRented bikeStoreAdpterAdminShowBikesRented;

    private List<BikeStores> listStoresShowBikesRented;

    private TextView tVBikeStoreImgAdminShowBikesRented;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_store_image_admin_show_bikes_rented);

        Objects.requireNonNull(getSupportActionBar()).setTitle("ADMIN Stores show bikes rented");

        progressDialog = new ProgressDialog(this);

        //initialize the bike store database
        databaseReference = FirebaseDatabase.getInstance().getReference("Bike Stores");

        tVBikeStoreImgAdminShowBikesRented = findViewById(R.id.tvBikeStoreImgAdminShowBikesRented);

        rvBikeStoreImgAdmin_ShowBikesRented = findViewById(R.id.rvBikeStoreImgAdminShowBikesRented);
        rvBikeStoreImgAdmin_ShowBikesRented.setHasFixedSize(true);
        rvBikeStoreImgAdmin_ShowBikesRented.setLayoutManager(new LinearLayoutManager(this));

        listStoresShowBikesRented = new ArrayList<>();

        bikeStoreAdpterAdminShowBikesRented = new BikeStoreAdpterAdminShowBikesRented(BikeStoreImageAdminShowBikesRented.this, listStoresShowBikesRented);
        rvBikeStoreImgAdmin_ShowBikesRented.setAdapter(bikeStoreAdpterAdminShowBikesRented);
        bikeStoreAdpterAdminShowBikesRented.setOnItmClickListener(BikeStoreImageAdminShowBikesRented.this);
    }

    @Override
    public void onStart() {
        super.onStart();
        loadBikeStoresListAdmin();
    }

    private void loadBikeStoresListAdmin(){

        progressDialog.show();

        bikeStoreEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                listStoresShowBikesRented.clear();

                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                        BikeStores bikeStores = postSnapshot.getValue(BikeStores.class);
                        assert bikeStores != null;
                        bikeStores.setBikeStore_Key(postSnapshot.getKey());
                        listStoresShowBikesRented.add(bikeStores);
                    }

                    tVBikeStoreImgAdminShowBikesRented.setText("Select the Bike Store");
                    bikeStoreAdpterAdminShowBikesRented.notifyDataSetChanged();
                }

                else {
                    tVBikeStoreImgAdminShowBikesRented.setText("No Bike Stores available");
                    bikeStoreAdpterAdminShowBikesRented.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikeStoreImageAdminShowBikesRented.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

        progressDialog.dismiss();
    }

    @Override
    public void onItemClick(int position) {
        BikeStores selected_Store = listStoresShowBikesRented.get(position);
        Intent store_Intent = new Intent(BikeStoreImageAdminShowBikesRented.this, BikeImageAdminShowBikesRented.class);
        store_Intent.putExtra("SName", selected_Store.getBikeStore_Location());
        store_Intent.putExtra("SKey",selected_Store.getBikeStore_Key());
        startActivity(store_Intent);
    }
}