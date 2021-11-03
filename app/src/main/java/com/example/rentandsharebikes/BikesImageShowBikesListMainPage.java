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
import android.view.Menu;
import android.view.MenuItem;
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

    //Access BikesRent table from database
    private DatabaseReference databaseRefMain;
    private FirebaseStorage bikesStorageMain;
    private ValueEventListener bikesEventListenerMain;

    private RecyclerView bikesListRecyclerView;

    private BikesAdapterShowBikesListMainPage bikesAdapterShowBikesListMainPage;

    private TextView textViewBikesImageMain;

    private List<BikesRent> bikesRentListMain;

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
        bikesRentListMain = new ArrayList<>();

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
                bikesRentListMain.clear();
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    BikesRent bikesRent = postSnapshot.getValue(BikesRent.class);
                    assert bikesRent != null;
                    if (bikesRent.getBikeStoreKey().equals(bikeStore_KeyMain)) {
                        bikesRent.setBike_Key(postSnapshot.getKey());
                        bikesRentListMain.add(bikesRent);
                        textViewBikesImageMain.setText(bikesRentListMain.size()+" bikes available in "+bikeStore_NameMain+" store");
                    }
                }
                bikesAdapterShowBikesListMainPage = new BikesAdapterShowBikesListMainPage(BikesImageShowBikesListMainPage.this, bikesRentListMain);
                bikesListRecyclerView.setAdapter(bikesAdapterShowBikesListMainPage);
                bikesAdapterShowBikesListMainPage.setOnItmClickListener(BikesImageShowBikesListMainPage.this);
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
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BikesImageShowBikesListMainPage.this);
        alertDialogBuilder
                .setMessage("Register and Log into your account to access the:\nRent and Share Bikes services.")
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

        AlertDialog alert1 = alertDialogBuilder.create();
        alert1.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_bikes_main, menu);
        return true;
    }

    private void goBackBikesMain(){
        finish();
        startActivity(new Intent(BikesImageShowBikesListMainPage.this, MainActivity.class));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.showBikesMainGoBack:{
                goBackBikesMain();
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
