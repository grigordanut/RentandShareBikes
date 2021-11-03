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

public class BikesImageShowBikesListCustomAll extends AppCompatActivity implements BikesAdapterShowBikesListCustomAll.OnItemClickListener {

    private FirebaseStorage bikesStorage;
    private DatabaseReference databaseReference;
    private ValueEventListener bikesEventListener;

    private RecyclerView bikesListRecyclerView;
    private BikesAdapterShowBikesListCustomAll bikesAdapterShowBikesListCustomAll;

    private TextView tVBikeImageShowListCustomAll;

    private List<BikesRent> bikesRentList;

    private ProgressDialog progressDialog;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bikes_image_show_bikes_list_custom_all);

        tVBikeImageShowListCustomAll = (TextView) findViewById(R.id.tvBikeImageShowListCustomAll);

        bikesListRecyclerView = (RecyclerView) findViewById(R.id.evRecyclerView);
        bikesListRecyclerView.setHasFixedSize(true);
        bikesListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressDialog = new ProgressDialog(this);
        bikesRentList = new ArrayList<>();

        progressDialog.show();
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
        databaseReference = FirebaseDatabase.getInstance().getReference("Bikes");

        bikesEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bikesRentList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    BikesRent bikesRent = postSnapshot.getValue(BikesRent.class);
                    assert bikesRent != null;
                    bikesRent.setBike_Key(postSnapshot.getKey());
                    bikesRentList.add(bikesRent);
                    tVBikeImageShowListCustomAll.setText(bikesRentList.size() + " bikes available to rent");
                }
                bikesAdapterShowBikesListCustomAll = new BikesAdapterShowBikesListCustomAll(BikesImageShowBikesListCustomAll.this, bikesRentList);
                bikesListRecyclerView.setAdapter(bikesAdapterShowBikesListCustomAll);
                bikesAdapterShowBikesListCustomAll.setOnItmClickListener(BikesImageShowBikesListCustomAll.this);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikesImageShowBikesListCustomAll.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Action on bikes onClick
    @Override
    public void onItemClick(final int position) {
        final String[] options = {"Rent this Bike", "Back Main Page"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, options);
        BikesRent selected_Bike = bikesRentList.get(position);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setCancelable(false)
                .setTitle("You selected "+selected_Bike.getBike_Model()+"\nSelect an option:")
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (which == 0) {
                    Intent intent = new Intent(BikesImageShowBikesListCustomAll.this, RentBikesCustomer.class);
                    BikesRent selected_Bike = bikesRentList.get(position);
                    intent.putExtra("BCondition", selected_Bike.getBike_Condition());
                    intent.putExtra("BModel", selected_Bike.getBike_Model());
                    intent.putExtra("BManufact", selected_Bike.getBike_Manufacturer());
                    intent.putExtra("BImage", selected_Bike.getBike_Image());
                    intent.putExtra("BStoreName", selected_Bike.getBikeStoreName());
                    intent.putExtra("BPrice", String.valueOf(selected_Bike.getBike_Price()));
                    intent.putExtra("BStoreKey",selected_Bike.getBikeStoreKey());
                    intent.putExtra("BKey", selected_Bike.getBike_Key());
                    startActivity(intent);
                }

                if (which == 1) {
                    startActivity(new Intent(BikesImageShowBikesListCustomAll.this, CustomerPageRentBikes.class));
                    Toast.makeText(BikesImageShowBikesListCustomAll.this, "Back to main page", Toast.LENGTH_SHORT).show();
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
