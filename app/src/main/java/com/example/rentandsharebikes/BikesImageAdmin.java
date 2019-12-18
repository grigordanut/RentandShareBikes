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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BikesImageAdmin extends AppCompatActivity {

    private FirebaseStorage bikesStorage;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private ValueEventListener bikesListDBEventListener;

    private RecyclerView bikesListRecyclerView;
    private BikesAdapterAdmin bikesListAdapter;

    private TextView textViewBikesImageList;

    private List<Bikes> bikesList;

    private ProgressDialog progressDialog;
    private Button buttonAddMoreBikes, buttonBackAdminPageBikes;

    String storeName ="";


    @SuppressLint({"NewApi", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bikes_image_admin);

        getIntent().hasExtra("SName");
        storeName = Objects.requireNonNull(getIntent().getExtras()).getString("SName");


        textViewBikesImageList = (TextView)findViewById(R.id.tvBikeImageList);
        textViewBikesImageList.setText("List of Bikes in " +storeName+" store");


        firebaseDatabase = FirebaseDatabase.getInstance();

        bikesListRecyclerView = (RecyclerView) findViewById(R.id.evRecyclerView);
        bikesListRecyclerView.setHasFixedSize(true);
        bikesListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressDialog = new ProgressDialog(this);
        bikesList = new ArrayList<>();

        progressDialog.show();

        buttonAddMoreBikes = (Button)findViewById(R.id.btnAddMoreBikes);
        buttonAddMoreBikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BikesImageAdmin.this, BikeStoreImageAddBikes.class));
            }
        });

        buttonBackAdminPageBikes = (Button)findViewById(R.id.btnBackAdminPageBikes);
        buttonBackAdminPageBikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BikesImageAdmin.this, AdminPage.class));
            }
        });

        //check if the bikes list is empty and add a new bike
        if(databaseReference == null){
            bikesStorage = FirebaseStorage.getInstance();
            databaseReference = FirebaseDatabase.getInstance().getReference("Bikes");
        }

        bikesListDBEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Bikes bikes = postSnapshot.getValue(Bikes.class);
                    assert bikes != null;
                    if(bikes.getBikeStoreName().equals(storeName)){
                        bikes.setBikesKey(postSnapshot.getKey());
                        bikesList.add(bikes);
                    }
                }

                bikesListAdapter = new BikesAdapterAdmin(BikesImageAdmin.this, bikesList);
                bikesListRecyclerView.setAdapter(bikesListAdapter);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
