package com.example.rentandsharebikes;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
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

public class CustomerPage extends AppCompatActivity {

    //Declaring some objects
    private DrawerLayout drawerLayoutUser;
    private ActionBarDrawerToggle drawerToggleUser;
    private NavigationView navigationViewUser;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private DatabaseReference databaseReference;

    private TextView tViewCustomerPage, tViewDMCustomer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_page);

        //initialise the variables
        tViewCustomerPage = (TextView) findViewById(R.id.tvCustomerPage);
        //tViewDMCustomer = (TextView)findViewById(R.id.tvDMCustomer

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        drawerLayoutUser = findViewById(R.id.activity_customer_page);
        drawerToggleUser = new ActionBarDrawerToggle(this, drawerLayoutUser, R.string.open_userPage, R.string.close_userPage);

        drawerLayoutUser.addDrawerListener(drawerToggleUser);
        drawerToggleUser.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationViewUser = findViewById(R.id.navViewCustomer);

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
                        tViewCustomerPage.setText("Welcome: " + custom_data.getfName_Customer() + " " + custom_data.getlName_Customer());


                        //Adding Click Events to our navigation drawer item
                        navigationViewUser.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                            @Override
                            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                                int id = item.getItemId();
                                switch (id) {
                                    case R.id.userShow_storesList:
                                        Toast.makeText(CustomerPage.this, "Bike Stores", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(CustomerPage.this, BikeStoreImageShowStoresListCustomer.class));
                                        break;
                                    case R.id.userShow_bikesList:
                                        Toast.makeText(CustomerPage.this, "Bikes Available", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(CustomerPage.this, BikeStoreImageShowBikesListCustomer.class));
                                        break;
                                    case R.id.userRent_bikes:
                                        Toast.makeText(CustomerPage.this, "Rent Bikes", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(CustomerPage.this, BikeStoreImageRentBikesCustomer.class));
                                        break;
                                    case R.id.userShow_bikesRented:
                                        Toast.makeText(CustomerPage.this, "Show Bikes Rented", Toast.LENGTH_SHORT).show();
                                        Intent intentRent = new Intent(CustomerPage.this, BikesImageShowBikesRentedCustomer.class);
                                        intentRent.putExtra("CFName", custom_data.getfName_Customer());
                                        intentRent.putExtra("CLName", custom_data.getlName_Customer());
                                        intentRent.putExtra("CId", currentUser.getUid());
                                        startActivity(intentRent);
                                        break;

                                    case R.id.userReturn_bikes:
                                        Toast.makeText(CustomerPage.this, "Return Bikes", Toast.LENGTH_SHORT).show();
                                        Intent intentReturn = new Intent(CustomerPage.this, BikesImageReturnBikesRented.class);
                                        intentReturn.putExtra("CFName", custom_data.getfName_Customer());
                                        intentReturn.putExtra("CLName", custom_data.getlName_Customer());
                                        intentReturn.putExtra("CId", currentUser.getUid());
                                        startActivity(intentReturn);

                                        break;
                                    case R.id.userShare_bikes:
                                        Toast.makeText(CustomerPage.this, "Share Bikes", Toast.LENGTH_SHORT).show();

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
                Toast.makeText(CustomerPage.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void editProfile() {
        finish();
        startActivity(new Intent(CustomerPage.this, EditCustomerProfile.class));
    }

    private void changePassword() {
        finish();
        startActivity(new Intent(CustomerPage.this, ChangePassword.class));
    }

    //user log out
    private void LogOut() {
        confirmLogOut();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_customer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (drawerToggleUser.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.editProfile: {
                editProfile();
            }
        }

        switch (item.getItemId()) {
            case R.id.changePassword: {
                changePassword();
            }
        }
        switch (item.getItemId()) {
            case R.id.logOutUser: {
                LogOut();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void confirmLogOut() {
        AlertDialog.Builder builderAlert = new AlertDialog.Builder(CustomerPage.this);
        builderAlert.setMessage("Are sure to Log Out?");
        builderAlert.setCancelable(true);
        builderAlert.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        firebaseAuth.signOut();
                        finish();
                        startActivity(new Intent(CustomerPage.this, MainActivity.class));
                    }
                });

        builderAlert.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert1 = builderAlert.create();
        alert1.show();
    }
}
