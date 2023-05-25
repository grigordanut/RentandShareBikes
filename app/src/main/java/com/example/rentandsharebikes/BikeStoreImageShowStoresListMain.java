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

public class BikeStoreImageShowStoresListMain extends AppCompatActivity implements BikeStoreAdapterMain.OnItemClickListener {

    private TextView tVBikeStoresImaShowStoreListMain;
    private DatabaseReference databaseReference;
    private ValueEventListener bikeStoreEventListener;

    private RecyclerView bikeStoreRecyclerView;
    private BikeStoreAdapterMain bikeStoreAdapterMain;

    private List<BikeStores> bikeStoresList;

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_store_image_show_stores_list_main);

        progressDialog = new ProgressDialog(this);
        progressDialog.show();

        tVBikeStoresImaShowStoreListMain = findViewById(R.id.tvBikeStoresImageShowStoreListMain);
        tVBikeStoresImaShowStoreListMain.setText("No Bike Stores available");

        bikeStoreRecyclerView = findViewById(R.id.evRecyclerView);
        bikeStoreRecyclerView.setHasFixedSize(true);
        bikeStoreRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        bikeStoresList = new ArrayList<>();

        bikeStoreAdapterMain = new BikeStoreAdapterMain(BikeStoreImageShowStoresListMain.this, bikeStoresList);
        bikeStoreRecyclerView.setAdapter(bikeStoreAdapterMain);
        bikeStoreAdapterMain.setOnItemClickListener(BikeStoreImageShowStoresListMain.this);
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
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    BikeStores bikeStores = postSnapshot.getValue(BikeStores.class);
                    assert bikeStores != null;
                    bikeStores.setBikeStore_Key(postSnapshot.getKey());
                    bikeStoresList.add(bikeStores);
                    tVBikeStoresImaShowStoreListMain.setText(bikeStoresList.size() + " Bike Stores available");
                }

                bikeStoreAdapterMain.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikeStoreImageShowStoresListMain.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
                        Toast.makeText(BikeStoreImageShowStoresListMain.this, "Show Bikes Stores in Google Map", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(BikeStoreImageShowStoresListMain.this, MapsActivity.class));
                    }
                    if (id == 1) {
                        startActivity(new Intent(BikeStoreImageShowStoresListMain.this, MainActivity.class));
                        Toast.makeText(BikeStoreImageShowStoresListMain.this, "Back to main page", Toast.LENGTH_SHORT).show();
                    }
                })

                .setNegativeButton("CLOSE", (dialog, id) -> dialog.dismiss());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
