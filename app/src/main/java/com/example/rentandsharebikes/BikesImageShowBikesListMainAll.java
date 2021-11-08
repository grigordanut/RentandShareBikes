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

public class BikesImageShowBikesListMainAll extends AppCompatActivity implements BikesAdapterShowBikesListMainAll.OnItemClickListener {

    //Access BikesRent table from database
    private DatabaseReference databaseRefMainAll;
    private FirebaseStorage bikesStorageMainAll;
    private ValueEventListener bikesEventListenerMainAll;

    private RecyclerView bikesListRecyclerView;

    private BikesAdapterShowBikesListMainAll bikesAdapterShowBikesListMainAll;
    private TextView tVBikesImageMainAll;

    private List<BikesRent> bikesRentListMainAll;

    String bikeStore_Name = "";

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bikes_image_show_bikes_list_main_all);

        tVBikesImageMainAll = findViewById(R.id.tvBikeImageBikesListMainAll);
        tVBikesImageMainAll.setText("No bikes available to rent");

        bikesListRecyclerView = findViewById(R.id.evRecyclerView);
        bikesListRecyclerView.setHasFixedSize(true);
        bikesListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressDialog = new ProgressDialog(this);
        bikesRentListMainAll = new ArrayList<>();

        progressDialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        loadBikesListMainPageAll();
    }

    public void loadBikesListMainPageAll() {
        //initialize the bike storage database
        bikesStorageMainAll = FirebaseStorage.getInstance();
        databaseRefMainAll = FirebaseDatabase.getInstance().getReference("Bikes");

        bikesEventListenerMainAll = databaseRefMainAll.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bikesRentListMainAll.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    BikesRent bikesRent = postSnapshot.getValue(BikesRent.class);
                    assert bikesRent != null;
                    bikesRent.setBike_Key(postSnapshot.getKey());
                    bikesRentListMainAll.add(bikesRent);
                    tVBikesImageMainAll.setText(bikesRentListMainAll.size() + " BikesRent available to rent ");
                }
                bikesAdapterShowBikesListMainAll = new BikesAdapterShowBikesListMainAll(BikesImageShowBikesListMainAll.this, bikesRentListMainAll);
                bikesListRecyclerView.setAdapter(bikesAdapterShowBikesListMainAll);
                bikesAdapterShowBikesListMainAll.setOnItmClickListener(BikesImageShowBikesListMainAll.this);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikesImageShowBikesListMainAll.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Action of the menu onClick
    @Override
    public void onItemClick(int position) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BikesImageShowBikesListMainAll.this);
        alertDialogBuilder
                .setCancelable(false)
                .setMessage("Register and Log into your account to access the:\nRent and Share Bikes services.")
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
