package com.example.rentandsharebikes;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BikeStoreImageReturnBikeDifferentStore extends AppCompatActivity implements BikeStoreAdapterReturnBikeDifferentStore.OnItemClickListener {
    private TextView textViewBikeStoresImageReturnBikeDiffStore;
    private DatabaseReference databaseReference;
    private ValueEventListener bikeStoreEventListener;

    private RecyclerView bikeStoreRecyclerView;
    private BikeStoreAdapterReturnBikeDifferentStore bikeStoreAdapterReturnBikeDifferentStore;

    public List<BikeStore> bikeStoreList;

    private ProgressDialog progressDialog;

    String bike_StoreReturnBikesSame,  bike_StoreReturnBikesDiff, bike_KeyReturnBikeSame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_store_image_return_bike_different_store);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            bike_StoreReturnBikesSame = bundle.getString("BStoreSame");
            bike_KeyReturnBikeSame = bundle.getString("BKey");
        }

        textViewBikeStoresImageReturnBikeDiffStore = (TextView)findViewById(R.id.tvBikeStoresReturnDiffStore);
        textViewBikeStoresImageReturnBikeDiffStore.setText("No Bike Stores available");

        bikeStoreRecyclerView = (RecyclerView) findViewById(R.id.evRecyclerView);
        bikeStoreRecyclerView.setHasFixedSize(true);
        bikeStoreRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressDialog = new ProgressDialog(this);
        bikeStoreList = new ArrayList<BikeStore>();

        progressDialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        loadBikeStoresListAdmin();
    }

    private void loadBikeStoresListAdmin() {
        //initialize the bike store database
        databaseReference = FirebaseDatabase.getInstance().getReference("Bike Stores");

        bikeStoreEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bikeStoreList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    BikeStore bikeStore = postSnapshot.getValue(BikeStore.class);
                    assert bikeStore != null;
                    bikeStore.setStoreKey(postSnapshot.getKey());
                    bikeStoreList.add(bikeStore);
                    textViewBikeStoresImageReturnBikeDiffStore.setText("Bike Stores available " +bikeStoreList.size());
                }
                bikeStoreAdapterReturnBikeDifferentStore = new BikeStoreAdapterReturnBikeDifferentStore (BikeStoreImageReturnBikeDifferentStore.this, bikeStoreList);
                bikeStoreRecyclerView.setAdapter(bikeStoreAdapterReturnBikeDifferentStore);
                bikeStoreAdapterReturnBikeDifferentStore.setOnItmClickListener(BikeStoreImageReturnBikeDifferentStore.this);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikeStoreImageReturnBikeDifferentStore.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Action of the menu onClick
    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(BikeStoreImageReturnBikeDifferentStore.this, ReturnRentedBikes.class);
        BikeStore selected_BikeStore = bikeStoreList.get(position);
        intent.putExtra("BStoreDiff", selected_BikeStore.getBikeStore_Location());
        intent.putExtra("BStoreSame", bike_StoreReturnBikesSame);
        intent.putExtra("BKey", bike_KeyReturnBikeSame);
        startActivity(intent);
    }
}
