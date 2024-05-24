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

public class BikeStoreImageMainShowStoresList extends AppCompatActivity implements BikeStoreAdapterMain.OnItemClickListener {

    private DatabaseReference databaseReference;
    private ValueEventListener bikeStoreEventListener;

    private RecyclerView bikeStoreRecyclerView;
    private BikeStoreAdapterMain bikeStoreAdapterMain;

    private List<BikeStores> bikeStoresList;

    private TextView tVBikeStoresImaShowStoreListMain;

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_store_image_main_show_stores_list);

        progressDialog = new ProgressDialog(this);
        progressDialog.show();

        tVBikeStoresImaShowStoreListMain = findViewById(R.id.tvBikeStoresImageShowStoreListMain);

        bikeStoreRecyclerView = findViewById(R.id.evRecyclerView);
        bikeStoreRecyclerView.setHasFixedSize(true);
        bikeStoreRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        bikeStoresList = new ArrayList<>();

        bikeStoreAdapterMain = new BikeStoreAdapterMain(BikeStoreImageMainShowStoresList.this, bikeStoresList);
        bikeStoreRecyclerView.setAdapter(bikeStoreAdapterMain);
        bikeStoreAdapterMain.setOnItemClickListener(BikeStoreImageMainShowStoresList.this);
    }

    @Override
    public void onStart() {
        super.onStart();
        loadBikeStoresListAdmin();
    }

    private void loadBikeStoresListAdmin() {

        //initialize the bike store database
        databaseReference = FirebaseDatabase.getInstance().getReference("Bike Stores");

        bikeStoreEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                bikeStoresList.clear();

                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        BikeStores bikeStores = postSnapshot.getValue(BikeStores.class);
                        assert bikeStores != null;
                        bikeStores.setBikeStore_Key(postSnapshot.getKey());
                        bikeStoresList.add(bikeStores);
                    }

                    bikeStoreAdapterMain.notifyDataSetChanged();

                    if (bikeStoresList.size() == 1) {
                        tVBikeStoresImaShowStoreListMain.setText(bikeStoresList.size() + " Bike Store available");
                    }
                    else {
                        tVBikeStoresImaShowStoreListMain.setText(bikeStoresList.size() + " Bike Stores available");
                    }
                }

                else {
                    tVBikeStoresImaShowStoreListMain.setText("No Bike Stores available!!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikeStoreImageMainShowStoresList.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        progressDialog.dismiss();
    }

    //Action of the menu onClick
    @Override
    public void onItemClick(int position) {

        final String[] options = {"Show Google Maps", "Back Main Page"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, options);
        BikeStores selected_store = bikeStoresList.get(position);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setCancelable(false)
                .setTitle("You selected " + selected_store.getBikeStore_Location() + " Store" + "\nSelect an option:")
                .setAdapter(adapter, (dialog, id) -> {

                    if (id == 0) {
                        Toast.makeText(BikeStoreImageMainShowStoresList.this, "Show Bikes Stores in Google Map", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(BikeStoreImageMainShowStoresList.this, MapsActivity.class));
                    }
                    if (id == 1) {
                        startActivity(new Intent(BikeStoreImageMainShowStoresList.this, MainActivity.class));
                        Toast.makeText(BikeStoreImageMainShowStoresList.this, "Back to main page", Toast.LENGTH_SHORT).show();
                    }
                })

                .setNegativeButton("CLOSE", (dialog, id) -> dialog.dismiss());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
