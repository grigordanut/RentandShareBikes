package com.example.rentandsharebikes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BikesImageRentBikesCustomer extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private FirebaseStorage bikesStorage;
    private ValueEventListener bikesEventListener;

    private RecyclerView bikesListRecyclerView;
    private BikesAdapterRentBikesCustomer bikesAdapterRentBikesCustomer;

    private TextView textViewBikesImageList;

    private List<Bikes> bikesList;

    String bikeStore_Name = "";
    String bikeStore_Key = "";

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bikes_image_rent_bikes_customer);

        getIntent().hasExtra("SName");
        bikeStore_Name = Objects.requireNonNull(getIntent().getExtras()).getString("SName");

        getIntent().hasExtra("SKey");
        bikeStore_Key= Objects.requireNonNull(getIntent().getExtras()).getString("SKey");

        textViewBikesImageList = (TextView) findViewById(R.id.tvBikeImageList);
        textViewBikesImageList.setText("No bikes available in " +bikeStore_Name+ " store");

        bikesListRecyclerView = (RecyclerView) findViewById(R.id.evRecyclerView);
        bikesListRecyclerView.setHasFixedSize(true);
        bikesListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressDialog = new ProgressDialog(this);
        bikesList = new ArrayList<>();

        progressDialog.show();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onStart() {
        super.onStart();
        loadBikesListCustomer();
    }

    private void loadBikesListCustomer() {
        //initialize the bike storage database
        bikesStorage = FirebaseStorage.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Bikes");

        bikesEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bikesList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Bikes bikes = postSnapshot.getValue(Bikes.class);
                    assert bikes != null;
                    if (bikes.getBikeStoreKey().equals(bikeStore_Key)) {
                        bikes.setBike_Key(postSnapshot.getKey());
                        bikesList.add(bikes);
                        textViewBikesImageList.setText(bikesList.size()+" bikes available in "+bikeStore_Name+" store");
                    }
                }
                bikesAdapterRentBikesCustomer = new BikesAdapterRentBikesCustomer(BikesImageRentBikesCustomer.this, bikesList);
                bikesListRecyclerView.setAdapter(bikesAdapterRentBikesCustomer);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikesImageRentBikesCustomer.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
