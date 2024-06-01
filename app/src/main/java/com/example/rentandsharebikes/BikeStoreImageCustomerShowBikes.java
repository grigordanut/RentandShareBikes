package com.example.rentandsharebikes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
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
import java.util.Objects;

public class BikeStoreImageCustomerShowBikes extends AppCompatActivity implements BikeStoreAdapterCustomer.OnItemClickListener{

    private DatabaseReference databaseReference;
    private ValueEventListener bikeStoreEventListener;

    private RecyclerView rvBikeStoreImgCustom_ShowBikes;
    private BikeStoreAdapterCustomer bikeStoreAdapterCustomer;

    private List<BikeStores> listStoresShowBikes;

    private TextView tVBikeStoreImgCustomShowBikes;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_store_image_customer_show_bikes);

        Objects.requireNonNull(getSupportActionBar()).setTitle("CUSTOMER show bikes available");

        progressDialog = new ProgressDialog(this);

        //initialize the bike store database
        databaseReference = FirebaseDatabase.getInstance().getReference("Bike Stores");

        tVBikeStoreImgCustomShowBikes = findViewById(R.id.tvBikeStoreImgCustomShowBikes);

        rvBikeStoreImgCustom_ShowBikes = (RecyclerView) findViewById(R.id.rvBikeStoreImgCustomShowBikes);
        rvBikeStoreImgCustom_ShowBikes.setHasFixedSize(true);
        rvBikeStoreImgCustom_ShowBikes.setLayoutManager(new LinearLayoutManager(this));

        listStoresShowBikes = new ArrayList<>();

        bikeStoreAdapterCustomer = new BikeStoreAdapterCustomer(BikeStoreImageCustomerShowBikes.this, listStoresShowBikes);
        rvBikeStoreImgCustom_ShowBikes.setAdapter(bikeStoreAdapterCustomer);
        bikeStoreAdapterCustomer.setOnItmClickListener(BikeStoreImageCustomerShowBikes.this);
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

                listStoresShowBikes.clear();

                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                        BikeStores bikeStores = postSnapshot.getValue(BikeStores.class);
                        assert bikeStores != null;
                        bikeStores.setBikeStore_Key(postSnapshot.getKey());
                        listStoresShowBikes.add(bikeStores);
                    }

                    tVBikeStoreImgCustomShowBikes.setText("Select the Bike Store");
                    bikeStoreAdapterCustomer.notifyDataSetChanged();
                }

                else {
                    tVBikeStoreImgCustomShowBikes.setText("No Bike Stores available");
                    bikeStoreAdapterCustomer.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikeStoreImageCustomerShowBikes.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

        progressDialog.dismiss();
    }

    @Override
    public void onItemClick(int position) {

        BikeStores selected_Store = listStoresShowBikes.get(position);
        Intent store_Intent = new Intent(BikeStoreImageCustomerShowBikes.this, BikeImageCustomerShowBikes.class);
        store_Intent.putExtra("SNameRent", selected_Store.getBikeStore_Location());
        store_Intent.putExtra("SKeyRent", selected_Store.getBikeStore_Key());
        startActivity(store_Intent);
    }
}
