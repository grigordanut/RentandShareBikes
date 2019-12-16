package com.example.rentandsharebikes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BikeStoreImageShowStoreList extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ValueEventListener bikeStoreDBEventListener;

    private RecyclerView bikeStoreRecyclerView;
    private BikeStoreAdapterShowStoreList bikeStoreAdapterShowStoreList;

    private List<BikeStore> bikeStoreList;

    private ProgressDialog progressDialog;
    private Button buttonAddBikeStore, buttonBackAdminPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_store_image_show_store_list);

        firebaseDatabase = FirebaseDatabase.getInstance();

        bikeStoreRecyclerView = (RecyclerView) findViewById(R.id.evRecyclerView);
        bikeStoreRecyclerView.setHasFixedSize(true);
        bikeStoreRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressDialog = new ProgressDialog(this);
        bikeStoreList = new ArrayList<>();

        progressDialog.show();

        buttonAddBikeStore = (Button)findViewById(R.id.btnAddBikesStores);
        buttonAddBikeStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BikeStoreImageShowStoreList.this, AddBikeStore.class));
            }
        });

        buttonBackAdminPage = (Button)findViewById(R.id.btnBackAdminPage);
        buttonBackAdminPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BikeStoreImageShowStoreList.this, AdminPage.class));
            }
        });

        //check if the bikes store list is empty and add a new bike store
        if(databaseReference == null){
            databaseReference = FirebaseDatabase.getInstance().getReference("Bike stores");
        }

        bikeStoreDBEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    BikeStore bikeStore = postSnapshot.getValue(BikeStore.class);
                    assert bikeStore != null;
                    bikeStore.setStoreKey(postSnapshot.getKey());
                    bikeStoreList.add(bikeStore);
                }

                bikeStoreAdapterShowStoreList = new BikeStoreAdapterShowStoreList(BikeStoreImageShowStoreList.this, bikeStoreList);
                bikeStoreRecyclerView.setAdapter(bikeStoreAdapterShowStoreList);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
