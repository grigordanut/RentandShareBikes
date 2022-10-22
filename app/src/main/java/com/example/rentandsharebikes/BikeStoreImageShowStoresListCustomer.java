package com.example.rentandsharebikes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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

public class BikeStoreImageShowStoresListCustomer extends AppCompatActivity implements BikeStoreAdapterCustom.OnItemClickListener {

    private TextView textViewBikeStoresImageShowStoreListCustomer;

    private DatabaseReference databaseReference;
    private ValueEventListener bikeStoreEventListener;

    private RecyclerView bikeStoreRecyclerView;
    private BikeStoreAdapterCustom bikeStoreAdapterCustom;

    public List<BikeStores> bikeStoresList;

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_store_image_show_stores_list_customer);

        progressDialog = new ProgressDialog(this);
        progressDialog.show();

        textViewBikeStoresImageShowStoreListCustomer = findViewById(R.id.tvBikeStoresImageShowStoresListCustomer);
        textViewBikeStoresImageShowStoreListCustomer.setText("No Bike Stores available");

        bikeStoreRecyclerView = findViewById(R.id.evRecyclerView);
        bikeStoreRecyclerView.setHasFixedSize(true);
        bikeStoreRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        bikeStoresList = new ArrayList<BikeStores>();

        bikeStoreAdapterCustom = new BikeStoreAdapterCustom(BikeStoreImageShowStoresListCustomer.this, bikeStoresList);
        bikeStoreRecyclerView.setAdapter(bikeStoreAdapterCustom);
        bikeStoreAdapterCustom.setOnItmClickListener(BikeStoreImageShowStoresListCustomer.this);
    }

    @Override
    public void onStart() {
        super.onStart();
        loadBikeStoresListCustomer();
    }

    private void loadBikeStoresListCustomer() {
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
                    textViewBikeStoresImageShowStoreListCustomer.setText(bikeStoresList.size() + " Bike Stores available ");
                }

                bikeStoreAdapterCustom.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikeStoreImageShowStoresListCustomer.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        final String[] options = {"Show Google Map", "Back Main Page"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, options);
        BikeStores selected_BikeStores = bikeStoresList.get(position);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setCancelable(false)
                .setTitle("You selected " + selected_BikeStores.getBikeStore_Location() + " Store" + "\nSelect an option:")
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (which == 0) {
                            Toast.makeText(BikeStoreImageShowStoresListCustomer.this, "Show Bikes Stores in Google Map", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(BikeStoreImageShowStoresListCustomer.this, MapsActivity.class));
                        }

                        if (which == 1) {
                            startActivity(new Intent(BikeStoreImageShowStoresListCustomer.this, CustomerPageRentBikes.class));
                            Toast.makeText(BikeStoreImageShowStoresListCustomer.this, "Back to Customer rent page", Toast.LENGTH_SHORT).show();
                        }
                    }
                })

                .setNegativeButton("CLOSE",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
