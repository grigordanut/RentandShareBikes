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

public class BikeStoreImageAdminShowBikes extends AppCompatActivity implements BikeStoreAdapterAdmin.OnItemClickListener {

    //Display the Bike Stores available
    private DatabaseReference dbRefBikeStores;
    private ValueEventListener bikeStoreEventListener;

    private RecyclerView rvBikeStoreImgAdmin_ShowBikes;
    private BikeStoreAdapterAdmin bikeStoreAdapterAdmin;

    private TextView tVBikeStoreImgAdminShowBikes;

    private List<BikeStores> listBikeStoresShowBikes;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_store_image_admin_show_bikes);

        Objects.requireNonNull(getSupportActionBar()).setTitle("ADMIN Bike Stores show bikes");

        progressDialog = new ProgressDialog(this);

        //initialize the bike store database
        dbRefBikeStores = FirebaseDatabase.getInstance().getReference("Bike Stores");

        tVBikeStoreImgAdminShowBikes = findViewById(R.id.tvBikeStoreImgAdminShowBikes);

        rvBikeStoreImgAdmin_ShowBikes = findViewById(R.id.rvBikeStoreImgAdminShowBikes);
        rvBikeStoreImgAdmin_ShowBikes.setHasFixedSize(true);
        rvBikeStoreImgAdmin_ShowBikes.setLayoutManager(new LinearLayoutManager(this));

        listBikeStoresShowBikes = new ArrayList<>();

        bikeStoreAdapterAdmin = new BikeStoreAdapterAdmin(BikeStoreImageAdminShowBikes.this, listBikeStoresShowBikes);
        rvBikeStoreImgAdmin_ShowBikes.setAdapter(bikeStoreAdapterAdmin);
        bikeStoreAdapterAdmin.setOnItmClickListener(BikeStoreImageAdminShowBikes.this);
    }

    @Override
    public void onStart() {
        super.onStart();
        loadBikeStoresListAdmin();
    }

    private void loadBikeStoresListAdmin() {

        progressDialog.show();

        bikeStoreEventListener = dbRefBikeStores.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                listBikeStoresShowBikes.clear();

                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        BikeStores bikeStores = postSnapshot.getValue(BikeStores.class);
                        assert bikeStores != null;
                        bikeStores.setBikeStore_Key(postSnapshot.getKey());
                        listBikeStoresShowBikes.add(bikeStores);
                        tVBikeStoreImgAdminShowBikes.setText("Select the Bike Store");
                    }

                    bikeStoreAdapterAdmin.notifyDataSetChanged();
                }

                else {
                    tVBikeStoreImgAdminShowBikes.setText("No Bike Stores available!!");
                    bikeStoreAdapterAdmin.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikeStoreImageAdminShowBikes.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        progressDialog.dismiss();
    }

    @Override
    public void onItemClick(int position) {
        BikeStores selected_Store = listBikeStoresShowBikes.get(position);
        Intent store_Intent = new Intent(BikeStoreImageAdminShowBikes.this, BikeImageAdminShowBikes.class);
        store_Intent.putExtra("SName", selected_Store.getBikeStore_Location());
        store_Intent.putExtra("SKey", selected_Store.getBikeStore_Key());
        startActivity(store_Intent);
    }
}
