package com.example.rentandsharebikes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
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

public class BikeImageShowSharedBikesAdmin extends AppCompatActivity implements BikeAdapterShowSharedBikesAdmin.OnItemClickListener {

    //Display data from Share Bikes database
    private FirebaseStorage firebaseStDisplaySharedBikes;
    private DatabaseReference databaseRefDisplaySharedBikes;
    private ValueEventListener displayShareBikesEventListener;
    private TextView tVBikesImgShowSBikesAdmin;

    private RecyclerView bikesListRecyclerView;
    private BikeAdapterShowSharedBikesAdmin bikeAdapterShowSharedBikesAdmin;

    private List<BikesShare> sharedBikesList;

    String customShareAll_Id = "";

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_image_show_shared_bikes_admin);

        progressDialog = new ProgressDialog(this);
        progressDialog.show();

        tVBikesImgShowSBikesAdmin = findViewById(R.id.tvBikesImgShowSBikesAdmin);

        bikesListRecyclerView = findViewById(R.id.evRecyclerView);
        bikesListRecyclerView.setHasFixedSize(true);
        bikesListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        sharedBikesList = new ArrayList<>();

        bikeAdapterShowSharedBikesAdmin = new BikeAdapterShowSharedBikesAdmin(BikeImageShowSharedBikesAdmin.this, sharedBikesList);
        bikesListRecyclerView.setAdapter(bikeAdapterShowSharedBikesAdmin);
        bikeAdapterShowSharedBikesAdmin.setOnItmClickListener(BikeImageShowSharedBikesAdmin.this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onStart() {
        super.onStart();
        loadSharedBikesNoOwner();
    }

    public void loadSharedBikesNoOwner() {
        //Display the list of the bikes from Share Bikes database
        firebaseStDisplaySharedBikes = FirebaseStorage.getInstance();
        databaseRefDisplaySharedBikes = FirebaseDatabase.getInstance().getReference("Share Bikes");

        displayShareBikesEventListener = databaseRefDisplaySharedBikes.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                sharedBikesList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    BikesShare share_Bikes = postSnapshot.getValue(BikesShare.class);
                    assert share_Bikes != null;
                    share_Bikes.setShareBike_Key(postSnapshot.getKey());
                    sharedBikesList.add(share_Bikes);
                    tVBikesImgShowSBikesAdmin.setText(sharedBikesList.size() + " bikes available to be shared");
                }

                bikeAdapterShowSharedBikesAdmin.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikeImageShowSharedBikesAdmin.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(final int position) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BikeImageShowSharedBikesAdmin.this);
        alertDialogBuilder
                .setMessage("Contact the owner for more information")
                .setCancelable(false)
                .setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

        AlertDialog alert1 = alertDialogBuilder.create();
        alert1.show();
    }
}
