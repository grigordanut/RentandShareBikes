package com.example.rentandsharebikes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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

public class BikeImageAdminShowBikesToShare extends AppCompatActivity implements BikeAdapterAdminShowBikesToShare.OnItemClickListener {

    //Display data from Share Bikes database
    private FirebaseStorage firebaseStDisplaySharedBikes;
    private DatabaseReference databaseRefDisplaySharedBikes;
    private ValueEventListener displayShareBikesEventListener;
    private TextView tVBikesImgAdminShowBikesToShare;

    private RecyclerView rvBikesImgAdminShow_BikesToShare;
    private BikeAdapterAdminShowBikesToShare bikeAdapterAdminShowBikesToShare;

    private List<BikesShare> sharedBikesList;

    String customShareAll_Id = "";

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_image_admin_show_bikes_to_share);

        progressDialog = new ProgressDialog(this);

        //Display the list of the bikes from Share Bikes database
        firebaseStDisplaySharedBikes = FirebaseStorage.getInstance();
        databaseRefDisplaySharedBikes = FirebaseDatabase.getInstance().getReference("Share Bikes");


        tVBikesImgAdminShowBikesToShare = findViewById(R.id.tvBikesImgAdminShowBikesToShare);

        rvBikesImgAdminShow_BikesToShare = findViewById(R.id.rvBikesImgAdminShowBikesToShare);
        rvBikesImgAdminShow_BikesToShare.setHasFixedSize(true);
        rvBikesImgAdminShow_BikesToShare.setLayoutManager(new LinearLayoutManager(this));

        sharedBikesList = new ArrayList<>();

        bikeAdapterAdminShowBikesToShare = new BikeAdapterAdminShowBikesToShare(BikeImageAdminShowBikesToShare.this, sharedBikesList);
        rvBikesImgAdminShow_BikesToShare.setAdapter(bikeAdapterAdminShowBikesToShare);
        bikeAdapterAdminShowBikesToShare.setOnItmClickListener(BikeImageAdminShowBikesToShare.this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onStart() {
        super.onStart();
        loadSharedBikesNoOwner();
    }

    public void loadSharedBikesNoOwner() {

        progressDialog.show();

        displayShareBikesEventListener = databaseRefDisplaySharedBikes.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                sharedBikesList.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    BikesShare share_Bikes = postSnapshot.getValue(BikesShare.class);
                    assert share_Bikes != null;
                    share_Bikes.setShareBike_Key(postSnapshot.getKey());
                    sharedBikesList.add(share_Bikes);

                }

                if (sharedBikesList.size() == 1) {
                    tVBikesImgAdminShowBikesToShare.setText(sharedBikesList.size() + " bike available to share");
                }
                else if (sharedBikesList.size() > 1) {
                    tVBikesImgAdminShowBikesToShare.setText(sharedBikesList.size() + " bikes available to share");
                }
                else {
                    tVBikesImgAdminShowBikesToShare.setText("No bike available to share!!");
                }

                bikeAdapterAdminShowBikesToShare.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikeImageAdminShowBikesToShare.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        progressDialog.dismiss();
    }

    @Override
    public void onItemClick(int position) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BikeImageAdminShowBikesToShare.this);
        alertDialogBuilder
                .setMessage("Contact the owner for more information")
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, id) -> dialog.dismiss());

        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
}
