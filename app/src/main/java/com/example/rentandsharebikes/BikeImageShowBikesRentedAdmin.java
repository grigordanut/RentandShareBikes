package com.example.rentandsharebikes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BikeImageShowBikesRentedAdmin extends AppCompatActivity implements BikeAdapterRentedBikesAdmin.OnItemClickListener {

    private DatabaseReference databaseReference;
    private FirebaseStorage bikesStorage;
    private ValueEventListener bikesEventListener;

    private RecyclerView bikesListRecyclerView;
    private BikeAdapterRentedBikesAdmin bikeAdapterRentedBikesAdmin;

    private TextView tVAdminRentedBikes;

    private List<RentedBikes> rentedBikesList;

    private ProgressDialog progressDialog;

    String bikeStore_Name = "";
    String bikeStore_Key = "";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_image_show_bikes_rented_admin);

        progressDialog = new ProgressDialog(this);
        progressDialog.show();

        getIntent().hasExtra("SName");
        bikeStore_Name = Objects.requireNonNull(getIntent().getExtras()).getString("SName");

        getIntent().hasExtra("SKey");
        bikeStore_Key = Objects.requireNonNull(getIntent().getExtras()).getString("SKey");

        tVAdminRentedBikes = findViewById(R.id.tvAdminRentedBikes);
        tVAdminRentedBikes.setText("No bikes rented from " + bikeStore_Name + " store");

        bikesListRecyclerView = findViewById(R.id.evRecyclerView);
        bikesListRecyclerView.setHasFixedSize(true);
        bikesListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        rentedBikesList = new ArrayList<>();

        bikeAdapterRentedBikesAdmin = new BikeAdapterRentedBikesAdmin(BikeImageShowBikesRentedAdmin.this, rentedBikesList);
        bikesListRecyclerView.setAdapter(bikeAdapterRentedBikesAdmin);
        bikeAdapterRentedBikesAdmin.setOnItmClickListener(BikeImageShowBikesRentedAdmin.this);
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
        databaseReference = FirebaseDatabase.getInstance().getReference("Rent Bikes");

        bikesEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                rentedBikesList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    RentedBikes rent_Bikes = postSnapshot.getValue(RentedBikes.class);
                    assert rent_Bikes != null;
                    if (rent_Bikes.getStoreKey_RentBikes().equals(bikeStore_Key)) {
                        rent_Bikes.setBike_RentKey(postSnapshot.getKey());
                        rentedBikesList.add(rent_Bikes);
                        tVAdminRentedBikes.setText(rentedBikesList.size()+" Bikes rented by customers");
                    }
                }

                bikeAdapterRentedBikesAdmin.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikeImageShowBikesRentedAdmin.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(int position) {

        RentedBikes selected_Bike = rentedBikesList.get(position);

        Context context = BikeImageShowBikesRentedAdmin.this;
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.image_bike_full, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        final ImageView img_full = promptsView.findViewById(R.id.imgImageFull);

        Picasso.get()
                .load(selected_Bike.getBikeImage_RentBike())
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(img_full);

        // set dialog message
        alertDialogBuilder
                .setTitle("Bike Model: " + selected_Bike.getBikeModel_RentBikes())
                .setView(promptsView)
                .setCancelable(false)
                .setNegativeButton("CLOSE", (dialog, id) -> dialog.cancel());

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}