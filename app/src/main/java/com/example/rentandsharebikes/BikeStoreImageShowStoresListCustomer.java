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

public class BikeStoreImageShowStoresListCustomer extends AppCompatActivity implements BikeStoreAdapterShowStoresListCustomer.OnItemClickListener {
    private TextView textViewBikeStoresImageShowStoreListCustomer;

    private DatabaseReference databaseReference;
    private ValueEventListener bikeStoreEventListener;

    private RecyclerView bikeStoreRecyclerView;
    private BikeStoreAdapterShowStoresListCustomer bikeStoreAdapterShowStoresListCustomer;

    public List<BikeStore> bikeStoreList;

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_store_image_show_stores_list_customer);

        textViewBikeStoresImageShowStoreListCustomer = (TextView)findViewById(R.id.tvBikeStoresImageShowStoresListCustomer);
        textViewBikeStoresImageShowStoreListCustomer.setText("No Bike Stores available");

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
        loadBikeStoresListCustomer();
    }

    private void loadBikeStoresListCustomer() {
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
                    textViewBikeStoresImageShowStoreListCustomer.setText(bikeStoreList.size()+" Bike Stores available ");
                }
                bikeStoreAdapterShowStoresListCustomer = new BikeStoreAdapterShowStoresListCustomer(BikeStoreImageShowStoresListCustomer.this, bikeStoreList);
                bikeStoreRecyclerView.setAdapter(bikeStoreAdapterShowStoresListCustomer);
                bikeStoreAdapterShowStoresListCustomer.setOnItmClickListener(BikeStoreImageShowStoresListCustomer.this);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikeStoreImageShowStoresListCustomer.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    //Action of the menu onClick
    @Override
    public void onItemClick(final int position) {
        final String[] options = {"Show Google Map", "Back Main Page"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, options);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        BikeStore selected_BikeStore = bikeStoreList.get(position);
        builder.setTitle("You selected "+selected_BikeStore.getBikeStore_Location()+" Store"+"\nSelect an option");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
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
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
