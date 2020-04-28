package com.example.rentandsharebikes;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
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

public class BikesImageReturnBikesRented extends AppCompatActivity {

    private DatabaseReference databaseRefReturnBikesRented;

    private FirebaseStorage bikesStReturnBikesRented;

    private ValueEventListener returnBikesEventListener;

    private RecyclerView bikesListRecyclerView;
    private BikesAdapterReturnBikesRented bikesAdapterReturnBikesRented;

    private List<RentBikes> rentBikesList;

    String customerFirst_Name = "";
    String customerLast_Name = "";
    String customer_Id = "";

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bikes_image_return_bikes_rented);

        getIntent().hasExtra("CFName");
        customerFirst_Name = Objects.requireNonNull(getIntent().getExtras()).getString("CFName");

        getIntent().hasExtra("CLName");
        customerLast_Name = Objects.requireNonNull(getIntent().getExtras()).getString("CLName");

        getIntent().hasExtra("CId");
        customer_Id = Objects.requireNonNull(getIntent().getExtras()).getString("CId");

        bikesListRecyclerView = (RecyclerView) findViewById(R.id.evRecyclerView);
        bikesListRecyclerView.setHasFixedSize(true);
        bikesListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressDialog = new ProgressDialog(this);
        rentBikesList = new ArrayList<>();

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
        bikesStReturnBikesRented = FirebaseStorage.getInstance();
        databaseRefReturnBikesRented = FirebaseDatabase.getInstance().getReference("Rent Bikes");

        returnBikesEventListener = databaseRefReturnBikesRented.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                rentBikesList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    RentBikes rent_Bikes = postSnapshot.getValue(RentBikes.class);

                    assert rent_Bikes != null;
                    if (rent_Bikes.getCustomerId_RentBikes().equals(customer_Id)) {
                        rent_Bikes.setBike_RentKey(postSnapshot.getKey());
                        rentBikesList.add(rent_Bikes);
                    }
                }
                bikesAdapterReturnBikesRented = new BikesAdapterReturnBikesRented(BikesImageReturnBikesRented.this, rentBikesList);
                bikesListRecyclerView.setAdapter(bikesAdapterReturnBikesRented);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikesImageReturnBikesRented.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
