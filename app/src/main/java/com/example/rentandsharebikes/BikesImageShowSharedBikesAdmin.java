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
import java.util.Objects;

public class BikesImageShowSharedBikesAdmin extends AppCompatActivity implements BikesAdapterShowSharedBikesAdmin.OnItemClickListener {

    //Display data from Share Bikes database
    private FirebaseStorage firebaseStDisplaySharedBikes;
    private DatabaseReference databaseRefDisplaySharedBikes;
    private ValueEventListener displayShareBikesEventListener;
    private TextView tVBikesImgShowSBikesAdmin;

    private RecyclerView bikesListRecyclerView;
    private BikesAdapterShowSharedBikesAdmin bikesAdapterShowSharedBikesAdmin;

    private List<ShareBikes> sharedBikesList;

    String customShareAll_Id = "";

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bikes_image_show_shared_bikes_admin);

        tVBikesImgShowSBikesAdmin = (TextView) findViewById(R.id.tvBikesImgShowSBikesAdmin);

        bikesListRecyclerView = (RecyclerView) findViewById(R.id.evRecyclerView);
        bikesListRecyclerView.setHasFixedSize(true);
        bikesListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressDialog = new ProgressDialog(this);
        sharedBikesList = new ArrayList<>();

        progressDialog.show();
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
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                sharedBikesList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    ShareBikes share_Bikes = postSnapshot.getValue(ShareBikes.class);
                    assert share_Bikes != null;
                    share_Bikes.setShareBike_Key(postSnapshot.getKey());
                    sharedBikesList.add(share_Bikes);
                    tVBikesImgShowSBikesAdmin.setText(sharedBikesList.size() + " bikes available to be shared");
                }

                bikesAdapterShowSharedBikesAdmin = new BikesAdapterShowSharedBikesAdmin(BikesImageShowSharedBikesAdmin.this, sharedBikesList);
                bikesListRecyclerView.setAdapter(bikesAdapterShowSharedBikesAdmin);
                bikesAdapterShowSharedBikesAdmin.setOnItmClickListener(BikesImageShowSharedBikesAdmin.this);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikesImageShowSharedBikesAdmin.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(final int position) {
        AlertDialog.Builder builderAlert = new AlertDialog.Builder(BikesImageShowSharedBikesAdmin.this);
        builderAlert.setMessage("Contact the owner for more information");
        builderAlert.setCancelable(true);
        builderAlert.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });

        AlertDialog alert1 = builderAlert.create();
        alert1.show();
    }
}
