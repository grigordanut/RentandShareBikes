package com.example.rentandsharebikes;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    //Display data from Bike Stores database
    private DatabaseReference databaseRefBikeStores;
    private ValueEventListener bikeStoresEventListener;

    //Display data from Bikes table database
    private FirebaseStorage firebaseStBikesAvRent;
    private DatabaseReference databaseRefBikesAvRent;
    private ValueEventListener bikesAvRentEventListener;

    //Display data from Share Bikes table database
    private FirebaseStorage firebaseStBikesAvShare;
    private DatabaseReference databaseRefBikesAvShare;
    private ValueEventListener bikesAvShareEventListener;

    private List<BikeStores> bikeStoresList;
    private List<Bikes> bikesRentListAv;
    private List<BikesShare> bikesListAvShare;

    private int numberStoresAvailable;
    private int numberBikesAvRent;
    private int numberBikesAvShare;

    private TextView tVMainStoresAv, tVMainBikesRentAv, tVMainBikesShareAv;

    //Declaring some objects
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navigationView;

    private ProgressDialog progressDialog;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Main Activity");

        progressDialog = new ProgressDialog(MainActivity.this);

        //initialize the bike storage database
        databaseRefBikeStores = FirebaseDatabase.getInstance().getReference("Bike Stores");

        //initialize the bike storage database
        firebaseStBikesAvRent = FirebaseStorage.getInstance();
        databaseRefBikesAvRent = FirebaseDatabase.getInstance().getReference("Bikes");

        //Display the list of the bikes from Share Bikes database
        firebaseStBikesAvShare = FirebaseStorage.getInstance();
        databaseRefBikesAvShare = FirebaseDatabase.getInstance().getReference("Share Bikes");

        bikeStoresList = new ArrayList<>();
        bikesRentListAv = new ArrayList<>();
        bikesListAvShare = new ArrayList<>();

        tVMainStoresAv = (TextView)findViewById(R.id.tvMainStoresAv);
        tVMainBikesRentAv = (TextView)findViewById(R.id.tvMainBikesRentAv);
        tVMainBikesShareAv = (TextView)findViewById(R.id.tvMainBikesShareAv);

        drawerLayout = findViewById(R.id.activity_main);
        navigationView = findViewById(R.id.navViewMain);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_mainActivity, R.string.close_mainActivity);

        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        //Adding Click Events to our navigation drawer item
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            switch (id) {
                //Log into My Account
                case R.id.myAccount:
                    startActivity(new Intent(MainActivity.this, Login.class));
                    break;
                //Show the list of Bike Stores available
                case R.id.bikeStoreAv:
                    startActivity(new Intent(MainActivity.this, BikeStoreImageMainShowStores.class));
                    break;
                //Show the list of Bikes available from main page ordered by Bike Stores
                case R.id.bikeAvToRent:
                    startActivity(new Intent(MainActivity.this, BikeStoreImageMainShowBikes.class));
                    break;
                //Show the list of all Bikes available from main page
                case R.id.bikeAvToRentAll:
                    startActivity(new Intent(MainActivity.this, BikeImageMainShowBikesAll.class));
                    break;
                //Show bikes available to share
                case R.id.bikeAvToShare:
                    startActivity(new Intent(MainActivity.this, BikeImageMainShowBikesToShare.class));
                    break;
                case R.id.contactUs:
                    startActivity(new Intent(MainActivity.this, ContactUs.class));
                    break;
                default:
                    return true;
            }
            return true;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        loadBikeStoresMainAv();
        loadBikeRentMainAv();
        loadBikeShareMainAv();
    }

    private void loadBikeStoresMainAv() {

        progressDialog.show();

        bikeStoresEventListener = databaseRefBikeStores.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                bikeStoresList.clear();

                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        BikeStores bike_Stores = postSnapshot.getValue(BikeStores.class);
                        assert bike_Stores != null;
                        bike_Stores.setBikeStore_Key(postSnapshot.getKey());
                        bikeStoresList.add(bike_Stores);
                        numberStoresAvailable = bikeStoresList.size();
                        tVMainStoresAv.setText(String.valueOf(numberStoresAvailable));
                    }
                }

                else {
                    tVMainStoresAv.setText(String.valueOf(0));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        progressDialog.dismiss();
    }

    private void loadBikeRentMainAv() {

        progressDialog.show();

        bikesAvRentEventListener = databaseRefBikesAvRent.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                bikesRentListAv.clear();

                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Bikes bikes = postSnapshot.getValue(Bikes.class);
                        assert bikes != null;
                        bikes.setBike_Key(postSnapshot.getKey());
                        bikesRentListAv.add(bikes);
                        numberBikesAvRent = bikesRentListAv.size();
                        tVMainBikesRentAv.setText(String.valueOf(numberBikesAvRent));
                    }
                }

                else {
                    tVMainBikesRentAv.setText(String.valueOf(0));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        progressDialog.dismiss();
    }

    private void loadBikeShareMainAv() {

        progressDialog.show();

        bikesAvShareEventListener = databaseRefBikesAvShare.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                bikesListAvShare.clear();

                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        BikesShare share_Bikes = postSnapshot.getValue(BikesShare.class);
                        assert share_Bikes != null;
                        share_Bikes.setShareBike_Key(postSnapshot.getKey());
                        bikesListAvShare.add(share_Bikes);
                        numberBikesAvShare = bikesListAvShare.size();
                        tVMainBikesShareAv.setText(String.valueOf(numberBikesAvShare));
                    }
                }

                else {
                    tVMainBikesShareAv.setText(String.valueOf(0));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        progressDialog.dismiss();
    }
}
