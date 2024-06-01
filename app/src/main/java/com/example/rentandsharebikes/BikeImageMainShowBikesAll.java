package com.example.rentandsharebikes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
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

public class BikeImageMainShowBikesAll extends AppCompatActivity implements BikeAdapterMainShowBikesAll.OnItemClickListener {

    //Access Bikes table from database
    private DatabaseReference databaseRefMainAll;
    private FirebaseStorage bikesStorageMainAll;
    private ValueEventListener bikesEventListenerMainAll;

    private RecyclerView rVBikeImgMain_ShowBikesAll;

    private BikeAdapterMainShowBikesAll bikeAdapterMainShowBikesAll;
    private TextView tVBikeImgMainShowBikesAll;

    private List<Bikes> listMainBikesAll;

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_image_main_show_bikes_all);

        Objects.requireNonNull(getSupportActionBar()).setTitle("MAIN Bikes available all");

        progressDialog = new ProgressDialog(this);

        //initialize the bike storage database
        bikesStorageMainAll = FirebaseStorage.getInstance();
        databaseRefMainAll = FirebaseDatabase.getInstance().getReference("Bikes");

        tVBikeImgMainShowBikesAll = findViewById(R.id.tvBikeImgMainShowBikesAll);

        rVBikeImgMain_ShowBikesAll = findViewById(R.id.rVBikeImgMainShowBikesAll);
        rVBikeImgMain_ShowBikesAll.setHasFixedSize(true);
        rVBikeImgMain_ShowBikesAll.setLayoutManager(new LinearLayoutManager(this));

        listMainBikesAll = new ArrayList<>();

        bikeAdapterMainShowBikesAll = new BikeAdapterMainShowBikesAll(BikeImageMainShowBikesAll.this, listMainBikesAll);
        rVBikeImgMain_ShowBikesAll.setAdapter(bikeAdapterMainShowBikesAll);
        bikeAdapterMainShowBikesAll.setOnItmClickListener(BikeImageMainShowBikesAll.this);
    }

    @Override
    public void onStart() {
        super.onStart();
        loadBikesListMainPageAll();
    }

    public void loadBikesListMainPageAll() {

        progressDialog.show();

        bikesEventListenerMainAll = databaseRefMainAll.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                listMainBikesAll.clear();

                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Bikes bikes = postSnapshot.getValue(Bikes.class);
                        assert bikes != null;
                        bikes.setBike_Key(postSnapshot.getKey());
                        listMainBikesAll.add(bikes);
                    }

                    if (listMainBikesAll.size() == 1) {
                        tVBikeImgMainShowBikesAll.setText(listMainBikesAll.size() + " bike available to rent ");
                    }
                    else {
                        tVBikeImgMainShowBikesAll.setText(listMainBikesAll.size() + " bikes available to rent ");
                    }

                    bikeAdapterMainShowBikesAll.notifyDataSetChanged();
                }

                else {
                    tVBikeImgMainShowBikesAll.setText("No bikes available to rent!!");
                    bikeAdapterMainShowBikesAll.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikeImageMainShowBikesAll.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        progressDialog.dismiss();
    }

    //Action of the menu onClick
    @Override
    public void onItemClick(int position) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BikeImageMainShowBikesAll.this);
        alertDialogBuilder
                .setCancelable(false)
                .setMessage("Register and Log into your account to access the:\nRent and Share Bikes services.")
                .setPositiveButton("OK", (dialog, id) -> dialog.dismiss());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
