package com.example.rentandsharebikes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class CustomerPageRentBikes extends AppCompatActivity {

    //Display data from Bike Stores database
    private FirebaseStorage firebaseStBikeSoresCustom;
    private DatabaseReference databaseRefBikeStoresCustom;
    private ValueEventListener bikeStoresCustomEventListener;

    //Display data from BikesRent table database
    private FirebaseStorage firebaseStBikesAvRentCustom;
    private DatabaseReference databaseRefBikesAvRentCustom;
    private ValueEventListener bikesAvRentCustomEventListener;

    //Display data from Rent BikesRent table database
    private FirebaseStorage firebaseStBikesRentCustom;
    private DatabaseReference databaseRefBikesRentCustom;
    private ValueEventListener bikesRentCustomEventListener;

    //Access customer database
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private DatabaseReference databaseReference;

    private List<BikeStores> bikeStoresListCustom;
    private List<BikesRent> bikesRentListAvRentCustom;
    private List<RentBikes> bikesListRentCustom;

    private int numberStoresAvCustom;
    private int numberBikesAvRentCustom;
    private int numberBikesRentedCustom;

    private TextView tVCustomPageRent, tVCustomPageRentPerDetails,tVCustomStoresAv, tVCustomBikesRentAv, tVCustomBikesRented;

    //Declaring some objects
    private DrawerLayout drawerLayoutUserRent;
    private ActionBarDrawerToggle drawerToggleUserRent;
    private NavigationView navigationViewUserRent;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_page_rent_bikes);

        bikeStoresListCustom = new ArrayList<>();
        bikesRentListAvRentCustom = new ArrayList<>();
        bikesListRentCustom = new ArrayList<>();

        //initialise the variables
        tVCustomPageRent = (TextView) findViewById(R.id.tvCustomPageRent);
        tVCustomPageRentPerDetails = (TextView)findViewById(R.id.tvCustomPageRentPerDetails);

        tVCustomStoresAv = (TextView)findViewById(R.id.tvCustomStoresAv);
        tVCustomBikesRentAv = (TextView)findViewById(R.id.tvCustomBikesRentAv);
        tVCustomBikesRented = (TextView)findViewById(R.id.tvCustomBikesRented);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        drawerLayoutUserRent = findViewById(R.id.activity_customer_page_rent_bikes);
        navigationViewUserRent = findViewById(R.id.navViewCustomRent);
        toolbar = findViewById(R.id.toolbarCustomPageRent);

        drawerToggleUserRent = new ActionBarDrawerToggle(this, drawerLayoutUserRent, toolbar, R.string.open_customPageRent, R.string.close_customPageRent);

        drawerLayoutUserRent.addDrawerListener(drawerToggleUserRent);
        drawerToggleUserRent.syncState();

        setSupportActionBar(toolbar);
        //Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        //retrieve data from database into text views
        databaseReference = FirebaseDatabase.getInstance().getReference("Customers");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"SetTextI18n", "NewApi"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //retrieve data from database
                for (DataSnapshot ds_User : dataSnapshot.getChildren()) {
                    final FirebaseUser user_Db = firebaseAuth.getCurrentUser();

                    final Customers custom_Data = ds_User.getValue(Customers.class);

                    assert user_Db != null;
                    assert custom_Data != null;
                    if (user_Db.getUid().equalsIgnoreCase(ds_User.getKey())) {
                        tVCustomPageRent.setText("Welcome: " + custom_Data.getfName_Customer() + " " + custom_Data.getlName_Customer());
                        tVCustomPageRentPerDetails.setText("Phone: \n"+custom_Data.getPhoneNumb_Customer()+"\n\nEmail: \n"+custom_Data.getEmail_Customer());

                        //Adding Click Events to our navigation drawer item
                        navigationViewUserRent.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                            @SuppressLint("NonConstantResourceId")
                            @Override
                            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                                int id = item.getItemId();
                                switch (id) {
                                    //Show the list of Bike Stores available
                                    case R.id.userShow_storesList:
                                        startActivity(new Intent(CustomerPageRentBikes.this, BikeStoreImageShowStoresListCustomer.class));
                                        break;
                                    //Show the list of BikesRent available from customer page ordered by Bike Stores
                                    case R.id.userShow_bikesList:
                                        startActivity(new Intent(CustomerPageRentBikes.this, BikeStoreImageShowBikesListCustom.class));
                                        break;
                                    //Show the list of all BikesRent available from customer page
                                    case R.id.userShow_bikesListAll:
                                        startActivity(new Intent(CustomerPageRentBikes.this, BikesImageShowBikesListCustomAll.class));
                                        break;
                                    //The activity of renting bikes by the customer
                                    case R.id.userRent_bikes:
                                        startActivity(new Intent(CustomerPageRentBikes.this, BikeStoreImageRentBikesCustom.class));
                                        break;
                                    //Show the list of BikesRent rented by customer
                                    case R.id.userShow_bikesRented:
                                        Intent intentRent = new Intent(CustomerPageRentBikes.this, BikesImageShowBikesRentedCustom.class);
                                        intentRent.putExtra("CFName", custom_Data.getfName_Customer());
                                        intentRent.putExtra("CLName", custom_Data.getlName_Customer());
                                        intentRent.putExtra("CId", user_Db.getUid());
                                        startActivity(intentRent);
                                        break;
                                    //The activity of returning rented bikes by the customer
                                    case R.id.userReturn_bikes:
                                        Intent intentReturn = new Intent(CustomerPageRentBikes.this, BikesImageReturnBikesRented.class);
                                        intentReturn.putExtra("CFName", custom_Data.getfName_Customer());
                                        intentReturn.putExtra("CLName", custom_Data.getlName_Customer());
                                        intentReturn.putExtra("CId", user_Db.getUid());
                                        startActivity(intentReturn);
                                        break;
                                    //The activity of scanning the QR code
                                    case R.id.userScan_QRCode:
                                        Intent intentScan = new Intent(CustomerPageRentBikes.this, Scanner.class);
                                        startActivity(intentScan);
                                        break;
                                    default:
                                        return true;
                                }
                                return true;
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CustomerPageRentBikes.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goBackRent() {
        finish();
        startActivity(new Intent(CustomerPageRentBikes.this, CustomerPageMain.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_customer_rent_bikes, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (drawerToggleUserRent.onOptionsItemSelected(item)) {
            return true;
        }

        if (item.getItemId() == R.id.userRentGoBack) {
            goBackRent();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        loadBikeStoresAvCustom();
        loadBikeRentAvCustom();
        loadBikeRentedCustom();
    }

    private void loadBikeStoresAvCustom() {
        //initialize the bike storage database
        firebaseStBikeSoresCustom = FirebaseStorage.getInstance();
        databaseRefBikeStoresCustom = FirebaseDatabase.getInstance().getReference("Bike Stores");

        bikeStoresCustomEventListener = databaseRefBikeStoresCustom.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bikeStoresListCustom.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    BikeStores bike_Stores = postSnapshot.getValue(BikeStores.class);
                    assert bike_Stores != null;
                    bike_Stores.setBikeStore_Key(postSnapshot.getKey());
                    bikeStoresListCustom.add(bike_Stores);
                    numberStoresAvCustom = bikeStoresListCustom.size();
                    tVCustomStoresAv.setText(String.valueOf(numberStoresAvCustom));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CustomerPageRentBikes.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadBikeRentAvCustom() {
        //initialize the bike storage database
        firebaseStBikesAvRentCustom = FirebaseStorage.getInstance();
        databaseRefBikesAvRentCustom = FirebaseDatabase.getInstance().getReference("Bikes");

        bikesAvRentCustomEventListener = databaseRefBikesAvRentCustom.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bikesRentListAvRentCustom.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    BikesRent bikesRent = postSnapshot.getValue(BikesRent.class);
                    assert bikesRent != null;
                    bikesRent.setBike_Key(postSnapshot.getKey());
                    bikesRentListAvRentCustom.add(bikesRent);
                    numberBikesAvRentCustom = bikesRentListAvRentCustom.size();
                    tVCustomBikesRentAv.setText(String.valueOf(numberBikesAvRentCustom));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CustomerPageRentBikes.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadBikeRentedCustom() {
        //initialize the bike storage database
        firebaseStBikesRentCustom = FirebaseStorage.getInstance();
        databaseRefBikesRentCustom = FirebaseDatabase.getInstance().getReference("Rent Bikes");

        bikesRentCustomEventListener = databaseRefBikesRentCustom.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bikesListRentCustom.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    RentBikes rented_Bikes = postSnapshot.getValue(RentBikes.class);
                    assert rented_Bikes != null;
                    if(rented_Bikes.getCustomerId_RentBikes().equals(currentUser.getUid())){
                        rented_Bikes.setBike_RentKey(postSnapshot.getKey());
                        bikesListRentCustom.add(rented_Bikes);
                        numberBikesRentedCustom = bikesListRentCustom.size();
                        tVCustomBikesRented.setText(String.valueOf(numberBikesRentedCustom));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CustomerPageRentBikes.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
