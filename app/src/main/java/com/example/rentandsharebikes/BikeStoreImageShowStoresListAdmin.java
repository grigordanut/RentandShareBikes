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
import android.view.View;
import android.widget.Button;
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

public class BikeStoreImageShowStoresListAdmin extends AppCompatActivity implements BikeStoreAdapterShowStoresListAdmin.OnItemClickListener {

    //Display Bike stores available
    private DatabaseReference dbRefStoresAv;
    private ValueEventListener bikeStoreEventListener;

    private RecyclerView bikeStoreRecyclerView;
    private BikeStoreAdapterShowStoresListAdmin bikeStoreAdapterShowStoresListAdmin;

    private TextView tVListBikeStoresAdmin;

    private Button buttonAddMoreStores, buttonBackAdminPageStore;

    public List<BikeStores> bikeStoresList;

    private ProgressDialog progressDialog;

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_store_image_show_stores_list_admin);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Bike Stores available Admin");

        progressDialog = new ProgressDialog(BikeStoreImageShowStoresListAdmin.this);

        tVListBikeStoresAdmin = findViewById(R.id.tvListBikeStoresAdmin);

        buttonAddMoreStores = findViewById(R.id.btnAddMoreStores);
        buttonBackAdminPageStore = findViewById(R.id.btnBackAdminPageStore);

        bikeStoreRecyclerView = findViewById(R.id.evRecyclerView);
        bikeStoreRecyclerView.setHasFixedSize(true);
        bikeStoreRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        bikeStoresList = new ArrayList<BikeStores>();

        bikeStoreAdapterShowStoresListAdmin = new BikeStoreAdapterShowStoresListAdmin(BikeStoreImageShowStoresListAdmin.this, bikeStoresList);
        bikeStoreRecyclerView.setAdapter(bikeStoreAdapterShowStoresListAdmin);
        bikeStoreAdapterShowStoresListAdmin.setOnItemClickListener(BikeStoreImageShowStoresListAdmin.this);

        buttonAddMoreStores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BikeStoreImageShowStoresListAdmin.this, CalculateCoordinates.class));
            }
        });

        buttonBackAdminPageStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BikeStoreImageShowStoresListAdmin.this, AdminPage.class));
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        loadBikeStoresListAdmin();
    }

    private void loadBikeStoresListAdmin() {

        progressDialog.show();

        //initialize the bike store database
        dbRefStoresAv = FirebaseDatabase.getInstance().getReference().child("Bike Stores");

        bikeStoreEventListener = dbRefStoresAv.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                bikeStoresList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    BikeStores bikeStores = postSnapshot.getValue(BikeStores.class);
                    assert bikeStores != null;
                    bikeStores.setBikeStore_Key(postSnapshot.getKey());
                    bikeStoresList.add(bikeStores);
                    tVListBikeStoresAdmin.setText(bikeStoresList.size() + " Bike Stores available");
                }

                bikeStoreAdapterShowStoresListAdmin.notifyDataSetChanged();
                tVListBikeStoresAdmin.setText(bikeStoresList.size() + " Bike Stores available");
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikeStoreImageShowStoresListAdmin.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Action of the menu onClick
    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, "Press long click to show more actions: ", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onShowMapStoreClick(int position) {
        startActivity(new Intent(BikeStoreImageShowStoresListAdmin.this, MapsActivity.class));
    }

    @Override
    public void onUpdateStoreClick(int position) {

        Intent intent = new Intent(BikeStoreImageShowStoresListAdmin.this, UpdateBikeStoreDetails.class);
        BikeStores selected_BikeStores = bikeStoresList.get(position);
        intent.putExtra("SLocation", selected_BikeStores.getBikeStore_Location());
        intent.putExtra("SAddress", selected_BikeStores.getBikeStore_Address());
        intent.putExtra("SLatitude", String.valueOf(selected_BikeStores.getBikeStore_Latitude()));
        intent.putExtra("SLongitude", String.valueOf(selected_BikeStores.getBikeStore_Longitude()));
        intent.putExtra("SNoSlots", String.valueOf(selected_BikeStores.getBikeStore_NumberSlots()));
        intent.putExtra("SKey", selected_BikeStores.getBikeStore_Key());
        startActivity(intent);
    }

    //Action of the menu Delete and alert dialog
    @Override
    public void onDeleteStoreClick(int position) {

        BikeStores selected_BikeStores = bikeStoresList.get(position);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BikeStoreImageShowStoresListAdmin.this);
        alertDialogBuilder
                .setMessage("Are sure to delete " + selected_BikeStores.getBikeStore_Location() + " Bike Store?")
                .setCancelable(false)
                .setPositiveButton("YES",
                        (dialog, id) -> {
                            String selected_StoreKey = selected_BikeStores.getBikeStore_Key();
                            dbRefStoresAv.child(selected_StoreKey).removeValue();
                            Toast.makeText(BikeStoreImageShowStoresListAdmin.this, "The Bike Store " + selected_BikeStores.getBikeStore_Location() + " has been successfully deleted.", Toast.LENGTH_SHORT).show();
                        })

                .setNegativeButton("CANCEL", (dialog, id) -> dialog.cancel());

        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    @Override
    public void alertDialogBikeStoreNotEmpty(final int position) {
        BikeStores selectedBikeStores = bikeStoresList.get(position);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BikeStoreImageShowStoresListAdmin.this);
        alertDialogBuilder
                .setMessage("The " + selectedBikeStores.getBikeStore_Location() + " Bike Store still has bikes and cannot be deleted \nDelete the Bikes first and after delete the Bike Store")
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, id) -> dialog.dismiss());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbRefStoresAv.removeEventListener(bikeStoreEventListener);
    }
}