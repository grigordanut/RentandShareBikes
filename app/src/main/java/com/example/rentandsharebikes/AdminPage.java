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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AdminPage extends AppCompatActivity {

    //Access admin database
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseRefAdmin;

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
    private List<Bikes> bikesRentListAv;
    private List<RentedBikes> bikesListRented;
    private List<BikesShare> bikesListAvShare;

    private int numberStoresAvailable;
    private int numberBikesAvRent;
    private int numberBikesAvShare;
    private int numberBikesRented;

    private TextView tVAdminDetails, tVAdminPersonalDetails, tVAdminStoresAv, tVAdminBikesRentAv, tVAdminBikesRented, tVAdminBikesShareAv;

    //Declaring some objects
    private DrawerLayout drawerLayoutAdmin;
    private ActionBarDrawerToggle drawerToggleAdmin;
    private NavigationView navigationViewAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_page);

        Objects.requireNonNull(getSupportActionBar()).setTitle("ADMIN Page");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        //Retrieve data from Admins table
        databaseRefAdmin = FirebaseDatabase.getInstance().getReference("Admins");

        //Retrieve data from Bike Store table
        dbRefBikeStoresAv = FirebaseDatabase.getInstance().getReference("Bike Stores");

        //Retrieve data from Bikes table
        dbRefBikesRentAv = FirebaseDatabase.getInstance().getReference("Bikes");

        //Retrieve data from Rent Bikes table
        dbRefBikesRent = FirebaseDatabase.getInstance().getReference("Rent Bikes");

        //Retrieve data Share Bikes table
        dbRefBikesShareAv = FirebaseDatabase.getInstance().getReference("Share Bikes");

        bikeStoresList = new ArrayList<>();
        bikesRentListAv = new ArrayList<>();
        bikesListRented = new ArrayList<>();
        bikesListAvShare = new ArrayList<>();

        tVAdminDetails = findViewById(R.id.tvAdminDetails);
        tVAdminPersonalDetails = findViewById(R.id.tvAdminPersonalDetails);
        tVAdminStoresAv = findViewById(R.id.tvAdminStoresAv);
        tVAdminBikesRentAv = findViewById(R.id.tvAdminBikesRentAv);
        tVAdminBikesRented = findViewById(R.id.tvAdminBikesRentedAv);
        tVAdminBikesShareAv = findViewById(R.id.tvAdminBikesShareAv);

        drawerLayoutAdmin = findViewById(R.id.activity_admin_page);
        navigationViewAdmin = findViewById(R.id.navViewAdmin);

        drawerToggleAdmin = new ActionBarDrawerToggle(this, drawerLayoutAdmin, R.string.open_adminPage, R.string.close_adminPage);

        drawerLayoutAdmin.addDrawerListener(drawerToggleAdmin);
        drawerToggleAdmin.syncState();

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        databaseRefAdmin.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"SetTextI18n", "NonConstantResourceId"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    final Admins admins_Data = postSnapshot.getValue(Admins.class);

                    if (firebaseUser.getUid().equals(postSnapshot.getKey())) {

                        assert admins_Data != null;

                        tVAdminDetails.setText("Welcome: " + admins_Data.getFirstName_Admin() + " " + admins_Data.getLastName_Admin());

                        tVAdminPersonalDetails.setText("Phone:\n" + admins_Data.getPhoneNumb_Admin() + "\n\nEmail:\n" + admins_Data.getEmail_Admin());

                        //Adding Click Events to navigation drawer item
                        navigationViewAdmin.setNavigationItemSelectedListener(item -> {
                            int id = item.getItemId();
                            switch (id) {
                                //Add new Bike Stores
                                case R.id.adminAdd_bikeStores:
                                    Toast.makeText(AdminPage.this, "Add Bike Stores", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(AdminPage.this, CalculateCoordinates.class));
                                    break;

                                //Show the list of Bike Stores available
                                case R.id.adminShow_bikeStores:
                                    Toast.makeText(AdminPage.this, "Show Bike Stores", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(AdminPage.this, BikeStoreImageShowStoresListAdmin.class));
                                    break;

                                //Add Bikes to the Bike Stores available
                                case R.id.adminAdd_bikesToStore:
                                    Toast.makeText(AdminPage.this, "Add Bikes to Store", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(AdminPage.this, BikeStoreImageAddBikesAdmin.class));
                                    break;

                                //Show the list of Bikes available ordered by Bike Stores
                                case R.id.adminShow_bikesList:
                                    Toast.makeText(AdminPage.this, "Show Bikes List", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(AdminPage.this, BikeStoreImageShowBikesListAdmin.class));
                                    break;

                                //Show the full list of Bikes available
                                case R.id.adminShow_bikesListAll:
                                    Toast.makeText(AdminPage.this, "Show Full List of Bikes", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(AdminPage.this, BikeImageShowBikesListAdminAll.class));
                                    break;

                                //Show the full list of rented Bikes
                                case R.id.adminShow_bikesRented:
                                    Toast.makeText(AdminPage.this, "Rented Bikes", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(AdminPage.this, BikeStoreImageShowRentedBikesListAdmin.class));
                                    break;

                                //Show the full list of rented Bikes
                                case R.id.adminShow_bikesRentedAll:
                                    Toast.makeText(AdminPage.this, "Rented Bikes All", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(AdminPage.this, BikeImageShowBikesRentedAdminAll.class));
                                    break;

                                //Show the full list of rented Bikes
                                case R.id.adminShow_bikesShared:
                                    Toast.makeText(AdminPage.this, "Shared Bikes", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(AdminPage.this, BikeImageShowSharedBikesAdmin.class));
                                    break;
                                default:
                                    return true;
                            }
                            return true;
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminPage.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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
    private void LogOut() {
        finish();
        startActivity(new Intent(AdminPage.this, MainActivity.class));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        if (drawerToggleAdmin.onOptionsItemSelected(item)) {
            return true;
        }

        if (item.getItemId() == R.id.logOutAdmin) {
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

                    if (bikeStoresList.size() == 0) {
                        tVAdminStoresAv.setText(String.valueOf(0));
                    }
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
                bikesRentListAv.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Bikes bikes = postSnapshot.getValue(Bikes.class);
                    assert bikes != null;
                    bikes.setBike_Key(postSnapshot.getKey());
                    bikesRentListAv.add(bikes);
                    numberBikesAvRent = bikesRentListAv.size();
                    tVAdminBikesRentAv.setText(String.valueOf(numberBikesAvRent));

                    if (bikesRentListAv.size() == 0) {
                        tVAdminBikesRentAv.setText(String.valueOf(0));
                    }
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
                    RentedBikes rented_Bikes = postSnapshot.getValue(RentedBikes.class);
                    assert rented_Bikes != null;
                    rented_Bikes.setBike_RentKey(postSnapshot.getKey());
                    bikesListRented.add(rented_Bikes);
                    numberBikesRented = bikesListRented.size();
                    tVAdminBikesRented.setText(String.valueOf(numberBikesRented));

                    if (bikesListRented.size() == 0) {
                        tVAdminBikesRented.setText(String.valueOf(0));
                    }
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
                    BikesShare share_Bikes = postSnapshot.getValue(BikesShare.class);
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
