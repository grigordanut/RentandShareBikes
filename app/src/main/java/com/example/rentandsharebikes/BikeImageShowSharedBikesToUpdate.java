package com.example.rentandsharebikes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
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

public class BikeImageShowSharedBikesToUpdate extends AppCompatActivity {

    //Display data from database
    private FirebaseStorage firebaseStShareUp;
    private DatabaseReference databaseRefShareUp;
    private ValueEventListener shareUpBikesEventListener;

    private RecyclerView bikesListRecyclerView;
    private BikeAdapterShowSharedBikesToUpdate bikesAdapterShowOwnSharedBikesToUpdate;

    private List<BikesShare> upBikesShareList;

    String customShare_Id = "";

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_image_show_shared_bikes_to_update);

        progressDialog = new ProgressDialog(this);
        progressDialog.show();

        getIntent().hasExtra("CIdUpdate");
        customShare_Id = Objects.requireNonNull(getIntent().getExtras()).getString("CIdUpdate");

        bikesListRecyclerView = (RecyclerView) findViewById(R.id.evRecyclerView);
        bikesListRecyclerView.setHasFixedSize(true);
        bikesListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        upBikesShareList = new ArrayList<>();

        bikesAdapterShowOwnSharedBikesToUpdate = new BikeAdapterShowSharedBikesToUpdate(BikeImageShowSharedBikesToUpdate.this, upBikesShareList);
        bikesListRecyclerView.setAdapter(bikesAdapterShowOwnSharedBikesToUpdate);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onStart() {
        super.onStart();
        loadBikeSharedToUpdateCustomer();
    }

    private void loadBikeSharedToUpdateCustomer() {
        //initialize the bike storage database
        firebaseStShareUp = FirebaseStorage.getInstance();
        databaseRefShareUp = FirebaseDatabase.getInstance().getReference("Share Bikes");

        shareUpBikesEventListener = databaseRefShareUp.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                upBikesShareList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    BikesShare share_UpBikes = postSnapshot.getValue(BikesShare.class);

                    assert share_UpBikes != null;
                    if (share_UpBikes.getShareBikes_CustomId().equals(customShare_Id)) {
                        share_UpBikes.setShareBike_Key(postSnapshot.getKey());
                        upBikesShareList.add(share_UpBikes);
                    }
                }

                bikesAdapterShowOwnSharedBikesToUpdate.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikeImageShowSharedBikesToUpdate.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
