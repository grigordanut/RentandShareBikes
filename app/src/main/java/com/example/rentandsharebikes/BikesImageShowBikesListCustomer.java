package com.example.rentandsharebikes;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BikesImageShowBikesListCustomer extends AppCompatActivity implements BikesAdapterShowBikesListCustomer.OnItemClickListener{

    private FirebaseStorage bikesStorage;
    private DatabaseReference databaseReference;
    private ValueEventListener bikesEventListener;

    private RecyclerView bikesListRecyclerView;
    private BikesAdapterShowBikesListCustomer bikesAdapterShowBikesListCustomer;

    private TextView textViewBikesImageList;

    private List<Bikes> bikesList;

    String bikeStore_Name = "";

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bikes_image_show_bikes_list_customer);

        getIntent().hasExtra("SName");
        bikeStore_Name = Objects.requireNonNull(getIntent().getExtras()).getString("SName");

        textViewBikesImageList = (TextView) findViewById(R.id.tvBikeImageList);
        textViewBikesImageList.setText("No bikes available in " +bikeStore_Name+ " store");

        bikesListRecyclerView = (RecyclerView) findViewById(R.id.evRecyclerView);
        bikesListRecyclerView.setHasFixedSize(true);
        bikesListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressDialog = new ProgressDialog(this);
        bikesList = new ArrayList<>();

        progressDialog.show();
    }

    //Action on bikes onClick
    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, "Press long click to show more actions: ", Toast.LENGTH_SHORT).show();
    }

    //Action of the menu Rent Bikes on alert dialog
    @Override
    public void onRentBikeClick(int position) {
        Intent intent = new Intent(BikesImageShowBikesListCustomer.this, RentBikesCustomer.class);
        Bikes selected_Bike = bikesList.get(position);
        intent.putExtra("BCondition",selected_Bike.getBike_Condition());
        intent.putExtra("BModel",selected_Bike.getBike_Model());
        intent.putExtra("BManufact",selected_Bike.getBike_Manufacturer());
        intent.putExtra("BImage",selected_Bike.getBike_Image());
        intent.putExtra("BStore",selected_Bike.getBikeStoreName());
        intent.putExtra("BPrice",String.valueOf(selected_Bike.getBike_Price()));
        intent.putExtra("BKey",selected_Bike.getBike_Key());
        startActivity(intent);
    }

    //Action of the menu Add to Cart on alert dialog
    @Override
    public void onAddToCartBikeClick(final int position) {

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
                    if (bikes.getBikeStoreName().equals(bikeStore_Name)) {
                        bikes.setBike_Key(postSnapshot.getKey());
                        bikesList.add(bikes);
                        textViewBikesImageList.setText(bikesList.size()+" bikes available in "+bikeStore_Name+" store");
                    }
                }
                bikesAdapterShowBikesListCustomer = new BikesAdapterShowBikesListCustomer(BikesImageShowBikesListCustomer.this, bikesList);
                bikesListRecyclerView.setAdapter(bikesAdapterShowBikesListCustomer);
                bikesAdapterShowBikesListCustomer.setOnItmClickListener(BikesImageShowBikesListCustomer.this);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikesImageShowBikesListCustomer.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
