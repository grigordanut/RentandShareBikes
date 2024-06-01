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

public class BikeImageAdminShowBikesRentedAll extends AppCompatActivity implements BikeAdapterAdminShowBikesRented.OnItemClickListener {

    private DatabaseReference databaseReference;
    private FirebaseStorage bikesStorage;
    private ValueEventListener bikesEventListener;

    private RecyclerView rvBikesImgAdminShow_BikesRentedAll;
    private BikeAdapterAdminShowBikesRented bikeAdapterAdminShowBikesRented;

    private TextView tVBikesImgAdminShowBikesRentedAll;

    private List<RentedBikes> listShowBikesRentedAll;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_image_admin_show_bikes_rented_all);

        Objects.requireNonNull(getSupportActionBar()).setTitle("ADMIN All bikes rented");

        progressDialog = new ProgressDialog(this);

        //initialize the bike storage database
        bikesStorage = FirebaseStorage.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Rent Bikes");

        tVBikesImgAdminShowBikesRentedAll = findViewById(R.id.tvBikesImgAdminShowBikesRentedAll);

        rvBikesImgAdminShow_BikesRentedAll = findViewById(R.id.rvBikesImgAdminShowBikesRentedAll);
        rvBikesImgAdminShow_BikesRentedAll.setHasFixedSize(true);
        rvBikesImgAdminShow_BikesRentedAll.setLayoutManager(new LinearLayoutManager(this));

        listShowBikesRentedAll = new ArrayList<>();

        bikeAdapterAdminShowBikesRented = new BikeAdapterAdminShowBikesRented(BikeImageAdminShowBikesRentedAll.this, listShowBikesRentedAll);
        rvBikesImgAdminShow_BikesRentedAll.setAdapter(bikeAdapterAdminShowBikesRented);
        bikeAdapterAdminShowBikesRented.setOnItmClickListener(BikeImageAdminShowBikesRentedAll.this);
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

                listShowBikesRentedAll.clear();

                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        RentedBikes rent_Bikes = postSnapshot.getValue(RentedBikes.class);
                        assert rent_Bikes != null;
                        rent_Bikes.setBike_RentKey(postSnapshot.getKey());
                        listShowBikesRentedAll.add(rent_Bikes);
                    }

                    if (listShowBikesRentedAll.size() == 1) {
                        tVBikesImgAdminShowBikesRentedAll.setText(listShowBikesRentedAll.size() + " bike rented by customers");

                    }
                    else {
                        tVBikesImgAdminShowBikesRentedAll.setText(listShowBikesRentedAll.size() + " bikes rented by customers");
                    }

                    bikeAdapterAdminShowBikesRented.notifyDataSetChanged();
                }

                else {
                    tVBikesImgAdminShowBikesRentedAll.setText("No bikes rented by customers");
                    bikeAdapterAdminShowBikesRented.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikeImageAdminShowBikesRentedAll.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        progressDialog.dismiss();
    }

    @Override
    public void onItemClick(int position) {

        RentedBikes selected_Bike = listShowBikesRentedAll.get(position);

        Context context = BikeImageAdminShowBikesRentedAll.this;
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
