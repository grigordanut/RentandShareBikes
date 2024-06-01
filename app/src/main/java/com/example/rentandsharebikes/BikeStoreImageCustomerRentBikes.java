package com.example.rentandsharebikes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
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

public class BikeStoreImageCustomerRentBikes extends AppCompatActivity implements BikeStoreAdapterCustomer.OnItemClickListener{

    private DatabaseReference databaseReference;
    private ValueEventListener bikeStoreEventListener;

    private RecyclerView rvBikeStoreImgCustom_RentBikes;
    private BikeStoreAdapterCustomer bikeStoreAdapterCustomer;

    private TextView tVBikeStoreImgCustomRentBikes;

    private List<BikeStores> listBikeStoresRentBikes;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_store_image_customer_rent_bikes);

        progressDialog = new ProgressDialog(this);

        //initialize the bike store database
        databaseReference = FirebaseDatabase.getInstance().getReference("Bike Stores");

        tVBikeStoreImgCustomRentBikes = findViewById(R.id.tvBikeStoreImgCustomRentBikes);

        rvBikeStoreImgCustom_RentBikes = findViewById(R.id.rvBikeStoreImgCustomRentBikes);
        rvBikeStoreImgCustom_RentBikes.setHasFixedSize(true);
        rvBikeStoreImgCustom_RentBikes.setLayoutManager(new LinearLayoutManager(this));

        listBikeStoresRentBikes = new ArrayList<>();

        bikeStoreAdapterCustomer = new BikeStoreAdapterCustomer(BikeStoreImageCustomerRentBikes.this, listBikeStoresRentBikes);
        rvBikeStoreImgCustom_RentBikes.setAdapter(bikeStoreAdapterCustomer);
        bikeStoreAdapterCustomer.setOnItmClickListener(BikeStoreImageCustomerRentBikes.this);
    }

    @Override
    public void onStart() {
        super.onStart();
        loadBikeStoresListCustomer();
    }

    private void loadBikeStoresListCustomer(){

        progressDialog.show();

        bikeStoreEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                listBikeStoresRentBikes.clear();

                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                        BikeStores bikeStores = postSnapshot.getValue(BikeStores.class);
                        assert bikeStores != null;
                        bikeStores.setBikeStore_Key(postSnapshot.getKey());
                        listBikeStoresRentBikes.add(bikeStores);
                        tVBikeStoreImgCustomRentBikes.setText("Select the Bike Store");
                    }

                    bikeStoreAdapterCustomer.notifyDataSetChanged();
                }

                else {
                    tVBikeStoreImgCustomRentBikes.setText("No Bike Stores available");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikeStoreImageCustomerRentBikes.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

        progressDialog.dismiss();
    }

    @Override
    public void onItemClick(int position) {

        BikeStores selected_Store = listBikeStoresRentBikes.get(position);
        Intent store_Intent = new Intent(BikeStoreImageCustomerRentBikes.this, BikeImageCustomerRentBikes.class);
        store_Intent.putExtra("SName", selected_Store.getBikeStore_Location());
        store_Intent.putExtra("SKey", selected_Store.getBikeStore_Key());
        startActivity(store_Intent);
    }
}
