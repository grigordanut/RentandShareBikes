package com.example.rentandsharebikes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
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
import java.util.Objects;

public class CustomerPageRentBikes extends AppCompatActivity {

    //Declaring some objects
    private DrawerLayout drawerLayoutUserRent;
    private ActionBarDrawerToggle drawerToggleUserRent;
    private NavigationView navigationViewUserRent;

    //Display data from Bikes table database
    private FirebaseStorage firebaseStBikesRentCustom;
    private DatabaseReference databaseRefBikesRentCustom;
    private ValueEventListener bikesRentCustomEventListener;

    //Access customer database
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private DatabaseReference databaseReference;

    private TextView tVCustomPageRent, tVCustomPageRentPerDetails, tVCustomerBikesRented;

    private List<RentBikes> bikesListRentCustom;
    private int numberBikesRentedCustom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_page_rent_bikes);

        bikesListRentCustom = new ArrayList<>();

        //initialise the variables
        tVCustomPageRent = (TextView) findViewById(R.id.tvCustomPageRent);
        tVCustomPageRentPerDetails = (TextView)findViewById(R.id.tvCustomPageRentPerDetails);
        tVCustomerBikesRented = (TextView)findViewById(R.id.tvCustomerBikesRented);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        drawerLayoutUserRent = findViewById(R.id.activity_customer_page_rent_bikes);
        drawerToggleUserRent = new ActionBarDrawerToggle(this, drawerLayoutUserRent, R.string.open_customPageRent, R.string.close_customPageRent);

        drawerLayoutUserRent.addDrawerListener(drawerToggleUserRent);
        drawerToggleUserRent.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationViewUserRent = findViewById(R.id.navViewCustomRent);

        //retrieve data from database into text views
        databaseReference = FirebaseDatabase.getInstance().getReference("Customers");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"SetTextI18n", "NewApi"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //retrieve data from database
                for (final DataSnapshot dsUser : dataSnapshot.getChildren()) {
                    final FirebaseUser custom_Details = firebaseAuth.getCurrentUser();

                    final Customers custom_data = dsUser.getValue(Customers.class);

                    assert custom_Details != null;
                    assert custom_data != null;
                    if (Objects.requireNonNull(custom_Details.getEmail()).equalsIgnoreCase(custom_data.getEmail_Customer())) {
                        tVCustomPageRent.setText("Welcome: " + custom_data.getfName_Customer() + " " + custom_data.getlName_Customer());
                        tVCustomPageRentPerDetails.setText("Phone: \n"+custom_data.getPhoneNumb_Customer()+"\n\nEmail: \n"+custom_data.getEmail_Customer());

                        //Adding Click Events to our navigation drawer item
                        navigationViewUserRent.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                            @Override
                            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                                int id = item.getItemId();
                                switch (id) {
                                    //Show the list of Bike Stores available
                                    case R.id.userShow_storesList:
                                        Toast.makeText(CustomerPageRentBikes.this, "Bike Stores", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(CustomerPageRentBikes.this, BikeStoreImageShowStoresListCustomer.class));
                                        break;
                                    //Show the list of Bikes available from customer page ordered by Bike Stores
                                    case R.id.userShow_bikesList:
                                        Toast.makeText(CustomerPageRentBikes.this, "Bikes Available", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(CustomerPageRentBikes.this, BikeStoreImageShowBikesListCustom.class));
                                        break;
                                    //Show the list of all Bikes available from customer page
                                    case R.id.userShow_bikesListAll:
                                        Toast.makeText(CustomerPageRentBikes.this, "All Bikes Available", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(CustomerPageRentBikes.this, BikesImageShowBikesListCustomAll.class));
                                        break;
                                    //The activity of renting bikes by the customer
                                    case R.id.userRent_bikes:
                                        Toast.makeText(CustomerPageRentBikes.this, "Rent Bikes", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(CustomerPageRentBikes.this, BikeStoreImageRentBikesCustom.class));
                                        break;
                                    //Show the list of Bikes rented by customer
                                    case R.id.userShow_bikesRented:
                                        Toast.makeText(CustomerPageRentBikes.this, "Show Bikes Rented", Toast.LENGTH_SHORT).show();
                                        Intent intentRent = new Intent(CustomerPageRentBikes.this, BikesImageShowBikesRentedCustom.class);
                                        intentRent.putExtra("CFName", custom_data.getfName_Customer());
                                        intentRent.putExtra("CLName", custom_data.getlName_Customer());
                                        intentRent.putExtra("CId", currentUser.getUid());
                                        startActivity(intentRent);
                                        break;
                                    //The activity of returning rented bikes by the customer
                                    case R.id.userReturn_bikes:
                                        Toast.makeText(CustomerPageRentBikes.this, "Return Bikes", Toast.LENGTH_SHORT).show();
                                        Intent intentReturn = new Intent(CustomerPageRentBikes.this, BikesImageReturnBikesRented.class);
                                        intentReturn.putExtra("CFName", custom_data.getfName_Customer());
                                        intentReturn.putExtra("CLName", custom_data.getlName_Customer());
                                        intentReturn.putExtra("CId", currentUser.getUid());
                                        startActivity(intentReturn);
                                        break;

                                    case R.id.userScan_Barcode:
                                        Toast.makeText(CustomerPageRentBikes.this, "Return Bikes", Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (drawerToggleUserRent.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.userRentGoBack: {
                goBackRent();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        loadBikeRentCustom();
    }

    private void loadBikeRentCustom() {
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
                        tVCustomerBikesRented.setText(String.valueOf(numberBikesRentedCustom));
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
