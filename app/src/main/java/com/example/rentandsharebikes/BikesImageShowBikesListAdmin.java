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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BikesImageShowBikesListAdmin extends AppCompatActivity implements BikesAdapterShowBikesListAdmin.OnItemClickListener {

    private DatabaseReference databaseReference;
    private FirebaseStorage bikesStorage;
    private ValueEventListener bikesEventListener;

    private RecyclerView bikesListRecyclerView;
    private BikesAdapterShowBikesListAdmin bikesListAdapter;

    private TextView textViewBikesImageList;

    private List<Bikes> bikesList;

    private Button buttonAddMoreBikes, buttonBackAdminPageBikes;

    String bikeStore_Name = "";
    String bikeStore_Key = "";

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bikes_image_show_bikes_list_admin);

        getIntent().hasExtra("SName");
        bikeStore_Name = Objects.requireNonNull(getIntent().getExtras()).getString("SName");

        getIntent().hasExtra("SKey");
        bikeStore_Key = Objects.requireNonNull(getIntent().getExtras()).getString("SKey");

        textViewBikesImageList = (TextView) findViewById(R.id.tvBikeImageList);
        textViewBikesImageList.setText("No bikes available in " +bikeStore_Name+ " store");

        bikesListRecyclerView = (RecyclerView) findViewById(R.id.evRecyclerView);
        bikesListRecyclerView.setHasFixedSize(true);
        bikesListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressDialog = new ProgressDialog(this);
        bikesList = new ArrayList<>();

        progressDialog.show();

        buttonAddMoreBikes = (Button) findViewById(R.id.btnAddMoreBikes);
        buttonAddMoreBikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BikesImageShowBikesListAdmin.this, BikeStoreImageAddBikesAdmin.class));
            }
        });

        buttonBackAdminPageBikes = (Button) findViewById(R.id.btnBackAdminPageBikes);
        buttonBackAdminPageBikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BikesImageShowBikesListAdmin.this, AdminPage.class));
            }
        });
    }

    //Action on bikes onClick
    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, "Press long click to show more action: ", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpdateClick(int position) {
        Intent intent = new Intent(BikesImageShowBikesListAdmin.this, UpdateBikeDetails.class);
        Bikes selected_Bike = bikesList.get(position);
        intent.putExtra("BCondition",selected_Bike.getBike_Condition());
        intent.putExtra("BModel",selected_Bike.getBike_Model());
        intent.putExtra("BManufact",selected_Bike.getBike_Manufacturer());
        intent.putExtra("BPrice",String.valueOf(selected_Bike.getBike_Price()));
        intent.putExtra("BImage",selected_Bike.getBike_Image());
        intent.putExtra("BKey",selected_Bike.getBike_Key());
        startActivity(intent);
    }

    //Action of the menu Delete and alert dialog
    @Override
    public void onDeleteClick(final int position) {
        AlertDialog.Builder builderAlert = new AlertDialog.Builder(BikesImageShowBikesListAdmin.this);
        builderAlert.setMessage("Are sure to delete this Bike?");
        builderAlert.setCancelable(true);
        builderAlert.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Bikes selected_Bike = bikesList.get(position);
                        final String selectedKeyBike = selected_Bike.getBike_Key();
                        StorageReference imageReference = bikesStorage.getReferenceFromUrl(selected_Bike.getBike_Image());
                        imageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                databaseReference.child(selectedKeyBike).removeValue();
                                Toast.makeText(BikesImageShowBikesListAdmin.this, "The Bike has been deleted successfully ", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

        builderAlert.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert1 = builderAlert.create();
        alert1.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseReference.removeEventListener(bikesEventListener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onStart() {
        super.onStart();
        loadBikesListAdmin();
    }

    private void loadBikesListAdmin() {
        //initialize the bike storage database
        bikesStorage = FirebaseStorage.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Bikes");

        bikesEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bikesList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Bikes bikes = postSnapshot.getValue(Bikes.class);
                    assert bikes != null;
                    if (bikes.getBikeStoreKey().equals(bikeStore_Key)) {
                        bikes.setBike_Key(postSnapshot.getKey());
                        bikesList.add(bikes);
                        textViewBikesImageList.setText(bikesList.size()+" Bikes available in "+bikeStore_Name+" store");
                    }
                }
                bikesListAdapter = new BikesAdapterShowBikesListAdmin(BikesImageShowBikesListAdmin.this, bikesList);
                bikesListRecyclerView.setAdapter(bikesListAdapter);
                bikesListAdapter.setOnItmClickListener(BikesImageShowBikesListAdmin.this);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikesImageShowBikesListAdmin.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
