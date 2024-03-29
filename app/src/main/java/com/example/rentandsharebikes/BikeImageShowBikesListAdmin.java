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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class BikeImageShowBikesListAdmin extends AppCompatActivity implements BikeAdapterBikesAdmin.OnItemClickListener {

    //Display the Bikes available in that Bike Store
    private DatabaseReference dbRefBikes;
    private FirebaseStorage bikesStorage;
    private ValueEventListener bikesEventListener;

    private RecyclerView bikesListRecyclerView;
    private BikeAdapterBikesAdmin bikeAdapterBikesAdmin;

    private TextView tVBikeListAdmin;

    private List<Bikes> bikesList;

    private Button buttonAddMoreBikes, buttonBackAdminPageBikes;

    private String bikeStore_Name = "";
    private String bikeStore_Key = "";

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_image_show_bikes_list_admin);

        progressDialog = new ProgressDialog(this);
        progressDialog.show();

        getIntent().hasExtra("SName");
        bikeStore_Name = Objects.requireNonNull(getIntent().getExtras()).getString("SName");

        getIntent().hasExtra("SKey");
        bikeStore_Key = Objects.requireNonNull(getIntent().getExtras()).getString("SKey");

        tVBikeListAdmin = findViewById(R.id.tvBikeListAdmin);

        bikesListRecyclerView = findViewById(R.id.evRecyclerView);
        bikesListRecyclerView.setHasFixedSize(true);
        bikesListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        bikesList = new ArrayList<>();

        bikeAdapterBikesAdmin = new BikeAdapterBikesAdmin(BikeImageShowBikesListAdmin.this, bikesList);
        bikesListRecyclerView.setAdapter(bikeAdapterBikesAdmin);
        bikeAdapterBikesAdmin.setOnItmClickListener(BikeImageShowBikesListAdmin.this);

        buttonAddMoreBikes = findViewById(R.id.btnAddMoreBikes);
        buttonAddMoreBikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BikeImageShowBikesListAdmin.this, BikeStoreImageAddBikesAdmin.class));
            }
        });

        buttonBackAdminPageBikes = findViewById(R.id.btnBackAdminPageBikes);
        buttonBackAdminPageBikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BikeImageShowBikesListAdmin.this, AdminPage.class));
            }
        });
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
        dbRefBikes = FirebaseDatabase.getInstance().getReference("Bikes");

        bikesEventListener = dbRefBikes.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bikesList.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Bikes bikes = postSnapshot.getValue(Bikes.class);
                        assert bikes != null;
                        bikes.setBike_Key(postSnapshot.getKey());
                        String storeKey = bikes.getBikeStoreKey();

                        if (storeKey.equals(bikeStore_Key)) {
                            bikesList.add(bikes);
                            tVBikeListAdmin.setText(bikesList.size() + " Bikes available in " + bikeStore_Name + " store");
                        }

                        if (bikesList.size() == 0) {
                            tVBikeListAdmin.setText("No bikes available in " + bikeStore_Name + " store");
                        }
                    }

                    bikeAdapterBikesAdmin.notifyDataSetChanged();
                } else {
                    alertDialogNoBikesAvailable();
                    tVBikeListAdmin.setText("There are not Bikes registered!!");
                }

                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikeImageShowBikesListAdmin.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Action on bikes onClick
    @Override
    public void onItemClick(final int position) {
        showOptionMenu(position);
    }

    public void showOptionMenu(final int position) {
        final String[] options = {"Update this Bike", "Delete this Bike"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, options);
        Bikes selected_Bike = bikesList.get(position);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setCancelable(false)
                .setTitle("You selected: " + selected_Bike.getBike_Model() + "\nSelect an option:")
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (which == 0) {
                            updateBikes(position);
                        }
                        if (which == 1) {
                            confirmDeletion(position);
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

    public void updateBikes(final int position) {
        Intent intent = new Intent(BikeImageShowBikesListAdmin.this, UpdateBikeDetails.class);
        Bikes selected_Bike = bikesList.get(position);
        intent.putExtra("BCondition", selected_Bike.getBike_Condition());
        intent.putExtra("BModel", selected_Bike.getBike_Model());
        intent.putExtra("BManufact", selected_Bike.getBike_Manufacturer());
        intent.putExtra("BPrice", String.valueOf(selected_Bike.getBike_Price()));
        intent.putExtra("BImage", selected_Bike.getBike_Image());
        intent.putExtra("BKey", selected_Bike.getBike_Key());
        startActivity(intent);
    }

    public void confirmDeletion(final int position) {
        Bikes selected_Bike = bikesList.get(position);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BikeImageShowBikesListAdmin.this);
        alertDialogBuilder
                .setTitle("Delete bike from Bike Store!!")
                .setMessage("Are sure to delete the " + selected_Bike.getBike_Model() + " Bike?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, which) -> {
                            final String selectedKeyBike = selected_Bike.getBike_Key();
                            StorageReference imageReference = bikesStorage.getReferenceFromUrl(selected_Bike.getBike_Image());
                            imageReference.delete().addOnSuccessListener(aVoid -> {
                                dbRefBikes.child(selectedKeyBike).removeValue();
                                Toast.makeText(BikeImageShowBikesListAdmin.this, "The Bike has been deleted successfully ", Toast.LENGTH_SHORT).show();
                            });
                        })

                .setNegativeButton("CANCEL", (dialog, which) -> dialog.cancel());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbRefBikes.removeEventListener(bikesEventListener);
    }

    public void alertDialogNoBikesAvailable() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setTitle("There are not Bikes available!!")
                .setMessage("Would you like to add bikes?")
                .setPositiveButton("YES", (dialog, id) -> {
                    finish();
                    Intent intent = new Intent(BikeImageShowBikesListAdmin.this, BikeStoreImageAddBikesAdmin.class);
                    startActivity(intent);
                })

                .setNegativeButton("NO", (dialog, id) -> {
                    Intent intent = new Intent(BikeImageShowBikesListAdmin.this, AdminPage.class);
                    startActivity(intent);
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
