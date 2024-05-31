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

public class BikeImageMainShowBikesToShare extends AppCompatActivity implements BikeAdapterMainShowBikesToShare.OnItemClickListener {

    //Display data from Share Bikes database
    private FirebaseStorage firebaseStDisplaySharedBikes;
    private DatabaseReference databaseRefDisplaySharedBikes;
    private ValueEventListener displayShareBikesEventListener;
    private TextView tVBikeImgMainShowBikesToShare;

    private RecyclerView rvBikeImgMain_ShowBikesToShare;
    private BikeAdapterMainShowBikesToShare bikeAdapterMainShowBikesToShare;

    private List<BikesShare> listBikeToShareMain;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_image_main_show_bikes_to_share);

        Objects.requireNonNull(getSupportActionBar()).setTitle("MAIN: Bikes available to share");

        progressDialog = new ProgressDialog(this);

        //Display the list of the bikes from Share Bikes database
        firebaseStDisplaySharedBikes = FirebaseStorage.getInstance();
        databaseRefDisplaySharedBikes = FirebaseDatabase.getInstance().getReference("Share Bikes");

        tVBikeImgMainShowBikesToShare = findViewById(R.id.tvBikeImgMainShowBikesToShare);

        rvBikeImgMain_ShowBikesToShare = findViewById(R.id.rvBikeImgMainShowBikesToShare);
        rvBikeImgMain_ShowBikesToShare.setHasFixedSize(true);
        rvBikeImgMain_ShowBikesToShare.setLayoutManager(new LinearLayoutManager(this));

        listBikeToShareMain = new ArrayList<>();

        bikeAdapterMainShowBikesToShare = new BikeAdapterMainShowBikesToShare(BikeImageMainShowBikesToShare.this, listBikeToShareMain);
        rvBikeImgMain_ShowBikesToShare.setAdapter(bikeAdapterMainShowBikesToShare);
        bikeAdapterMainShowBikesToShare.setOnItmClickListener(BikeImageMainShowBikesToShare.this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onStart() {
        super.onStart();
        loadSharedBikesMain();
    }

    public void loadSharedBikesMain() {

        progressDialog.show();

        displayShareBikesEventListener = databaseRefDisplaySharedBikes.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                listBikeToShareMain.clear();

                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        BikesShare share_Bikes = postSnapshot.getValue(BikesShare.class);
                        assert share_Bikes != null;
                        share_Bikes.setShareBike_Key(postSnapshot.getKey());
                        listBikeToShareMain.add(share_Bikes);
                        tVBikeImgMainShowBikesToShare.setText(listBikeToShareMain.size() + " Bikes available to share");
                    }

                    bikeAdapterMainShowBikesToShare.notifyDataSetChanged();
                }

                else {
                    tVBikeImgMainShowBikesToShare.setText("No Bikes available to share");
                    bikeAdapterMainShowBikesToShare.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikeImageMainShowBikesToShare.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        progressDialog.dismiss();
    }

    @Override
    public void onItemClick(final int position) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BikeImageMainShowBikesToShare.this);
        alertDialogBuilder
                .setMessage("Register and Log into your account to access the:\nRent and Share Bikes services.")
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, id) -> dialog.dismiss());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
