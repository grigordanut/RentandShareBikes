package com.example.rentandsharebikes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
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

public class BikesImageCustomer extends AppCompatActivity {

    private FirebaseStorage bikesStorage;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private ValueEventListener bikesListDBEventListener;

    private RecyclerView bikesListRecyclerView;
    private BikesAdapterCustomer bikesListAdapterCustomer;

    private TextView textViewBikesImageList;

    private List<Bikes> bikesList;

    private ProgressDialog progressDialog;
    String storeName ="";

    @SuppressLint({"NewApi", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bikes_image_customer);

        getIntent().hasExtra("SName");
        storeName = Objects.requireNonNull(getIntent().getExtras()).getString("SName");

        textViewBikesImageList = (TextView)findViewById(R.id.tvBikeImageList);
        textViewBikesImageList.setText("List of Bikes in "+storeName+" store");

        firebaseDatabase = FirebaseDatabase.getInstance();

        bikesListRecyclerView = (RecyclerView) findViewById(R.id.evRecyclerView);
        bikesListRecyclerView.setHasFixedSize(true);
        bikesListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressDialog = new ProgressDialog(this);
        bikesList = new ArrayList<>();

        progressDialog.show();

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

                bikesListAdapterCustomer = new BikesAdapterCustomer(BikesImageCustomer.this, bikesList);
                bikesListRecyclerView.setAdapter(bikesListAdapterCustomer);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
