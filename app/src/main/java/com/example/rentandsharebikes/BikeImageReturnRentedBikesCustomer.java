package com.example.rentandsharebikes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
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

public class BikeImageReturnRentedBikesCustomer extends AppCompatActivity implements BikeAdapterRentedBikesCustomer.OnItemClickListener {

    private DatabaseReference databaseRefReturnBikesRented;

    private FirebaseStorage bikesStReturnBikesRented;


    private ValueEventListener returnBikesEventListener;

    private RecyclerView bikesListRecyclerView;
    private BikeAdapterRentedBikesCustomer bikeAdapterRentedBikesCustomer;

    private TextView tVCustomerReturnBikes;

    private List<RentedBikes> rentedBikesList;

    String customerFirst_Name = "";
    String customerLast_Name = "";
    String customer_Id = "";

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_image_return_rented_bikes_customer);

        progressDialog = new ProgressDialog(this);
        progressDialog.show();

        getIntent().hasExtra("CFName");
        customerFirst_Name = Objects.requireNonNull(getIntent().getExtras()).getString("CFName");

        getIntent().hasExtra("CLName");
        customerLast_Name = Objects.requireNonNull(getIntent().getExtras()).getString("CLName");

        getIntent().hasExtra("CId");
        customer_Id = Objects.requireNonNull(getIntent().getExtras()).getString("CId");

        tVCustomerReturnBikes = findViewById(R.id.tvCusReturnBikes);

        tVCustomerReturnBikes.setText("No Bikes rented by: " + customerFirst_Name + " " + customerLast_Name);

        bikesListRecyclerView = (RecyclerView) findViewById(R.id.evRecyclerView);
        bikesListRecyclerView.setHasFixedSize(true);
        bikesListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        rentedBikesList = new ArrayList<>();

        bikeAdapterRentedBikesCustomer = new BikeAdapterRentedBikesCustomer(BikeImageReturnRentedBikesCustomer.this, rentedBikesList);
        bikesListRecyclerView.setAdapter(bikeAdapterRentedBikesCustomer);
        bikeAdapterRentedBikesCustomer.setOnItmClickListener(BikeImageReturnRentedBikesCustomer.this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onStart() {
        super.onStart();
        loadBikesListCustomer();
    }

    private void loadBikesListCustomer() {

        //initialize the bike storage database
        bikesStReturnBikesRented = FirebaseStorage.getInstance();
        databaseRefReturnBikesRented = FirebaseDatabase.getInstance().getReference().child("Rent Bikes");

        returnBikesEventListener = databaseRefReturnBikesRented.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                rentedBikesList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    RentedBikes rent_Bikes = postSnapshot.getValue(RentedBikes.class);

                    assert rent_Bikes != null;
                    if (rent_Bikes.getCustomerId_RentBikes().equals(customer_Id)) {
                        rent_Bikes.setBike_RentKey(postSnapshot.getKey());
                        rentedBikesList.add(rent_Bikes);
                        tVCustomerReturnBikes.setText("Select the Bike");
                    }
//                    else {
//                        tVCustomerReturnBikes.setText("No rented Bikes 1");
//                    }
                }

                bikeAdapterRentedBikesCustomer.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikeImageReturnRentedBikesCustomer.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(int position) {

        RentedBikes selected_Bike = rentedBikesList.get(position);
        Intent intent = new Intent(BikeImageReturnRentedBikesCustomer.this, ReturnRentedBikes.class);
        //Intent intent = new Intent (BikeImageReturnRentedBikesCustomer.this, ReturnRentedBikesSpinner.class);

        //Bike key of rented bike
        intent.putExtra("BikeRentedKey", selected_Bike.getBike_RentKey());
        startActivity(intent);
    }
}
