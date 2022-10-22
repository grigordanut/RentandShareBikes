package com.example.rentandsharebikes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class BikeImageShowSharedBikesOwner extends AppCompatActivity {

    //Display data from database
    private FirebaseStorage bikesStorageShare;
    private DatabaseReference databaseRefShare;

    //Delete data from database
    private FirebaseStorage bikesStorageDelete;
    private DatabaseReference databaseRefDeleteBike;
    private ValueEventListener shareBikesEventListener;

    private RecyclerView bikesListRecyclerView;
    private BikeAdapterShowSharedBikesOwner bikeAdapterShowSharedBikesOwner;

    private TextView tVCustomerShareBikes;

    private List<BikesShare> bikesShareList;

    String customShareFirst_Name = "";
    String customShareLast_Name = "";
    String customShare_Id = "";

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_image_show_shared_bikes_owner);

        progressDialog = new ProgressDialog(this);
        progressDialog.show();

        bikesStorageShare = FirebaseStorage.getInstance();
        databaseRefShare = FirebaseDatabase.getInstance().getReference("Share Bikes");

        tVCustomerShareBikes = (TextView) findViewById(R.id.tvBikesImageShowBikesSharedOwn);

        getIntent().hasExtra("CFNameShare");
        customShareFirst_Name = Objects.requireNonNull(getIntent().getExtras()).getString("CFNameShare");

        getIntent().hasExtra("CLNameShare");
        customShareLast_Name = Objects.requireNonNull(getIntent().getExtras()).getString("CLNameShare");

        tVCustomerShareBikes.setText("No bikes added by "+customShareFirst_Name+" "+customShareLast_Name);

        getIntent().hasExtra("CIdShare");
        customShare_Id = Objects.requireNonNull(getIntent().getExtras()).getString("CIdShare");

        bikesListRecyclerView = (RecyclerView) findViewById(R.id.evRecyclerView);
        bikesListRecyclerView.setHasFixedSize(true);
        bikesListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        bikesShareList = new ArrayList<>();

        bikeAdapterShowSharedBikesOwner = new BikeAdapterShowSharedBikesOwner(BikeImageShowSharedBikesOwner.this, bikesShareList);
        bikesListRecyclerView.setAdapter(bikeAdapterShowSharedBikesOwner);

        Button buttonAddSBikes = findViewById(R.id.btnAddMoreShareBikes);
        buttonAddSBikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BikeImageShowSharedBikesOwner.this, AddBikeShare.class));
            }
        });

        Button buttonBackSBikesPage = findViewById(R.id.btnBackShareBikesPage);
        buttonBackSBikesPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BikeImageShowSharedBikesOwner.this, CustomerPageShareBikes.class));
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onStart() {
        super.onStart();
        loadSharedBikesOwner();
    }

    public void loadSharedBikesOwner(){
        shareBikesEventListener = databaseRefShare.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bikesShareList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    BikesShare share_Bikes = postSnapshot.getValue(BikesShare.class);

                    assert share_Bikes != null;
                    if(share_Bikes.getShareBikes_CustomId().equals(customShare_Id)){
                        share_Bikes.setShareBike_Key(postSnapshot.getKey());
                        bikesShareList.add(share_Bikes);
                        tVCustomerShareBikes.setText(bikesShareList.size() + " Bikes added by " + customShareFirst_Name+" "+customShareLast_Name);
                    }
                }

                bikeAdapterShowSharedBikesOwner.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikeImageShowSharedBikesOwner.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
