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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class BikeImageShowBikesListAdminAll extends AppCompatActivity implements BikeAdapterBikesAdmin.OnItemClickListener {

    private DatabaseReference databaseReference;
    private FirebaseStorage bikesStorage;
    private ValueEventListener bikesEventListener;

    private RecyclerView bikesListRecyclerView;
    private BikeAdapterBikesAdmin bikeAdapterBikesAdmin;

    private TextView tVBikeListAdminAll;

    private List<Bikes> bikesList;

    private Button buttonAddMoreBikesFull, buttonBackAdminPageBikesFull;

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_image_show_bikes_list_admin_all);

        progressDialog = new ProgressDialog(this);
        progressDialog.show();

        tVBikeListAdminAll = findViewById(R.id.tvBikeListAdminAll);
        tVBikeListAdminAll.setText("No bikes available");

        bikesListRecyclerView = findViewById(R.id.evRecyclerView);
        bikesListRecyclerView.setHasFixedSize(true);
        bikesListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        bikesList = new ArrayList<>();

        bikeAdapterBikesAdmin = new BikeAdapterBikesAdmin(BikeImageShowBikesListAdminAll.this, bikesList);
        bikesListRecyclerView.setAdapter(bikeAdapterBikesAdmin);
        bikeAdapterBikesAdmin.setOnItmClickListener(BikeImageShowBikesListAdminAll.this);

        buttonAddMoreBikesFull = findViewById(R.id.btnAddMoreBikesFull);
        buttonAddMoreBikesFull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BikeImageShowBikesListAdminAll.this, BikeStoreImageAddBikesAdmin.class));
            }
        });

        buttonBackAdminPageBikesFull = findViewById(R.id.btnBackAdminPageBikesFull);
        buttonBackAdminPageBikesFull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BikeImageShowBikesListAdminAll.this, AdminPage.class));
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
                        tVBikeListAdminAll.setText(bikesList.size() + " Bikes available");
                    }

                    bikeAdapterBikesAdmin.notifyDataSetChanged();
                }
                else{
                    tVBikeListAdminAll.setText("No Bikes registered were found!!");
                }

                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikeImageShowBikesListAdminAll.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Action on bikes onClick
    @Override
    public void onItemClick(final int position) {
        final String[] options = {"Update this Bike", "Delete this Bike"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, options);
        Bikes selected_Bike = bikesList.get(position);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setCancelable(false)
                .setTitle("You selected: " + selected_Bike.getBike_Model() + "\nSelect an option:")
                .setAdapter(adapter, (dialog, id) -> {
                    if (id == 0) {
                        updateBikeDetails(position);
                    }
                    if (id == 1) {
                        confirmBikeDeletion(position);
                    }
                })
                .setNegativeButton("CLOSE", (dialog, id) -> dialog.dismiss());

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void updateBikeDetails(final int position) {
        Intent intent = new Intent(BikeImageShowBikesListAdminAll.this, UpdateBikeDetails.class);
        Bikes selected_Bike = bikesList.get(position);
        intent.putExtra("BCondition", selected_Bike.getBike_Condition());
        intent.putExtra("BModel", selected_Bike.getBike_Model());
        intent.putExtra("BManufact", selected_Bike.getBike_Manufacturer());
        intent.putExtra("BPrice", String.valueOf(selected_Bike.getBike_Price()));
        intent.putExtra("BImage", selected_Bike.getBike_Image());
        intent.putExtra("BKey", selected_Bike.getBike_Key());
        startActivity(intent);
    }

    public void confirmBikeDeletion(final int position) {
        Bikes selected_Bike = bikesList.get(position);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BikeImageShowBikesListAdminAll.this);
        alertDialogBuilder
                .setMessage("Are sure to delete the " + selected_Bike.getBike_Model() + " Bike?")
                .setCancelable(true)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Bikes selected_Bike = bikesList.get(position);
                                final String selectedKeyBike = selected_Bike.getBike_Key();
                                StorageReference imageReference = bikesStorage.getReferenceFromUrl(selected_Bike.getBike_Image());
                                imageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        databaseReference.child(selectedKeyBike).removeValue();
                                        Toast.makeText(BikeImageShowBikesListAdminAll.this, "The Bike has been successfully deleted. ", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        })

                .setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alert1 = alertDialogBuilder.create();
        alert1.show();
    }
}
