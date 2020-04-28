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

public class CustomerPageShareBikes extends AppCompatActivity {

    //Declaring some objects
    private DrawerLayout drawerLayoutUserShare;
    private ActionBarDrawerToggle drawerToggleUserShare;
    private NavigationView navigationViewUserShare;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private DatabaseReference databaseReference;

    private TextView tVCustomPageShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_page_share_bikes);

        //initialise the variables
        tVCustomPageShare = (TextView) findViewById(R.id.tvCustomPageShare);
        //tViewDMCustomer = (TextView)findViewById(R.id.tvDMCustomer

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        drawerLayoutUserShare = findViewById(R.id.activity_customer_page_share_bikes);
        drawerToggleUserShare = new ActionBarDrawerToggle(this, drawerLayoutUserShare, R.string.open_customPageShare, R.string.close_customPageShare);

        drawerLayoutUserShare.addDrawerListener(drawerToggleUserShare);
        drawerToggleUserShare.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationViewUserShare = findViewById(R.id.navViewCustomShare);

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
                        tVCustomPageShare.setText("Welcome: " + custom_data.getfName_Customer() + " " + custom_data.getlName_Customer());

                        //Adding Click Events to our navigation drawer item
                        navigationViewUserShare.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                            @Override
                            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                                int id = item.getItemId();
                                switch (id) {
                                    case R.id.userAdd_shareBikes:
                                        Toast.makeText(CustomerPageShareBikes.this, "Add bikes to share", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(CustomerPageShareBikes.this, ShareBikesCustomer.class));
                                        break;
                                    case R.id.userShow_ownShareBikes:
                                        Toast.makeText(CustomerPageShareBikes.this, "Show Bikes Shared", Toast.LENGTH_SHORT).show();
                                        Intent intentRent = new Intent(CustomerPageShareBikes.this, BikesImageShowOwnSharedBikes.class);
                                        intentRent.putExtra("CFNameShare", custom_data.getfName_Customer());
                                        intentRent.putExtra("CLNameShare", custom_data.getlName_Customer());
                                        intentRent.putExtra("CIdShare", currentUser.getUid());
                                        startActivity(intentRent);
                                        break;
                                    case R.id.userShow_shareBikesAvailable:
                                        Toast.makeText(CustomerPageShareBikes.this, "Share Bikes", Toast.LENGTH_SHORT).show();
                                        //startActivity(new Intent(CustomerPageShareBikes.this, ShareBikesCustomer.class));
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
                Toast.makeText(CustomerPageShareBikes.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goBackShare() {
        finish();
        startActivity(new Intent(CustomerPageShareBikes.this, CustomerPageMain.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_customer_share_bikes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (drawerToggleUserShare.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.userShareGoBack: {
                goBackShare();
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
