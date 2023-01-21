package com.example.rentandsharebikes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
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

public class BikeImageShowBikesListCustomerAll extends AppCompatActivity implements BikeAdapterBikesCustomer.OnItemClickListener {

    private FirebaseStorage bikesStorage;
    private DatabaseReference databaseReference;
    private ValueEventListener bikesEventListener;

    private RecyclerView bikesListRecyclerView;
    private BikeAdapterBikesCustomer bikeAdapterBikesCustomer;

    private TextView tVBikeImageShowListCustomAll;

    private List<Bikes> bikesList;

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_image_show_bikes_list_customer_all);

        progressDialog = new ProgressDialog(this);
        progressDialog.show();

        tVBikeImageShowListCustomAll = (TextView) findViewById(R.id.tvBikeImageShowListCustomAll);

        bikesListRecyclerView = (RecyclerView) findViewById(R.id.evRecyclerView);
        bikesListRecyclerView.setHasFixedSize(true);
        bikesListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        bikesList = new ArrayList<>();

        bikeAdapterBikesCustomer = new BikeAdapterBikesCustomer(BikeImageShowBikesListCustomerAll.this, bikesList);
        bikesListRecyclerView.setAdapter(bikeAdapterBikesCustomer);
        bikeAdapterBikesCustomer.setOnItmClickListener(BikeImageShowBikesListCustomerAll.this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onStart() {
        super.onStart();
        loadBikesListCustomer();
    }

    private void loadBikesListCustomer() {

        //initialize the bike storage database
        bikesStorage = FirebaseStorage.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Bikes");

        bikesEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    bikesList.clear();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Bikes bikes = postSnapshot.getValue(Bikes.class);
                        assert bikes != null;
                        bikes.setBike_Key(postSnapshot.getKey());
                        bikesList.add(bikes);
                        tVBikeImageShowListCustomAll.setText(bikesList.size() + " bikes available to rent");
                    }

                    bikeAdapterBikesCustomer.notifyDataSetChanged();
                }
                else {
                    tVBikeImageShowListCustomAll.setText("No Bikes registered were found!!");
                }

                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikeImageShowBikesListCustomerAll.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Action on bikes onClick
    @Override
    public void onItemClick(final int position) {
        final String[] options = {"Rent this Bike", "Back Main Page"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, options);
        Bikes selected_Bike = bikesList.get(position);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setCancelable(false)
                .setTitle("You selected: " + selected_Bike.getBike_Model() + "\nSelect an option:")
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (which == 0) {
                            Intent intent = new Intent(BikeImageShowBikesListCustomerAll.this, RentBikesCustomer.class);
                            Bikes selected_Bike = bikesList.get(position);
                            intent.putExtra("BCondition", selected_Bike.getBike_Condition());
                            intent.putExtra("BModel", selected_Bike.getBike_Model());
                            intent.putExtra("BManufact", selected_Bike.getBike_Manufacturer());
                            intent.putExtra("BImage", selected_Bike.getBike_Image());
                            intent.putExtra("BStoreName", selected_Bike.getBikeStoreName());
                            intent.putExtra("BPrice", String.valueOf(selected_Bike.getBike_Price()));
                            intent.putExtra("BStoreKey", selected_Bike.getBikeStoreKey());
                            intent.putExtra("BKey", selected_Bike.getBike_Key());
                            startActivity(intent);
                        }

                        if (which == 1) {
                            startActivity(new Intent(BikeImageShowBikesListCustomerAll.this, CustomerPageRentBikes.class));
                            Toast.makeText(BikeImageShowBikesListCustomerAll.this, "Back to main page", Toast.LENGTH_SHORT).show();
                        }
                    }
                })

                .setNegativeButton("CLOSE",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
