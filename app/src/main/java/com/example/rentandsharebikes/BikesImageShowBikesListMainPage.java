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

public class BikesImageShowBikesListMainPage extends AppCompatActivity implements BikesAdapterShowBikesListMainPage.OnItemClickListener {

    //Access Bikes table from database
    private DatabaseReference databaseRefMain;
    private FirebaseStorage bikesStorageMain;
    private ValueEventListener bikesEventListenerMain;

    private RecyclerView bikesListRecyclerView;

    private BikesAdapterShowBikesListMainPage bikesAdapterShowBikesListMainPage;

    private TextView textViewBikesImageMain;

    private List<Bikes> bikesListMain;

    String bikeStore_NameMain = "";
    String bikeStore_KeyMain = "";


    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bikes_image_show_bikes_list_main_page);

        getIntent().hasExtra("SNameMain");
        bikeStore_NameMain = Objects.requireNonNull(getIntent().getExtras()).getString("SNameMain");

        getIntent().hasExtra("SKeyMain");
        bikeStore_KeyMain = Objects.requireNonNull(getIntent().getExtras()).getString("SKeyMain");

        textViewBikesImageMain = (TextView) findViewById(R.id.tvBikeImageBikesListMainPage);
        textViewBikesImageMain.setText("No bikes available in " +bikeStore_NameMain+ " store");

        bikesListRecyclerView = (RecyclerView) findViewById(R.id.evRecyclerView);
        bikesListRecyclerView.setHasFixedSize(true);
        bikesListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressDialog = new ProgressDialog(this);
        bikesListMain = new ArrayList<>();

        progressDialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        loadBikesListMainPage();
    }

    public void loadBikesListMainPage(){
        //initialize the bike storage database
        bikesStorageMain = FirebaseStorage.getInstance();
        databaseRefMain = FirebaseDatabase.getInstance().getReference("Bikes");

        bikesEventListenerMain = databaseRefMain.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bikesListMain.clear();
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Bikes bikes = postSnapshot.getValue(Bikes.class);
                    assert bikes != null;
                    if (bikes.getBikeStoreKey().equals(bikeStore_KeyMain)) {
                        bikes.setBike_Key(postSnapshot.getKey());
                        bikesListMain.add(bikes);
                        textViewBikesImageMain.setText(bikesListMain.size()+" bikes available in "+bikeStore_NameMain+" store");
                    }
                }
                bikesAdapterShowBikesListMainPage = new BikesAdapterShowBikesListMainPage(BikesImageShowBikesListMainPage.this,bikesListMain);
                bikesListRecyclerView.setAdapter(bikesAdapterShowBikesListMainPage);

                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikesImageShowBikesListMainPage.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Action of the menu onClick
    @Override
    public void onItemClick(int position) {
        AlertDialog.Builder builderAlert = new AlertDialog.Builder(BikesImageShowBikesListMainPage.this);
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
