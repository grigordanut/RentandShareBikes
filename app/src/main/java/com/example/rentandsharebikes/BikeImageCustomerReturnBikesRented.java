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

public class BikeImageCustomerReturnBikesRented extends AppCompatActivity implements BikeAdapterCustomerShowBikesRented.OnItemClickListener {

    private DatabaseReference databaseRefReturnBikesRented;

    private FirebaseStorage bikesStReturnBikesRented;

    private ValueEventListener returnBikesEventListener;

    private RecyclerView rvBikesImgCustom_ReturnBikesRented;
    private BikeAdapterCustomerShowBikesRented bikeAdapterCustomerShowBikesRented;

    private TextView tVBikesImgCustomReturnBikesRented;

    private List<RentedBikes> listCustomReturnBikesRented;

    String customerFirst_Name = "";
    String customerLast_Name = "";
    String customer_Id = "";

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_image_customer_return_bikes_rented);

        progressDialog = new ProgressDialog(this);

        //initialize the bike storage database
        bikesStReturnBikesRented = FirebaseStorage.getInstance();
        databaseRefReturnBikesRented = FirebaseDatabase.getInstance().getReference().child("Rent Bikes");

        getIntent().hasExtra("CFName");
        customerFirst_Name = Objects.requireNonNull(getIntent().getExtras()).getString("CFName");

        getIntent().hasExtra("CLName");
        customerLast_Name = Objects.requireNonNull(getIntent().getExtras()).getString("CLName");

        getIntent().hasExtra("CId");
        customer_Id = Objects.requireNonNull(getIntent().getExtras()).getString("CId");

        Objects.requireNonNull(getSupportActionBar()).setTitle("Bikes rented by: " + customerFirst_Name + " " + customerLast_Name);

        tVBikesImgCustomReturnBikesRented = findViewById(R.id.tvBikesImgCustomReturnBikesRented);

        rvBikesImgCustom_ReturnBikesRented = (RecyclerView) findViewById(R.id.rvBikesImgCustomReturnBikesRented);
        rvBikesImgCustom_ReturnBikesRented.setHasFixedSize(true);
        rvBikesImgCustom_ReturnBikesRented.setLayoutManager(new LinearLayoutManager(this));

        listCustomReturnBikesRented = new ArrayList<>();

        bikeAdapterCustomerShowBikesRented = new BikeAdapterCustomerShowBikesRented(BikeImageCustomerReturnBikesRented.this, listCustomReturnBikesRented);
        rvBikesImgCustom_ReturnBikesRented.setAdapter(bikeAdapterCustomerShowBikesRented);
        bikeAdapterCustomerShowBikesRented.setOnItmClickListener(BikeImageCustomerReturnBikesRented.this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onStart() {
        super.onStart();
        loadBikesListCustomer();
    }

    private void loadBikesListCustomer() {

        progressDialog.show();

        returnBikesEventListener = databaseRefReturnBikesRented.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                listCustomReturnBikesRented.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    RentedBikes rent_Bikes = postSnapshot.getValue(RentedBikes.class);
                    assert rent_Bikes != null;
                    rent_Bikes.setBike_RentKey(postSnapshot.getKey());

                    if (rent_Bikes.getCustomerId_RentBikes().equals(customer_Id)) {
                        listCustomReturnBikesRented.add(rent_Bikes);
                    }
                }

                if (listCustomReturnBikesRented.size() == 1) {
                    tVBikesImgCustomReturnBikesRented.setText("Select the bike");

                }
                else {
                    tVBikesImgCustomReturnBikesRented.setText("No bikes rented by: " + customerFirst_Name + " " + customerLast_Name);
                }

                bikeAdapterCustomerShowBikesRented.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikeImageCustomerReturnBikesRented.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        progressDialog.dismiss();
    }

    @Override
    public void onItemClick(int position) {

        RentedBikes selected_Bike = listCustomReturnBikesRented.get(position);
        Intent intent = new Intent(BikeImageCustomerReturnBikesRented.this, ReturnRentedBikes.class);
        intent.putExtra("BikeRentedKey", selected_Bike.getBike_RentKey());
        startActivity(intent);
    }
}
