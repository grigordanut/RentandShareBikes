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

public class BikeImageAdminShowBikesRented extends AppCompatActivity implements BikeAdapterAdminShowBikesRented.OnItemClickListener {

    private DatabaseReference databaseReference;
    private FirebaseStorage bikesStorage;
    private ValueEventListener bikesEventListener;

    private RecyclerView rvBikesImgAdminShow_BikesRented;
    private BikeAdapterAdminShowBikesRented bikeAdapterAdminShowBikesRented;

    private TextView tVBikesImgAdminShowBikesRented;

    private List<RentedBikes> listShowBikesRented;

    private ProgressDialog progressDialog;

    String bikeStore_Name = "";
    String bikeStore_Key = "";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_image_admin_show_bikes_rented);

        progressDialog = new ProgressDialog(this);

        //initialize the bike storage database
        bikesStorage = FirebaseStorage.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Rent Bikes");

        getIntent().hasExtra("SName");
        bikeStore_Name = Objects.requireNonNull(getIntent().getExtras()).getString("SName");

        getIntent().hasExtra("SKey");
        bikeStore_Key = Objects.requireNonNull(getIntent().getExtras()).getString("SKey");

        Objects.requireNonNull(getSupportActionBar()).setTitle(bikeStore_Name + " Bike Store");

        tVBikesImgAdminShowBikesRented = findViewById(R.id.tvBikesImgAdminShowBikesRented);

        rvBikesImgAdminShow_BikesRented = findViewById(R.id.rvBikesImgAdminShowBikesRented);
        rvBikesImgAdminShow_BikesRented.setHasFixedSize(true);
        rvBikesImgAdminShow_BikesRented.setLayoutManager(new LinearLayoutManager(this));

        listShowBikesRented = new ArrayList<>();

        bikeAdapterAdminShowBikesRented = new BikeAdapterAdminShowBikesRented(BikeImageAdminShowBikesRented.this, listShowBikesRented);
        rvBikesImgAdminShow_BikesRented.setAdapter(bikeAdapterAdminShowBikesRented);
        bikeAdapterAdminShowBikesRented.setOnItmClickListener(BikeImageAdminShowBikesRented.this);
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

                listShowBikesRented.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    RentedBikes rent_Bikes = postSnapshot.getValue(RentedBikes.class);
                    assert rent_Bikes != null;
                    rent_Bikes.setBike_RentKey(postSnapshot.getKey());

                    if (rent_Bikes.getStoreKey_RentBikes().equals(bikeStore_Key)) {
                        listShowBikesRented.add(rent_Bikes);
                    }
                }

                if (listShowBikesRented.size() == 1) {
                    tVBikesImgAdminShowBikesRented.setText(listShowBikesRented.size() + " Bike rented from: " + bikeStore_Name);
                }
                else if (listShowBikesRented.size() > 1) {
                    tVBikesImgAdminShowBikesRented.setText(listShowBikesRented.size() + " Bikes rented from: " + bikeStore_Name);
                }
                else {
                    tVBikesImgAdminShowBikesRented.setText("No bikes rented from: " + bikeStore_Name);
                }

                bikeAdapterAdminShowBikesRented.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikeImageAdminShowBikesRented.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        progressDialog.dismiss();
    }

    @Override
    public void onItemClick(int position) {

        RentedBikes selected_Bike = listShowBikesRented.get(position);

        Context context = BikeImageAdminShowBikesRented.this;
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