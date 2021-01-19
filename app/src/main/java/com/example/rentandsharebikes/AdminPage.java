package com.example.rentandsharebikes;

import android.annotation.SuppressLint;
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

import java.util.ArrayList;
import java.util.List;

public class AdminPage extends AppCompatActivity {

    //Declare Bike Store database variables (Retrieve data)
    private DatabaseReference dbRefBikeStoresAv;
    private ValueEventListener evListenerBikeStoreAv;

    //Declare Bike database variables (Retrieve data)
    private DatabaseReference dbRefBikesRentAv;
    private ValueEventListener evListenerBikesRentAv;

    //Declare Rent Bikes database variables (Retrieve data)
    private DatabaseReference dbRefBikesRent;
    private ValueEventListener eventListenerBikesRent;

    //Declare Share Bikes database variables (Retrieve data)
    private DatabaseReference dbRefBikesShareAv;
    private ValueEventListener eventListenerBikeShareAv;

    private List<BikeStores> bikeStoresList;
    private List<Bikes> bikesListAvRent;
    private List<RentBikes> bikesListRented;
    private List<ShareBikes> bikesListAvShare;

    private int numberStoresAvailable;
    private int numberBikesAvRent;
    private int numberBikesAvShare;
    private int numberBikesRented;

    private TextView tVAdminStoresAv, tVAdminBikesRentAv, tVAdminBikesRented, tVAdminBikesShareAv;

    //Declaring some objects
    private DrawerLayout drawerLayoutAdmin;
    private ActionBarDrawerToggle drawerToggleAdmin;
    private NavigationView navigationViewAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_page);

        //Retrieve data from Bike Store table
        dbRefBikeStoresAv = FirebaseDatabase.getInstance().getReference("Bike Stores");

        //Retrieve data from Bikes table
        dbRefBikesRentAv = FirebaseDatabase.getInstance().getReference("Bikes");

        //Retrieve data from Rent Bikes table
        dbRefBikesRent = FirebaseDatabase.getInstance().getReference("Rent Bikes");

        //Retrieve data Share Bikes table
        dbRefBikesShareAv = FirebaseDatabase.getInstance().getReference("Share Bikes");

        bikeStoresList = new ArrayList<>();
        bikesListAvRent = new ArrayList<>();
        bikesListRented = new ArrayList<>();
        bikesListAvShare = new ArrayList<>();

        tVAdminStoresAv = findViewById(R.id.tvAdminStoresAv);
        tVAdminBikesRentAv = findViewById(R.id.tvAdminBikesRentAv);
        tVAdminBikesRented = findViewById(R.id.tvAdminBikesRentedAv);
        tVAdminBikesShareAv = findViewById(R.id.tvAdminBikesShareAv);

        drawerLayoutAdmin = findViewById(R.id.activity_admin_page);
        drawerToggleAdmin = new ActionBarDrawerToggle(this,drawerLayoutAdmin, R.string.open_adminPage, R.string.close_adminPage);

        drawerLayoutAdmin.addDrawerListener(drawerToggleAdmin);
        drawerToggleAdmin.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationViewAdmin = findViewById(R.id.navViewAdmin);

        //Adding Click Events to navigation drawer item
        navigationViewAdmin.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id){
                    //Add new Bike Stores
                    case R.id.adminAdd_bikeStores:
                        Toast.makeText(AdminPage.this, "Add Bike Stores",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AdminPage.this, CalculateCoordinates.class));
                        break;
                    //Show the list of Bike Stores available
                    case R.id.adminShow_bikeStores:
                        Toast.makeText(AdminPage.this, "Show Bike Stores",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AdminPage.this, BikeStoreImageShowStoresListAdmin.class));
                        break;
                    //Add Bikes to the Bike Stores available
                    case R.id.adminAdd_bikesToStore:
                        Toast.makeText(AdminPage.this, "Add Bikes to Store",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AdminPage.this, BikeStoreImageAddBikesAdmin.class));
                        break;
                    //Show the list of Bikes available ordered by Bike Stores
                    case R.id.adminShow_bikesList:
                        Toast.makeText(AdminPage.this, "Show Bikes List",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AdminPage.this, BikeStoreImageShowBikesListAdmin.class));
                        break;
                    //Show the full list of Bikes available
                    case R.id.adminShow_bikesListFull:
                        Toast.makeText(AdminPage.this, "Show Full List of Bikes",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AdminPage.this, BikesImageShowBikesListAdminFull.class));
                        break;
                    //Show the full list of rented Bikes
                    case R.id.adminShow_bikesRented:
                        Toast.makeText(AdminPage.this, "Rented Bikes",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AdminPage.this, BikesImageShowBikesRentedAdmin.class));
                        break;
                    //Show the full list of rented Bikes
                    case R.id.adminShow_bikesShared:
                        Toast.makeText(AdminPage.this, "Shared Bikes",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AdminPage.this, BikesImageShowSharedBikesAdmin.class));
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
        getMenuInflater().inflate(R.menu.menu_admin, menu);
        return true;
    }

    //user log out
    private void LogOut(){
        finish();
        startActivity(new Intent(AdminPage.this, MainActivity.class));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        if(drawerToggleAdmin.onOptionsItemSelected(item)) {
            return true;
        }

        if (item.getItemId() == R.id.logOutUser) {
            LogOut();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        loadBikeStoresAv();
        loadBikeRentAv();
        loadBikeRented();
        loadBikeShareAv();
    }

    //Display the Bike Stores available
    private void loadBikeStoresAv() {
        evListenerBikeStoreAv = dbRefBikeStoresAv.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bikeStoresList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    BikeStores bike_Stores = postSnapshot.getValue(BikeStores.class);
                    assert bike_Stores != null;
                    bike_Stores.setBikeStore_Key(postSnapshot.getKey());
                    bikeStoresList.add(bike_Stores);
                    numberStoresAvailable = bikeStoresList.size();
                    tVAdminStoresAv.setText(String.valueOf(numberStoresAvailable));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AdminPage.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Display the Bikes available to rent
    private void loadBikeRentAv() {

        evListenerBikesRentAv = dbRefBikesRentAv.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bikesListAvRent.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Bikes bikes = postSnapshot.getValue(Bikes.class);
                    assert bikes != null;
                    bikes.setBike_Key(postSnapshot.getKey());
                    bikesListAvRent.add(bikes);
                    numberBikesAvRent = bikesListAvRent.size();
                    tVAdminBikesRentAv.setText(String.valueOf(numberBikesAvRent));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AdminPage.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Display the Bikes rented by customers
    private void loadBikeRented() {

        eventListenerBikesRent = dbRefBikesRent.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bikesListRented.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    RentBikes rented_Bikes = postSnapshot.getValue(RentBikes.class);
                    assert rented_Bikes != null;
                    rented_Bikes.setBike_RentKey(postSnapshot.getKey());
                    bikesListRented.add(rented_Bikes);
                    numberBikesRented = bikesListRented.size();
                    tVAdminBikesRented.setText(String.valueOf(numberBikesRented));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AdminPage.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Display the Bike Shares available
    private void loadBikeShareAv() {

        eventListenerBikeShareAv = dbRefBikesShareAv.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bikesListAvShare.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    ShareBikes share_Bikes = postSnapshot.getValue(ShareBikes.class);
                    assert share_Bikes != null;
                    share_Bikes.setShareBike_Key(postSnapshot.getKey());
                    bikesListAvShare.add(share_Bikes);
                    numberBikesAvShare = bikesListAvShare.size();
                    tVAdminBikesShareAv.setText(String.valueOf(numberBikesAvShare));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AdminPage.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
