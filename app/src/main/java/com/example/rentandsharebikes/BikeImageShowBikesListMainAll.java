package com.example.rentandsharebikes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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

public class BikeImageShowBikesListMainAll extends AppCompatActivity implements BikeAdapterShowBikesListMainAll.OnItemClickListener {

    //Access Bikes table from database
    private DatabaseReference databaseRefMainAll;
    private FirebaseStorage bikesStorageMainAll;
    private ValueEventListener bikesEventListenerMainAll;

    private RecyclerView bikesListRecyclerView;

    private BikeAdapterShowBikesListMainAll bikeAdapterShowBikesListMainAll;
    private TextView tVBikesImageMainAll;

    private List<Bikes> bikesListMainAll;

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_image_show_bikes_list_main_all);

        progressDialog = new ProgressDialog(this);
        progressDialog.show();

        tVBikesImageMainAll = findViewById(R.id.tvBikeImageBikesListMainAll);

        bikesListRecyclerView = findViewById(R.id.evRecyclerView);
        bikesListRecyclerView.setHasFixedSize(true);
        bikesListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        bikesListMainAll = new ArrayList<>();

        bikeAdapterShowBikesListMainAll = new BikeAdapterShowBikesListMainAll(BikeImageShowBikesListMainAll.this, bikesListMainAll);
        bikesListRecyclerView.setAdapter(bikeAdapterShowBikesListMainAll);
        bikeAdapterShowBikesListMainAll.setOnItmClickListener(BikeImageShowBikesListMainAll.this);
    }

    @Override
    public void onStart() {
        super.onStart();
        loadBikesListMainPageAll();
    }

    public void loadBikesListMainPageAll() {
        //initialize the bike storage database
        bikesStorageMainAll = FirebaseStorage.getInstance();
        databaseRefMainAll = FirebaseDatabase.getInstance().getReference("Bikes");

        bikesEventListenerMainAll = databaseRefMainAll.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                bikesListMainAll.clear();

                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Bikes bikes = postSnapshot.getValue(Bikes.class);
                        assert bikes != null;
                        bikes.setBike_Key(postSnapshot.getKey());
                        bikesListMainAll.add(bikes);
                    }

                    bikeAdapterShowBikesListMainAll.notifyDataSetChanged();

                    if (bikesListMainAll.size() == 1) {
                        tVBikesImageMainAll.setText(bikesListMainAll.size() + " Bike available to rent ");
                    }
                    else {
                        tVBikesImageMainAll.setText(bikesListMainAll.size() + " Bikes available to rent ");
                    }
                }

                else {
                    tVBikesImageMainAll.setText("No bikes available to rent!!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikeImageShowBikesListMainAll.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        progressDialog.dismiss();
    }

    //Action of the menu onClick
    @Override
    public void onItemClick(int position) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BikeImageShowBikesListMainAll.this);
        alertDialogBuilder
                .setCancelable(false)
                .setMessage("Register and Log into your account to access the:\nRent and Share Bikes services.")
                .setPositiveButton("OK", (dialog, id) -> dialog.dismiss());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
