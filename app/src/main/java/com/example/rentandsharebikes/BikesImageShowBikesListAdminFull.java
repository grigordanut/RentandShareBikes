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
import java.util.Objects;

public class BikesImageShowBikesListAdminFull extends AppCompatActivity implements BikesAdapterShowBikesListAdminFull.OnItemClickListener{

    private DatabaseReference databaseReference;
    private FirebaseStorage bikesStorage;
    private ValueEventListener bikesEventListener;

    private RecyclerView bikesListRecyclerView;
    private BikesAdapterShowBikesListAdminFull bikesListAdapterAdminFull;

    private TextView textViewBikesImageList;

    private List<Bikes> bikesList;

    private Button buttonAddMoreBikesFull, buttonBackAdminPageBikesFull;

    String bikeStore_Name = "";

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bikes_image_show_bikes_list_admin_full);

//        getIntent().hasExtra("SName");
//        bikeStore_Name = Objects.requireNonNull(getIntent().getExtras()).getString("SName");

        textViewBikesImageList = (TextView) findViewById(R.id.tvBikeImageListFull);
        textViewBikesImageList.setText("No bikes available");

        bikesListRecyclerView = (RecyclerView) findViewById(R.id.evRecyclerView);
        bikesListRecyclerView.setHasFixedSize(true);
        bikesListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressDialog = new ProgressDialog(this);
        bikesList = new ArrayList<>();

        progressDialog.show();

        buttonAddMoreBikesFull = (Button) findViewById(R.id.btnAddMoreBikesFull);
        buttonAddMoreBikesFull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BikesImageShowBikesListAdminFull.this, BikeStoreImageAddBikesAdmin.class));
            }
        });

        buttonBackAdminPageBikesFull = (Button) findViewById(R.id.btnBackAdminPageBikesFull);
        buttonBackAdminPageBikesFull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BikesImageShowBikesListAdminFull.this, AdminPage.class));
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
        databaseReference = FirebaseDatabase.getInstance().getReference("Bikes");

        bikesEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bikesList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Bikes bikes = postSnapshot.getValue(Bikes.class);
                    assert bikes != null;
                    bikes.setBike_Key(postSnapshot.getKey());
                    bikesList.add(bikes);
                    textViewBikesImageList.setText(bikesList.size()+" Bikes available");
                }
                bikesListAdapterAdminFull = new BikesAdapterShowBikesListAdminFull(BikesImageShowBikesListAdminFull.this, bikesList);
                bikesListRecyclerView.setAdapter(bikesListAdapterAdminFull);
                bikesListAdapterAdminFull.setOnItmClickListener(BikesImageShowBikesListAdminFull.this);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikesImageShowBikesListAdminFull.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Action on bikes onClick
    @Override
    public void onItemClick(final int position) {
        final String [] options = {"Update this Bike", "Delete this Bike"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item,options);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Bikes selected_Bike = bikesList.get(position);
        builder.setTitle("You selected "+selected_Bike.getBike_Model()+"\nSelect an option");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0){
                    updateBikeDetails(position);
                }

                if (which == 1){
                    confirmBikeDeletion(position);
                }
            }
        });

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void updateBikeDetails(final int position){
        Intent intent = new Intent(BikesImageShowBikesListAdminFull.this, UpdateBikeDetails.class);
        Bikes selected_Bike = bikesList.get(position);
        intent.putExtra("BCondition",selected_Bike.getBike_Condition());
        intent.putExtra("BModel",selected_Bike.getBike_Model());
        intent.putExtra("BManufact",selected_Bike.getBike_Manufacturer());
        intent.putExtra("BPrice",String.valueOf(selected_Bike.getBike_Price()));
        intent.putExtra("BImage",selected_Bike.getBike_Image());
        intent.putExtra("BKey",selected_Bike.getBike_Key());
        startActivity(intent);
    }

    public void confirmBikeDeletion(final int position){
        AlertDialog.Builder builderAlert = new AlertDialog.Builder(BikesImageShowBikesListAdminFull.this);
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
                                Toast.makeText(BikesImageShowBikesListAdminFull.this, "The Bike has been deleted successfully ", Toast.LENGTH_SHORT).show();
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
}
