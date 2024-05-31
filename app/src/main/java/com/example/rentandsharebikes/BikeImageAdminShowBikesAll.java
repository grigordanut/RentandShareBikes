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
import android.widget.ArrayAdapter;
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

public class BikeImageAdminShowBikesAll extends AppCompatActivity implements BikeAdapterAdminBikes.OnItemClickListener {


    private FirebaseStorage bikesStorage;
    private DatabaseReference databaseReference;
    private ValueEventListener bikesEventListener;

    private RecyclerView rvBikeImgAdmin_ShowBikesAll;
    private BikeAdapterAdminBikes bikeAdapterAdminBikes;

    private TextView tVBikeListAdminAll;

    private List<Bikes> listShowBikesAll;

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_image_admin_show_bikes_all);

        Objects.requireNonNull(getSupportActionBar()).setTitle("ADMIN Bikes available all");

        progressDialog = new ProgressDialog(this);

        //initialize the bike storage database
        bikesStorage = FirebaseStorage.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Bikes");

        tVBikeListAdminAll = findViewById(R.id.tvBikeImgAdminShoeBikesAll);

        rvBikeImgAdmin_ShowBikesAll = findViewById(R.id.rvBikeImgAdminShowBikesAll);
        rvBikeImgAdmin_ShowBikesAll.setHasFixedSize(true);
        rvBikeImgAdmin_ShowBikesAll.setLayoutManager(new LinearLayoutManager(this));

        listShowBikesAll = new ArrayList<>();

        bikeAdapterAdminBikes = new BikeAdapterAdminBikes(BikeImageAdminShowBikesAll.this, listShowBikesAll);
        rvBikeImgAdmin_ShowBikesAll.setAdapter(bikeAdapterAdminBikes);
        bikeAdapterAdminBikes.setOnItmClickListener(BikeImageAdminShowBikesAll.this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onStart() {
        super.onStart();
        loadBikesListAdmin();
    }

    private void loadBikesListAdmin() {

        progressDialog.show();

        bikesEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                listShowBikesAll.clear();

                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Bikes bikes = postSnapshot.getValue(Bikes.class);
                        assert bikes != null;
                        bikes.setBike_Key(postSnapshot.getKey());
                        listShowBikesAll.add(bikes);
                    }

                    bikeAdapterAdminBikes.notifyDataSetChanged();

                    if (listShowBikesAll.size() == 1) {
                        tVBikeListAdminAll.setText(listShowBikesAll.size() + " Bike available");
                    }
                    else {
                        tVBikeListAdminAll.setText(listShowBikesAll.size() + " Bikes available");
                    }
                }
                else {
                    tVBikeListAdminAll.setText("No Bikes registered!!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikeImageAdminShowBikesAll.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        progressDialog.dismiss();
    }

    //Action on bikes onClick
    @Override
    public void onItemClick(final int position) {
        final String[] options = {"Update this Bike", "Delete this Bike"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, options);
        Bikes selected_Bike = listShowBikesAll.get(position);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setTitle("You selected: " + selected_Bike.getBike_Model() + "\nSelect an option:")
                .setCancelable(false)
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
        Intent intent = new Intent(BikeImageAdminShowBikesAll.this, UpdateBikeDetails.class);
        Bikes selected_Bike = listShowBikesAll.get(position);
        intent.putExtra("BCondition", selected_Bike.getBike_Condition());
        intent.putExtra("BModel", selected_Bike.getBike_Model());
        intent.putExtra("BManufact", selected_Bike.getBike_Manufacturer());
        intent.putExtra("BPrice", String.valueOf(selected_Bike.getBike_Price()));
        intent.putExtra("BImage", selected_Bike.getBike_Image());
        intent.putExtra("BKey", selected_Bike.getBike_Key());
        startActivity(intent);
    }

    public void confirmBikeDeletion(final int position) {
        Bikes selected_Bike = listShowBikesAll.get(position);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BikeImageAdminShowBikesAll.this);
        alertDialogBuilder
                .setTitle("Delete bikes from BikeStore!!")
                .setMessage("Are sure to delete the " + selected_Bike.getBike_Model() + " Bike?")
                .setCancelable(true)
                .setPositiveButton("YES",
                        (dialog, which) -> {
                            Bikes selected_Bike1 = listShowBikesAll.get(position);
                            final String selectedKeyBike = selected_Bike1.getBike_Key();
                            StorageReference imageReference = bikesStorage.getReferenceFromUrl(selected_Bike1.getBike_Image());
                            imageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    databaseReference.child(selectedKeyBike).removeValue();
                                    Toast.makeText(BikeImageAdminShowBikesAll.this, "The Bike has been successfully deleted. ", Toast.LENGTH_SHORT).show();
                                }
                            });
                        })

                .setNegativeButton("NO", (dialog, which) -> dialog.cancel());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseReference.removeEventListener(bikesEventListener);
    }


}
