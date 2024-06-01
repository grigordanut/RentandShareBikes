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

public class BikeStoreImageCustomerShowStores extends AppCompatActivity implements BikeStoreAdapterCustomer.OnItemClickListener {

    private DatabaseReference databaseReference;
    private ValueEventListener bikeStoreEventListener;

    private RecyclerView rvBikeStoreImgCustom_ShowStores;
    private BikeStoreAdapterCustomer bikeStoreAdapterCustomer;

    public List<BikeStores> listBikeStoresCustomer;

    private TextView tVBikeStoreImgCustomShowStores;

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_store_image_customer_show_stores);

        Objects.requireNonNull(getSupportActionBar()).setTitle("CUSTOMER Bike Stores available");

        progressDialog = new ProgressDialog(this);

        //initialize the bike store database
        databaseReference = FirebaseDatabase.getInstance().getReference("Bike Stores");

        tVBikeStoreImgCustomShowStores = findViewById(R.id.tvBikeStoreImgCustomShowStores);

        rvBikeStoreImgCustom_ShowStores = findViewById(R.id.rvBikeStoreImgCustomShowStores);
        rvBikeStoreImgCustom_ShowStores.setHasFixedSize(true);
        rvBikeStoreImgCustom_ShowStores.setLayoutManager(new LinearLayoutManager(this));

        listBikeStoresCustomer = new ArrayList<>();

        bikeStoreAdapterCustomer = new BikeStoreAdapterCustomer(BikeStoreImageCustomerShowStores.this, listBikeStoresCustomer);
        rvBikeStoreImgCustom_ShowStores.setAdapter(bikeStoreAdapterCustomer);
        bikeStoreAdapterCustomer.setOnItmClickListener(BikeStoreImageCustomerShowStores.this);
    }

    @Override
    public void onStart() {
        super.onStart();
        loadBikeStoresListCustomer();
    }

    private void loadBikeStoresListCustomer() {

        progressDialog.show();

        bikeStoreEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                listBikeStoresCustomer.clear();

                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        BikeStores bikeStores = postSnapshot.getValue(BikeStores.class);
                        assert bikeStores != null;
                        bikeStores.setBikeStore_Key(postSnapshot.getKey());
                        listBikeStoresCustomer.add(bikeStores);
                    }

                    if (listBikeStoresCustomer.size() == 1) {
                        tVBikeStoreImgCustomShowStores.setText(listBikeStoresCustomer.size() + " Bike Store available ");
                    }
                    else {
                        tVBikeStoreImgCustomShowStores.setText(listBikeStoresCustomer.size() + " Bike Stores available ");
                    }

                    bikeStoreAdapterCustomer.notifyDataSetChanged();
                }

                else {
                    tVBikeStoreImgCustomShowStores.setText("No Bike Stores available!!");
                    bikeStoreAdapterCustomer.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikeStoreImageCustomerShowStores.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        progressDialog.dismiss();
    }

    @Override
    public void onItemClick(int position) {
        final String[] options = {"Show Google Map", "Back Main Page"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, options);
        BikeStores selected_BikeStores = listBikeStoresCustomer.get(position);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setCancelable(false)
                .setTitle("You selected " + selected_BikeStores.getBikeStore_Location() + " Store" + "\nSelect an option:")
                .setAdapter(adapter, (dialog, id) -> {

                    if (id == 0) {
                        Toast.makeText(BikeStoreImageCustomerShowStores.this, "Show Bikes Stores in Google Map", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(BikeStoreImageCustomerShowStores.this, MapsActivity.class));
                    }

                    if (id == 1) {
                        startActivity(new Intent(BikeStoreImageCustomerShowStores.this, CustomerPageRentBikes.class));
                        Toast.makeText(BikeStoreImageCustomerShowStores.this, "Back to Customer rent page", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("CLOSE", (dialog, id) -> dialog.dismiss());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
