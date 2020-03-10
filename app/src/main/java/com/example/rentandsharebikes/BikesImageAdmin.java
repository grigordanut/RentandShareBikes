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

public class BikesImageAdmin extends AppCompatActivity implements BikesAdapterAdmin.OnItemClickListener {

    private DatabaseReference databaseReference;
    private FirebaseStorage bikeStorage;
    private ValueEventListener bikesDBEventListener;

    private RecyclerView bikesListRecyclerView;
    private BikesAdapterAdmin bikesListAdapter;

    private TextView textViewBikesImageList;

    public List<Bikes> bikesList;

    private Button buttonAddMoreBikes, buttonBackAdminPageBikes;

    String bikeStore_Name = "";

    private ProgressDialog progressDialog;

    @SuppressLint({"NewApi", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bikes_image_admin);

        getIntent().hasExtra("SName");
        bikeStore_Name = Objects.requireNonNull(getIntent().getExtras()).getString("SName");

        textViewBikesImageList = (TextView) findViewById(R.id.tvBikeImageList);
        textViewBikesImageList.setText("List of Bikes in " + bikeStore_Name + " store");

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
                startActivity(new Intent(BikesImageAdmin.this, BikeStoreImageAddBikes.class));
            }
        });

        buttonBackAdminPageBikes = (Button) findViewById(R.id.btnBackAdminPageBikes);
        buttonBackAdminPageBikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BikesImageAdmin.this, AdminPage.class));
            }
        });

        //check if the bikes list is empty and add a new bike
        //if (databaseReference == null) {
            bikeStorage = FirebaseStorage.getInstance();
            databaseReference = FirebaseDatabase.getInstance().getReference("Bikes");
        //}

        bikesDBEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Bikes bikes = postSnapshot.getValue(Bikes.class);
                    assert bikes != null;
                    if (bikes.getBikeStoreName().equals(bikeStore_Name)) {
                        bikes.setBikesKey(postSnapshot.getKey());
                        bikesList.add(bikes);
                    }
                }

                bikesListAdapter = new BikesAdapterAdmin(BikesImageAdmin.this, bikesList);
                bikesListRecyclerView.setAdapter(bikesListAdapter);
                bikesListAdapter.setOnItmClickListener(BikesImageAdmin.this);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikesImageAdmin.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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
        Intent intent = new Intent(BikesImageAdmin.this, UpdateBikeDetails.class);
        Bikes selected_Bike = bikesList.get(position);
        //final String selectedKey = selected_Bike.getBikesKey();
        intent.putExtra("model",selected_Bike.getBike_Model());
        //intent.putExtra("manufact",selected_Bike.getBike_Manufacturer());
        //intent.putExtra("Price",selected_Bike.getBike_Price());
        startActivity(intent);

    }

    //Action of the menu Delete and alert dialog
    @Override
    public void onDeleteClick(final int position) {
        AlertDialog.Builder builderAlert = new AlertDialog.Builder(BikesImageAdmin.this);
        builderAlert.setMessage("Are sure to delete this item?");
        builderAlert.setCancelable(true);
        builderAlert.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Bikes selected_Bike = bikesList.get(position);
                        final String selectedKeyBike = selected_Bike.getBikesKey();
                        StorageReference imageReference = bikeStorage.getReferenceFromUrl(selected_Bike.getBike_Image());
                        imageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                databaseReference.child(selectedKeyBike).removeValue();
                                Toast.makeText(BikesImageAdmin.this, "The Bike has been deleted successfully ", Toast.LENGTH_SHORT).show();
                                //startActivity(new Intent(BikesImageAdmin.this, AdminPage.class));
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
        databaseReference.removeEventListener(bikesDBEventListener);
    }
}
