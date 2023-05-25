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

public class BikeImageRemoveSharedBikesOwner extends AppCompatActivity implements BikeAdapterSharedBikesOwner.OnItemClickListener {

    //Declare Share Bikes database variables (Retrieve data)
    private DatabaseReference dbRefDisplayBikesShare;
    private ValueEventListener evListenerDisplayBikesShare;

    //Declare Share Bikes database (Retrieve and Delete data)
    private FirebaseStorage fbStDeleteBikesShare;
    private DatabaseReference dbRefDeleteBikeShare;

    private RecyclerView bikesListRecyclerView;
    private BikeAdapterSharedBikesOwner bikeAdapterSharedBikesOwner;

    private List<BikesShare> sharedBikesList;

    String customShare_Id = "";

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_image_remove_shared_bikes_owner);

        progressDialog = new ProgressDialog(this);
        progressDialog.show();

        //Retrieve data from Share Bikes table
        dbRefDisplayBikesShare = FirebaseDatabase.getInstance().getReference("Share Bikes");

        //Retrieve and delete data from Share Bikes table
        fbStDeleteBikesShare = FirebaseStorage.getInstance();
        dbRefDeleteBikeShare = FirebaseDatabase.getInstance().getReference("Share Bikes");

        getIntent().hasExtra("CIdRemove");
        customShare_Id = Objects.requireNonNull(getIntent().getExtras()).getString("CIdRemove");

        bikesListRecyclerView = (RecyclerView) findViewById(R.id.evRecyclerView);
        bikesListRecyclerView.setHasFixedSize(true);
        bikesListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        sharedBikesList = new ArrayList<>();

        bikeAdapterSharedBikesOwner = new BikeAdapterSharedBikesOwner(BikeImageRemoveSharedBikesOwner.this, sharedBikesList);
        bikesListRecyclerView.setAdapter(bikeAdapterSharedBikesOwner);
        bikeAdapterSharedBikesOwner.setOnItmClickListener(BikeImageRemoveSharedBikesOwner.this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onStart() {
        super.onStart();
        loadSharedBikesOwner();
    }

    //Display the Share Bikes belongs to owner
    public void loadSharedBikesOwner() {

        evListenerDisplayBikesShare = dbRefDisplayBikesShare.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                sharedBikesList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    BikesShare share_Bikes = postSnapshot.getValue(BikesShare.class);
                    assert share_Bikes != null;
                    if (share_Bikes.getShareBikes_CustomId().equals(customShare_Id)) {
                        share_Bikes.setShareBike_Key(postSnapshot.getKey());
                        sharedBikesList.add(share_Bikes);
                    }
                }

                bikeAdapterSharedBikesOwner.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikeImageRemoveSharedBikesOwner.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Display the options menu
    @Override
    public void onItemClick(final int position) {
        final String[] options = {"Back to Main Page", "Remove this Bike"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, options);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        BikesShare selected_Bike = sharedBikesList.get(position);
        builder.setTitle("You selected: "+selected_Bike.getShareBike_Model()+"\nSelect an option");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    Toast.makeText(BikeImageRemoveSharedBikesOwner.this, "Go back to main Page", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(BikeImageRemoveSharedBikesOwner.this, CustomerPageShareBikes.class));
                    progressDialog.dismiss();
                    finish();
                }
                if (which == 1) {
                    confirmDeletionShareBike(position);
                }
            }
        });

        final AlertDialog alertDialog = builder.create();

        alertDialog.show();
    }

    //Delete Share Bikes belongs to owner
    public void confirmDeletionShareBike(final int position){
        progressDialog.show();
        BikesShare selected_Bike = sharedBikesList.get(position);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BikeImageRemoveSharedBikesOwner.this);
        alertDialogBuilder
                .setMessage("Are sure to delete "+selected_Bike.getShareBike_Model())
                .setCancelable(false)
                .setPositiveButton("YES",
                        (dialog, id) -> {
                            BikesShare selected_Bike1 = sharedBikesList.get(position);
                            final String selectedKeyBike = selected_Bike1.getShareBike_Key();
                            StorageReference imageReference = fbStDeleteBikesShare.getReferenceFromUrl(selected_Bike1.getShareBike_Image());
                            imageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    dbRefDeleteBikeShare.child(selectedKeyBike).removeValue();
                                    Toast.makeText(BikeImageRemoveSharedBikesOwner.this, "The Bike has been deleted successfully ", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            });
                        })

                .setNegativeButton("NO", (dialog, id) -> dialog.cancel());
        progressDialog.dismiss();
        AlertDialog alert1 = alertDialogBuilder.create();
        alert1.show();
    }
}
