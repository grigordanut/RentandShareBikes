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

public class BikeImageCustomerRentBikes extends AppCompatActivity implements BikeAdapterBikesCustomer.OnItemClickListener {

    private DatabaseReference databaseReference;
    private FirebaseStorage bikesStorage;
    private ValueEventListener bikesEventListener;

    private RecyclerView rvBikesImgCustom_RentBikes;
    private BikeAdapterBikesCustomer bikeAdapterBikesCustomer;

    private TextView tVBikesImgCustomRentBikes;

    private List<Bikes> listCustomerRentBikes;

    String bikeStore_Name = "";
    String bikeStore_Key = "";

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_image_customer_rent_bikes);

        progressDialog = new ProgressDialog(this);

        //initialize the bike storage database
        bikesStorage = FirebaseStorage.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Bikes");

        getIntent().hasExtra("SName");
        bikeStore_Name = Objects.requireNonNull(getIntent().getExtras()).getString("SName");

        getIntent().hasExtra("SKey");
        bikeStore_Key = Objects.requireNonNull(getIntent().getExtras()).getString("SKey");

        tVBikesImgCustomRentBikes = findViewById(R.id.tvBikesImgCustomRentBikes);

        rvBikesImgCustom_RentBikes = findViewById(R.id.rvBikesImgCustomRentBikes);
        rvBikesImgCustom_RentBikes.setHasFixedSize(true);
        rvBikesImgCustom_RentBikes.setLayoutManager(new LinearLayoutManager(this));

        listCustomerRentBikes = new ArrayList<>();

        bikeAdapterBikesCustomer = new BikeAdapterBikesCustomer(BikeImageCustomerRentBikes.this, listCustomerRentBikes);
        rvBikesImgCustom_RentBikes.setAdapter(bikeAdapterBikesCustomer);
        bikeAdapterBikesCustomer.setOnItmClickListener(BikeImageCustomerRentBikes.this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onStart() {
        super.onStart();
        loadBikesListCustomer();
    }

    private void loadBikesListCustomer() {

        progressDialog.show();

        bikesEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                listCustomerRentBikes.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Bikes bikes = postSnapshot.getValue(Bikes.class);
                    assert bikes != null;
                    bikes.setBike_Key(postSnapshot.getKey());

                    if (bikes.getBikeStoreKey().equals(bikeStore_Key)) {
                        listCustomerRentBikes.add(bikes);
                    }
                }

                if (listCustomerRentBikes.size() == 1) {
                    tVBikesImgCustomRentBikes.setText(listCustomerRentBikes.size() + " bike available in " + bikeStore_Name + " store");
                }
                else if (listCustomerRentBikes.size() > 1) {
                    tVBikesImgCustomRentBikes.setText(listCustomerRentBikes.size() + " bike available in " + bikeStore_Name + " store");
                }
                else {
                    tVBikesImgCustomRentBikes.setText("No bikes available in " + bikeStore_Name + " store");
                }

                bikeAdapterBikesCustomer.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikeImageCustomerRentBikes.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        progressDialog.dismiss();
    }

    @Override
    public void onItemClick(int position) {

        Bikes selected_Bike = listCustomerRentBikes.get(position);
        Intent intent = new Intent(BikeImageCustomerRentBikes.this, RentBikesCustomer.class);
        intent.putExtra("BCondition", selected_Bike.getBike_Condition());
        intent.putExtra("BModel", selected_Bike.getBike_Model());
        intent.putExtra("BManufact", selected_Bike.getBike_Manufacturer());
        intent.putExtra("BImage", selected_Bike.getBike_Image());
        intent.putExtra("BStoreName", selected_Bike.getBikeStoreName());
        intent.putExtra("BStoreKey", selected_Bike.getBikeStoreKey());
        intent.putExtra("BPrice", String.valueOf(selected_Bike.getBike_Price()));
        intent.putExtra("BKey", selected_Bike.getBike_Key());
        startActivity(intent);
    }
}
