package com.example.rentandsharebikes;

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

public class MainActivity extends AppCompatActivity {

    //Display data from Bike Stores database
    private FirebaseStorage firebaseStBikeSores;
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

    private List<BikeStore> bikeStoresList;
    private List<Bikes> bikesListAvRent;
    private List<ShareBikes> bikesListAvShare;


    private int numberStoresAvailable;
    private int numberBikesAvRent;
    private int numberBikesAvShare;

    private TextView tVMainStoresAv, tVMainBikesAvRent, tVMainBikesAvShare;
    //Declaring some objects
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bikeStoresList = new ArrayList<>();
        bikesListAvRent = new ArrayList<>();
        bikesListAvShare = new ArrayList<>();

        tVMainStoresAv = (TextView) findViewById(R.id.tvMainStoresAv);
        tVMainBikesAvRent = (TextView) findViewById(R.id.tvMainBikesRentAv);
        tVMainBikesAvShare = (TextView) findViewById(R.id.tvMainBikesShareAv);

        drawerLayout = findViewById(R.id.activity_main);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_mainActivity, R.string.close_mainActivity);

        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView = findViewById(R.id.navViewMain);

        //Adding Click Events to our navigation drawer item
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    //Log into My Account
                    case R.id.myAccount:
                        Toast.makeText(MainActivity.this, "My Account", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, LoginCustomer.class));
                        break;
                    //Show the list of Bike Stores available
                    case R.id.bikeStoreAv:
                        Toast.makeText(MainActivity.this, "Bike Stores", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, BikeStoreImageShowStoresListMain.class));
                        break;
                    //Show the list of Bikes available from main page ordered by Bike Stores
                    case R.id.bikeAvToRent:
                        Toast.makeText(MainActivity.this, "Bikes to Rent", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, BikeStoreImageShowBikesListMain.class));
                        break;
                    //Show the list of all Bikes available from main page
                    case R.id.bikeAvToRentAll:
                        Toast.makeText(MainActivity.this, "Bikes to Rent All", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, BikesImageShowBikesListMainAll.class));
                        break;
                    //Bikes available to share
                    case R.id.bikeAvToShare:
                        Toast.makeText(MainActivity.this, "Bikes available to Share",Toast.LENGTH_SHORT).show();
                        //startActivity(new Intent(MainActivity.this, BikesImageShowAllSharedBikes.class));
                        break;
                    case R.id.settings:
                        Toast.makeText(MainActivity.this, "Bikes to share", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, AdminPage.class));
                        break;
                    default:
                        return true;
                }
                return true;
            }
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
        loadBikeStoresAv();
        loadBikeRentAv();
        loadBikeShareAv();
    }

    private void loadBikeStoresAv() {
        //initialize the bike storage database
        firebaseStBikeSores = FirebaseStorage.getInstance();
        databaseRefBikeStores = FirebaseDatabase.getInstance().getReference("Bike Stores");

        bikeStoresEventListener = databaseRefBikeStores.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bikeStoresList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    BikeStore bike_Stores = postSnapshot.getValue(BikeStore.class);
                    assert bike_Stores != null;
                    bike_Stores.setStoreKey(postSnapshot.getKey());
                    bikeStoresList.add(bike_Stores);
                    numberStoresAvailable = bikeStoresList.size();
                    tVMainStoresAv.setText(String.valueOf(numberStoresAvailable));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loadBikeRentAv() {
        //initialize the bike storage database
        firebaseStBikesAvRent = FirebaseStorage.getInstance();
        databaseRefBikesAvRent = FirebaseDatabase.getInstance().getReference("Bikes");

        bikesAvRentEventListener = databaseRefBikesAvRent.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bikesListAvRent.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Bikes bikes = postSnapshot.getValue(Bikes.class);
                    assert bikes != null;
                    bikes.setBike_Key(postSnapshot.getKey());
                    bikesListAvRent.add(bikes);
                    numberBikesAvRent = bikesListAvRent.size();
                    tVMainBikesAvRent.setText(String.valueOf(numberBikesAvRent));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadBikeShareAv() {
        //initialize the bike storage database
        firebaseStBikesAvShare = FirebaseStorage.getInstance();
        databaseRefBikesAvShare = FirebaseDatabase.getInstance().getReference("Share Bikes");

        bikesAvShareEventListener = databaseRefBikesAvShare.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bikesListAvShare.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    ShareBikes share_Bikes = postSnapshot.getValue(ShareBikes.class);
                    assert share_Bikes != null;
                    share_Bikes.setShareBike_Key(postSnapshot.getKey());
                    bikesListAvShare.add(share_Bikes);
                    numberBikesAvShare = bikesListAvShare.size();
                    tVMainBikesAvShare.setText(String.valueOf(numberBikesAvShare));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
