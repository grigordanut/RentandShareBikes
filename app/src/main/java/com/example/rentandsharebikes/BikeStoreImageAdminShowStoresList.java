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
import android.view.MenuItem;
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

public class BikeStoreImageAdminShowStoresList extends AppCompatActivity implements BikeStoreAdapterAdminShowStoresList.OnItemClickListener {

    //Display Bike stores available
    private DatabaseReference dbRefStoresAv;
    private ValueEventListener bikeStoreEventListener;

    private RecyclerView bikeStoreRecyclerView;
    private BikeStoreAdapterAdminShowStoresList bikeStoreAdapterAdminShowStoresList;

    private TextView tVListBikeStoresAdmin;

    public List<BikeStores> bikeStoresList;

    private ProgressDialog progressDialog;

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_store_image_admin_show_stores_list);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Bike Stores available Admin");

        progressDialog = new ProgressDialog(BikeStoreImageAdminShowStoresList.this);

        tVListBikeStoresAdmin = findViewById(R.id.tvListBikeStoresAdmin);

        bikeStoreRecyclerView = findViewById(R.id.evRecyclerView);
        bikeStoreRecyclerView.setHasFixedSize(true);
        bikeStoreRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        bikeStoresList = new ArrayList<BikeStores>();

        bikeStoreAdapterAdminShowStoresList = new BikeStoreAdapterAdminShowStoresList(BikeStoreImageAdminShowStoresList.this, bikeStoresList);
        bikeStoreRecyclerView.setAdapter(bikeStoreAdapterAdminShowStoresList);
        bikeStoreAdapterAdminShowStoresList.setOnItemClickListener(BikeStoreImageAdminShowStoresList.this);
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

                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        BikeStores bikeStores = postSnapshot.getValue(BikeStores.class);
                        assert bikeStores != null;
                        bikeStores.setBikeStore_Key(postSnapshot.getKey());
                        bikeStoresList.add(bikeStores);
                    }

                    bikeStoreAdapterAdminShowStoresList.notifyDataSetChanged();

                    if (bikeStoresList.size() == 1) {
                        tVListBikeStoresAdmin.setText(bikeStoresList.size() + " Bike Store available");
                    }

                    else {
                        tVListBikeStoresAdmin.setText(bikeStoresList.size() + " Bike Stores available");
                    }
                }
                else {
                    tVListBikeStoresAdmin.setText("No Bike Stores available!!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikeStoreImageAdminShowStoresList.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        progressDialog.dismiss();
    }

    //Action of the menu onClick
    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, "Press long click to show more actions: ", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onShowMapStoreClick(int position) {
        startActivity(new Intent(BikeStoreImageAdminShowStoresList.this, MapsActivity.class));
    }

    @Override
    public void onUpdateStoreClick(int position) {

        Intent intent = new Intent(BikeStoreImageAdminShowStoresList.this, UpdateBikeStoreDetails.class);
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

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BikeStoreImageAdminShowStoresList.this);
        alertDialogBuilder
                .setMessage("Are sure to delete " + selected_BikeStores.getBikeStore_Location() + " Bike Store?")
                .setCancelable(false)
                .setPositiveButton("YES",
                        (dialog, id) -> {
                            String selected_StoreKey = selected_BikeStores.getBikeStore_Key();
                            dbRefStoresAv.child(selected_StoreKey).removeValue();
                            Toast.makeText(BikeStoreImageAdminShowStoresList.this, "The Bike Store " + selected_BikeStores.getBikeStore_Location() + " has been successfully deleted.", Toast.LENGTH_SHORT).show();
                        })

                .setNegativeButton("CANCEL", (dialog, id) -> dialog.cancel());

        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    @Override
    public void alertDialogBikeStoreNotEmpty(final int position) {
        BikeStores selectedBikeStores = bikeStoresList.get(position);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BikeStoreImageAdminShowStoresList.this);
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

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bike_store_image_admin_show_stores_list, menu);
        return true;
    }

    public void menuStoreImgAdminShowStoreGoBack() {
        startActivity(new Intent(BikeStoreImageAdminShowStoresList.this, AdminPage.class));
        finish();
    }

    public void menuStoreImgAdminShowStoreAddStore() {
        startActivity(new Intent(BikeStoreImageAdminShowStoresList.this, CalculateCoordinates.class));
        finish();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menuStoreImgAdminShowStore_goBack) {
            menuStoreImgAdminShowStoreGoBack();
        }

        if (item.getItemId() == R.id.menuStoreImgAdminShowStore_addStore) {
            menuStoreImgAdminShowStoreAddStore();
        }

        return super.onOptionsItemSelected(item);
    }
}