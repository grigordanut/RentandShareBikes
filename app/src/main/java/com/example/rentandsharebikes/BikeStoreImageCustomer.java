package com.example.rentandsharebikes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BikeStoreImageCustomer extends AppCompatActivity {

    private TextView textViewBikeStore;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ValueEventListener bikeStoreDBEventListener;

    private RecyclerView bikeStoreRecyclerView;
    private BikeStoreAdapterShowStoreListCustomer bikeStoreAdapterShowStoreListCustomer;

    private List<BikeStore> bikeStoreList;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_store_image_customer);

        firebaseDatabase = FirebaseDatabase.getInstance();

        bikeStoreRecyclerView = (RecyclerView) findViewById(R.id.evRecyclerView);
        bikeStoreRecyclerView.setHasFixedSize(true);
        bikeStoreRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressDialog = new ProgressDialog(this);
        bikeStoreList = new ArrayList<>();

        progressDialog.show();

        //check if the bikes list is empty and add a new bike
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

                bikeStoreAdapterShowStoreListCustomer = new BikeStoreAdapterShowStoreListCustomer(BikeStoreImageCustomer.this, bikeStoreList);
                bikeStoreRecyclerView.setAdapter(bikeStoreAdapterShowStoreListCustomer);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikeStoreImageCustomer.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}
