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

public class BikeStoreImageShowStoresListMain extends AppCompatActivity implements BikeStoreAdapterShowStoresListMain.OnItemClickListener {

    private TextView tVBikeStoresImaShowStoreListMain;
    private DatabaseReference databaseReference;
    private ValueEventListener bikeStoreEventListener;

    private RecyclerView bikeStoreRecyclerView;
    private BikeStoreAdapterShowStoresListMain bikeStoreAdapterShowStoresListMain;

    public List<BikeStore> bikeStoreList;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_store_image_show_stores_list_main);

        tVBikeStoresImaShowStoreListMain = (TextView) findViewById(R.id.tvBikeStoresImageShowStoreListMain);
        tVBikeStoresImaShowStoreListMain.setText("No Bike Stores available");

        bikeStoreRecyclerView = (RecyclerView) findViewById(R.id.evRecyclerView);
        bikeStoreRecyclerView.setHasFixedSize(true);
        bikeStoreRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressDialog = new ProgressDialog(this);
        bikeStoreList = new ArrayList<BikeStore>();

        progressDialog.show();
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
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bikeStoreList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    BikeStore bikeStore = postSnapshot.getValue(BikeStore.class);
                    assert bikeStore != null;
                    bikeStore.setStoreKey(postSnapshot.getKey());
                    bikeStoreList.add(bikeStore);
                    tVBikeStoresImaShowStoreListMain.setText("Bike Stores available " + bikeStoreList.size());
                }
                bikeStoreAdapterShowStoresListMain = new BikeStoreAdapterShowStoresListMain(BikeStoreImageShowStoresListMain.this, bikeStoreList);
                bikeStoreRecyclerView.setAdapter(bikeStoreAdapterShowStoresListMain);
                bikeStoreAdapterShowStoresListMain.setOnItmClickListener(BikeStoreImageShowStoresListMain.this);
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
    public void onItemClick(final int position) {
        final String[] options = {"Show Map","Back Main Page"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, options);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select an option");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (which == 0) {
                    Toast.makeText(BikeStoreImageShowStoresListMain.this, "Show Bikes Stores in Google Map", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(BikeStoreImageShowStoresListMain.this, MapsActivity.class));
                }
                if (which == 1) {
                    startActivity(new Intent(BikeStoreImageShowStoresListMain.this, MainActivity.class));
                    Toast.makeText(BikeStoreImageShowStoresListMain.this, "Back to main page", Toast.LENGTH_SHORT).show();
                }
            }
        });

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
