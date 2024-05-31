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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
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

public class BikeStoreImageAdminShowStores extends AppCompatActivity implements BikeStoreAdapterAdminShowStores.OnItemClickListener {

    //Display Bike stores available
    private DatabaseReference dbRefStoresAv;
    private ValueEventListener bikeStoreEventListener;

    private RecyclerView rvBikeStoreImgAdmin_ShowStores;
    private BikeStoreAdapterAdminShowStores bikeStoreAdapterAdminShowStores;

    private TextView tVBikeStoreImgAdminShowStores;

    public List<BikeStores> bikeStoresListAdmin;

    private ProgressDialog progressDialog;

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_store_image_admin_show_stores);

        Objects.requireNonNull(getSupportActionBar()).setTitle("ADMIN Bike Stores available");

        progressDialog = new ProgressDialog(BikeStoreImageAdminShowStores.this);

        //initialize the bike store database
        dbRefStoresAv = FirebaseDatabase.getInstance().getReference().child("Bike Stores");

        tVBikeStoreImgAdminShowStores = findViewById(R.id.tvBikeStoreImgAdminShowStores);

        rvBikeStoreImgAdmin_ShowStores = findViewById(R.id.rvBikeStoreImgAdminShowStores);
        rvBikeStoreImgAdmin_ShowStores.setHasFixedSize(true);
        rvBikeStoreImgAdmin_ShowStores.setLayoutManager(new LinearLayoutManager(this));

        bikeStoresListAdmin = new ArrayList<>();

        bikeStoreAdapterAdminShowStores = new BikeStoreAdapterAdminShowStores(BikeStoreImageAdminShowStores.this, bikeStoresListAdmin);
        rvBikeStoreImgAdmin_ShowStores.setAdapter(bikeStoreAdapterAdminShowStores);
        bikeStoreAdapterAdminShowStores.setOnItemClickListener(BikeStoreImageAdminShowStores.this);
    }

    @Override
    public void onStart() {
        super.onStart();
        loadBikeStoresListAdmin();
    }

    private void loadBikeStoresListAdmin() {

        progressDialog.show();

        bikeStoreEventListener = dbRefStoresAv.addValueEventListener(new ValueEventListener() {

            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                bikeStoresListAdmin.clear();

                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        BikeStores bikeStores = postSnapshot.getValue(BikeStores.class);
                        assert bikeStores != null;
                        bikeStores.setBikeStore_Key(postSnapshot.getKey());
                        bikeStoresListAdmin.add(bikeStores);
                    }

                    if (bikeStoresListAdmin.size() == 1) {
                        tVBikeStoreImgAdminShowStores.setText(bikeStoresListAdmin.size() + " Bike Store available");
                    }

                    else {
                        tVBikeStoreImgAdminShowStores.setText(bikeStoresListAdmin.size() + " Bike Stores available");
                    }

                    bikeStoreAdapterAdminShowStores.notifyDataSetChanged();
                }

                else {
                    tVBikeStoreImgAdminShowStores.setText("No Bike Stores available!!");
                    bikeStoreAdapterAdminShowStores.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikeStoreImageAdminShowStores.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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
        startActivity(new Intent(BikeStoreImageAdminShowStores.this, MapsActivity.class));
    }

    @Override
    public void onUpdateStoreClick(int position) {

        Intent intent = new Intent(BikeStoreImageAdminShowStores.this, UpdateBikeStoreDetails.class);
        BikeStores selected_BikeStores = bikeStoresListAdmin.get(position);
        intent.putExtra("SLocation", selected_BikeStores.getBikeStore_Location());
        intent.putExtra("SAddress", selected_BikeStores.getBikeStore_Address());
        intent.putExtra("SLatitude", String.valueOf(selected_BikeStores.getBikeStore_Latitude()));
        intent.putExtra("SLongitude", String.valueOf(selected_BikeStores.getBikeStore_Longitude()));
        intent.putExtra("SNoSlots", String.valueOf(selected_BikeStores.getBikeStore_NumberSlots()));
        intent.putExtra("SKey", selected_BikeStores.getBikeStore_Key());
        startActivity(intent);
    }

    //Action of the menu Delete and alert dialog
    @SuppressLint("SetTextI18n")
    @Override
    public void onDeleteStoreClick(int position) {

        BikeStores selected_BikeStores = bikeStoresListAdmin.get(position);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BikeStoreImageAdminShowStores.this);
        alertDialogBuilder
                .setMessage("Are sure to delete " + selected_BikeStores.getBikeStore_Location() + " Bike Store?")
                .setCancelable(false)
                .setPositiveButton("YES",
                        (dialog, id) -> {
                            String selected_StoreKey = selected_BikeStores.getBikeStore_Key();
                            dbRefStoresAv.child(selected_StoreKey).removeValue();
                            LayoutInflater inflater = getLayoutInflater();

                            @SuppressLint("InflateParams") View layout = inflater.inflate(R.layout.toast, null);
                            TextView text = layout.findViewById(R.id.tvToast);
                            ImageView imageView = layout.findViewById(R.id.imgToast);
                            text.setText("The Bike Store has been successfully deleted.");
                            imageView.setImageResource(R.drawable.baseline_delete_forever_24);
                            Toast toast = new Toast(getApplicationContext());
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.setView(layout);
                            toast.show();
                            Toast.makeText(BikeStoreImageAdminShowStores.this, "The Bike Store " + selected_BikeStores.getBikeStore_Location() + " has been successfully deleted.", Toast.LENGTH_SHORT).show();
                        })

                .setNegativeButton("CANCEL", (dialog, id) -> dialog.cancel());

        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    @Override
    public void alertDialogBikeStoreNotEmpty(final int position) {
        BikeStores selectedBikeStores = bikeStoresListAdmin.get(position);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BikeStoreImageAdminShowStores.this);
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
        startActivity(new Intent(BikeStoreImageAdminShowStores.this, AdminPage.class));
        finish();
    }

    public void menuStoreImgAdminShowStoreAddStore() {
        startActivity(new Intent(BikeStoreImageAdminShowStores.this, CalculateCoordinates.class));
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