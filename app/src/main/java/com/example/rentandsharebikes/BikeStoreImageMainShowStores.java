package com.example.rentandsharebikes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
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

public class BikeStoreImageMainShowStores extends AppCompatActivity implements BikeStoreAdapterMain.OnItemClickListener {

    private DatabaseReference databaseReference;
    private ValueEventListener bikeStoreEventListener;

    private RecyclerView rVBikeStoreImgMain_ShowStores;
    private BikeStoreAdapterMain bikeStoreAdapterMain;

    private List<BikeStores> listMainBikeStores;

    private TextView tVBikeStoreImgMainShowStores;

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_store_image_main_show_stores);

        Objects.requireNonNull(getSupportActionBar()).setTitle("MAIN Bike Stores available");

        progressDialog = new ProgressDialog(this);

        //initialize the bike store database
        databaseReference = FirebaseDatabase.getInstance().getReference("Bike Stores");

        tVBikeStoreImgMainShowStores = findViewById(R.id.tvBikeStoreImgMainShowStores);

        rVBikeStoreImgMain_ShowStores = findViewById(R.id.rVBikeStoreImgMainShowStores);
        rVBikeStoreImgMain_ShowStores.setHasFixedSize(true);
        rVBikeStoreImgMain_ShowStores.setLayoutManager(new LinearLayoutManager(this));

        listMainBikeStores = new ArrayList<>();

        bikeStoreAdapterMain = new BikeStoreAdapterMain(BikeStoreImageMainShowStores.this, listMainBikeStores);
        rVBikeStoreImgMain_ShowStores.setAdapter(bikeStoreAdapterMain);
        bikeStoreAdapterMain.setOnItemClickListener(BikeStoreImageMainShowStores.this);
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

                    if (listMainBikeStores.size() == 1) {
                        tVBikeStoreImgMainShowStores.setText(listMainBikeStores.size() + " Bike Store available");
                    }
                    else {
                        tVBikeStoreImgMainShowStores.setText(listMainBikeStores.size() + " Bike Stores available");
                    }

                    bikeStoreAdapterMain.notifyDataSetChanged();
                }

                else {
                    tVBikeStoreImgMainShowStores.setText("No Bike Stores available!!");
                    bikeStoreAdapterMain.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikeStoreImageMainShowStores.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        progressDialog.dismiss();
    }

    //Action of the menu onClick
    @Override
    public void onItemClick(int position) {

        final String[] options = {"Show Google Maps", "Back Main Page"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, options);
        BikeStores selected_store = listMainBikeStores.get(position);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setCancelable(false)
                .setTitle("You selected " + selected_store.getBikeStore_Location() + " Store" + "\nSelect an option:")
                .setAdapter(adapter, (dialog, id) -> {

                    if (id == 0) {
                        Toast.makeText(BikeStoreImageMainShowStores.this, "Show Bikes Stores in Google Map", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(BikeStoreImageMainShowStores.this, MapsActivity.class));
                    }
                    if (id == 1) {
                        startActivity(new Intent(BikeStoreImageMainShowStores.this, MainActivity.class));
                        Toast.makeText(BikeStoreImageMainShowStores.this, "Back to main page", Toast.LENGTH_SHORT).show();
                    }
                })

                .setNegativeButton("CLOSE", (dialog, id) -> dialog.dismiss());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
