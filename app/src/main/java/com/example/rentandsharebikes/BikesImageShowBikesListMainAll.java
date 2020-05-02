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

public class BikesImageShowBikesListMainAll extends AppCompatActivity implements BikesAdapterShowBikesListMainAll.OnItemClickListener{

    //Access Bikes table from database
    private DatabaseReference databaseRefMainAll;
    private FirebaseStorage bikesStorageMainAll;
    private ValueEventListener bikesEventListenerMainAll;

    private RecyclerView bikesListRecyclerView;

    private BikesAdapterShowBikesListMainAll bikesAdapterShowBikesListMainAll;
    private TextView tVBikesImageMainAll;

    private List<Bikes> bikesListMainAll;

    String bikeStore_Name = "";

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bikes_image_show_bikes_list_main_all);

        tVBikesImageMainAll = (TextView) findViewById(R.id.tvBikeImageBikesListMainAll);
        tVBikesImageMainAll.setText("No bikes available to rent");

        bikesListRecyclerView = (RecyclerView) findViewById(R.id.evRecyclerView);
        bikesListRecyclerView.setHasFixedSize(true);
        bikesListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressDialog = new ProgressDialog(this);
        bikesListMainAll = new ArrayList<>();

        progressDialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        loadBikesListMainPageAll();
    }

    public void loadBikesListMainPageAll(){
        //initialize the bike storage database
        bikesStorageMainAll = FirebaseStorage.getInstance();
        databaseRefMainAll = FirebaseDatabase.getInstance().getReference("Bikes");

        bikesEventListenerMainAll = databaseRefMainAll.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bikesListMainAll.clear();
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Bikes bikes = postSnapshot.getValue(Bikes.class);
                    assert bikes != null;
                    //if (bikes.getBikeStoreName().equals(bikeStore_Name)) {
                        bikes.setBike_Key(postSnapshot.getKey());
                        bikesListMainAll.add(bikes);
                        tVBikesImageMainAll.setText(bikesListMainAll.size()+" bikes available to rent ");
                    //}
                }
                bikesAdapterShowBikesListMainAll = new BikesAdapterShowBikesListMainAll(BikesImageShowBikesListMainAll.this,bikesListMainAll);
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
        AlertDialog.Builder builderAlert = new AlertDialog.Builder(BikesImageShowBikesListMainAll.this);
        builderAlert.setMessage("Register and Log into your account to access the:\nRenting and Share Bikes services");
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
