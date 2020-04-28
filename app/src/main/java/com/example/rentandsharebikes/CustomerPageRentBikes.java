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

import java.util.Objects;

public class CustomerPageRentBikes extends AppCompatActivity {

    //Declaring some objects
    private DrawerLayout drawerLayoutUserRent;
    private ActionBarDrawerToggle drawerToggleUserRent;
    private NavigationView navigationViewUserRent;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private DatabaseReference databaseReference;

    private TextView tVCustomPageRent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_page_rent_bikes);

        //initialise the variables
        tVCustomPageRent = (TextView) findViewById(R.id.tvCustomPageRent);
        //tViewDMCustomer = (TextView)findViewById(R.id.tvDMCustomer

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


                        //Adding Click Events to our navigation drawer item
                        navigationViewUserRent.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                            @Override
                            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                                int id = item.getItemId();
                                switch (id) {
                                    case R.id.userShow_storesList:
                                        Toast.makeText(CustomerPageRentBikes.this, "Bike Stores", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(CustomerPageRentBikes.this, BikeStoreImageShowStoresListCustomer.class));
                                        break;
                                    case R.id.userShow_bikesList:
                                        Toast.makeText(CustomerPageRentBikes.this, "Bikes Available", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(CustomerPageRentBikes.this, BikeStoreImageShowBikesListCustom.class));
                                        break;
                                    case R.id.userRent_bikes:
                                        Toast.makeText(CustomerPageRentBikes.this, "Rent Bikes", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(CustomerPageRentBikes.this, BikeStoreImageRentBikesCustom.class));
                                        break;
                                    case R.id.userShow_bikesRented:
                                        Toast.makeText(CustomerPageRentBikes.this, "Show Bikes Rented", Toast.LENGTH_SHORT).show();
                                        Intent intentRent = new Intent(CustomerPageRentBikes.this, BikesImageShowBikesRentedCustom.class);
                                        intentRent.putExtra("CFName", custom_data.getfName_Customer());
                                        intentRent.putExtra("CLName", custom_data.getlName_Customer());
                                        intentRent.putExtra("CId", currentUser.getUid());
                                        startActivity(intentRent);
                                        break;

                                    case R.id.userReturn_bikes:
                                        Toast.makeText(CustomerPageRentBikes.this, "Return Bikes", Toast.LENGTH_SHORT).show();
                                        Intent intentReturn = new Intent(CustomerPageRentBikes.this, BikesImageReturnBikesRented.class);
                                        intentReturn.putExtra("CFName", custom_data.getfName_Customer());
                                        intentReturn.putExtra("CLName", custom_data.getlName_Customer());
                                        intentReturn.putExtra("CId", currentUser.getUid());
                                        startActivity(intentReturn);
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
}
