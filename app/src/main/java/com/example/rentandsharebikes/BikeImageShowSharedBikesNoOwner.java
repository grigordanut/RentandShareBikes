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
import java.util.Objects;

public class BikeImageShowSharedBikesNoOwner extends AppCompatActivity implements BikeAdapterShowSharedBikesNoOwner.OnItemClickListener{

    //Display data from Share Bikes database
    private FirebaseStorage firebaseStDisplaySharedBikes;
    private DatabaseReference databaseRefDisplaySharedBikes;
    private ValueEventListener displayShareBikesEventListener;
    private TextView tVBikesImgShowBikesSNoOwn;

    private RecyclerView bikesListRecyclerView;
    private BikeAdapterShowSharedBikesNoOwner bikeAdapterShowSharedBikesNoOwner;

    private List<BikesShare> sharedBikesList;

    String customShareAll_Id = "";

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_image_show_shared_bikes_no_owner);

        progressDialog = new ProgressDialog(this);
        progressDialog.show();

        getIntent().hasExtra("CIdNoOwner");
        customShareAll_Id = Objects.requireNonNull(getIntent().getExtras()).getString("CIdNoOwner");

        tVBikesImgShowBikesSNoOwn = (TextView)findViewById(R.id.tvBikesImgShowBikesSNoOwn);

        bikesListRecyclerView = (RecyclerView) findViewById(R.id.evRecyclerView);
        bikesListRecyclerView.setHasFixedSize(true);
        bikesListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        sharedBikesList = new ArrayList<>();

        bikeAdapterShowSharedBikesNoOwner = new BikeAdapterShowSharedBikesNoOwner(BikeImageShowSharedBikesNoOwner.this, sharedBikesList);
        bikesListRecyclerView.setAdapter(bikeAdapterShowSharedBikesNoOwner);
        bikeAdapterShowSharedBikesNoOwner.setOnItmClickListener(BikeImageShowSharedBikesNoOwner.this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onStart() {
        super.onStart();
        loadSharedBikesNoOwner();
    }

    public void loadSharedBikesNoOwner() {
        //Display the list of the bikes from Share Bikes database
        firebaseStDisplaySharedBikes = FirebaseStorage.getInstance();
        databaseRefDisplaySharedBikes = FirebaseDatabase.getInstance().getReference("Share Bikes");

        displayShareBikesEventListener = databaseRefDisplaySharedBikes.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                sharedBikesList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    BikesShare share_Bikes = postSnapshot.getValue(BikesShare.class);
                    assert share_Bikes != null;
                    if (!share_Bikes.getShareBikes_CustomId().equals(customShareAll_Id)) {
                        share_Bikes.setShareBike_Key(postSnapshot.getKey());
                        sharedBikesList.add(share_Bikes);
                        tVBikesImgShowBikesSNoOwn.setText(sharedBikesList.size()+" bikes available to be shared");
                    }
                }

                bikeAdapterShowSharedBikesNoOwner.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikeImageShowSharedBikesNoOwner.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(final int position) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BikeImageShowSharedBikesNoOwner.this);
        alertDialogBuilder
                .setMessage("Contact the owner if you like to share this bike")
                .setCancelable(false)
                .setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alert1 = alertDialogBuilder.create();
        alert1.show();
    }
}
