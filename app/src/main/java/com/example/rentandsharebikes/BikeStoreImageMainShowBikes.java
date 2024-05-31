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

public class BikeStoreImageMainShowBikes extends AppCompatActivity implements BikeStoreAdapterMain.OnItemClickListener {

    private DatabaseReference databaseReference;
    private ValueEventListener bikeStoreEventListener;

    private RecyclerView rVBikeStoreImgMain_ShowBikes;
    private BikeStoreAdapterMain bikeStoreAdapterMain;

    private List<BikeStores> listMainBikeStores;

    private TextView tVBikeStoreImgMainShowBikes;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_store_image_main_show_bikes);

        Objects.requireNonNull(getSupportActionBar()).setTitle("MAIN Bikes Stores show bikes");

        //initialize the bike store database
        databaseReference = FirebaseDatabase.getInstance().getReference("Bike Stores");

        progressDialog = new ProgressDialog(this);

        tVBikeStoreImgMainShowBikes = findViewById(R.id.tvBikeStoreImgMainShowBikes);

        rVBikeStoreImgMain_ShowBikes = (RecyclerView) findViewById(R.id.rVBikeStoreImgMainShowBikes);
        rVBikeStoreImgMain_ShowBikes.setHasFixedSize(true);
        rVBikeStoreImgMain_ShowBikes.setLayoutManager(new LinearLayoutManager(this));

        listMainBikeStores = new ArrayList<>();

        bikeStoreAdapterMain = new BikeStoreAdapterMain(BikeStoreImageMainShowBikes.this, listMainBikeStores);
        rVBikeStoreImgMain_ShowBikes.setAdapter(bikeStoreAdapterMain);
        bikeStoreAdapterMain.setOnItemClickListener(BikeStoreImageMainShowBikes.this);
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

                listMainBikeStores.clear();

                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        BikeStores bikeStores = postSnapshot.getValue(BikeStores.class);
                        assert bikeStores != null;
                        bikeStores.setBikeStore_Key(postSnapshot.getKey());
                        listMainBikeStores.add(bikeStores);
                    }

                    tVBikeStoreImgMainShowBikes.setText("Select the Bike Store");
                    bikeStoreAdapterMain.notifyDataSetChanged();
                }

                else {
                    tVBikeStoreImgMainShowBikes.setText("No Bike Stores available!!");
                    bikeStoreAdapterMain.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikeStoreImageMainShowBikes.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        progressDialog.dismiss();
    }

    @Override
    public void onItemClick(int position) {
        BikeStores selected_Store = listMainBikeStores.get(position);
        Intent intent = new Intent(BikeStoreImageMainShowBikes.this, BikeImageMainShowBikes.class);
        intent.putExtra("SNameMain", selected_Store.getBikeStore_Location());
        intent.putExtra("SKeyMain", selected_Store.getBikeStore_Key());
        startActivity(intent);
    }
}
