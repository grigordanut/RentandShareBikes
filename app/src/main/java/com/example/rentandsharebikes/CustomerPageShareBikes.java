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

public class CustomerPageShareBikes extends AppCompatActivity {

    //Display data from Rent Bikes table database
    private FirebaseStorage firebaseStBikesShareCustom;
    private DatabaseReference databaseRefBikesShareCustom;
    private ValueEventListener bikesShareCustomEventListener;

    //Display data from Rent Bikes table database
    private FirebaseStorage firebaseStBikesShareAv;
    private DatabaseReference databaseRefBikesShareAv;
    private ValueEventListener bikesShareAvEventListener;

    //Access customer database
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private DatabaseReference databaseReference;

    private List<BikesShare> bikesListShareCustom;
    private List<BikesShare> bikesListShareAv;

    private int numberBikesSCustom;
    private int numberBikesShareAv;

    private TextView tVCustomPageShare, tVCustomPageSharePerDetails, tVCustomerBikesShared, tVBikesShareAv;

    //Declaring some objects
    private DrawerLayout drawerLayoutUserShare;
    private ActionBarDrawerToggle drawerToggleUserShare;
    private NavigationView navigationViewUserShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_page_share_bikes);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Customer page share Bikes");

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        //initialize the bike storage database
        firebaseStBikesShareAv = FirebaseStorage.getInstance();
        databaseRefBikesShareAv = FirebaseDatabase.getInstance().getReference("Share Bikes");

        //initialize the bike storage database
        firebaseStBikesShareCustom = FirebaseStorage.getInstance();
        databaseRefBikesShareCustom = FirebaseDatabase.getInstance().getReference("Share Bikes");

        bikesListShareCustom = new ArrayList<>();
        bikesListShareAv = new ArrayList<>();

        //initialise the variables
        tVCustomPageShare = (TextView) findViewById(R.id.tvCustomPageShare);
        tVCustomPageSharePerDetails = (TextView) findViewById(R.id.tvCustomPageSharePerDetails);
        tVCustomerBikesShared = (TextView) findViewById(R.id.tvCustomerBikesShared);
        tVBikesShareAv = (TextView) findViewById(R.id.tvCustomerBikesSharedAv);

        drawerLayoutUserShare = findViewById(R.id.activity_customer_page_share_bikes);
        navigationViewUserShare = findViewById(R.id.navViewCustomShare);

        drawerToggleUserShare = new ActionBarDrawerToggle(this, drawerLayoutUserShare, R.string.open_customPageShare, R.string.close_customPageShare);

        drawerLayoutUserShare.addDrawerListener(drawerToggleUserShare);
        drawerToggleUserShare.syncState();

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

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
                    if (user_Db.getUid().equals(ds_User.getKey())) {
                        tVCustomPageShare.setText("Welcome: " + custom_Data.getfName_Customer() + " " + custom_Data.getlName_Customer());
                        tVCustomPageSharePerDetails.setText("Phone: \n" + custom_Data.getPhoneNumb_Customer() + "\n\nEmail: \n" + custom_Data.getEmail_Customer());
                        //Adding Click Events to our navigation drawer item
                        navigationViewUserShare.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                            @SuppressLint("NonConstantResourceId")
                            @Override
                            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                                int id = item.getItemId();
                                switch (id) {
                                    case R.id.userShow_shareBikesAvailable:
                                        Intent intentNoOwnerBikes = new Intent(CustomerPageShareBikes.this, BikeImageShowSharedBikesNoOwner.class);
                                        intentNoOwnerBikes.putExtra("CIdNoOwner", user_Db.getUid());
                                        startActivity(intentNoOwnerBikes);
                                        break;
                                    case R.id.userAdd_shareBikes:
                                        startActivity(new Intent(CustomerPageShareBikes.this, AddBikeShare.class));
                                        break;
                                    //Show the customer's own bikes added available to be shared
                                    case R.id.userShow_ownShareBikes:
                                        Intent intentShowBikes = new Intent(CustomerPageShareBikes.this, BikeImageShowSharedBikesOwner.class);
                                        intentShowBikes.putExtra("CFNameShare", custom_Data.getfName_Customer());
                                        intentShowBikes.putExtra("CLNameShare", custom_Data.getlName_Customer());
                                        intentShowBikes.putExtra("CIdShare", user_Db.getUid());
                                        startActivity(intentShowBikes);
                                        break;
                                    case R.id.userUpdate_ownShareBikes:
                                        Intent intentUpdate = new Intent(CustomerPageShareBikes.this, BikeImageShowSharedBikesToUpdate.class);
                                        intentUpdate.putExtra("CIdUpdate", user_Db.getUid());
                                        startActivity(intentUpdate);
                                        break;
                                    case R.id.userRemove_ownShareBikes:
                                        Intent intentRemoveBikes = new Intent(CustomerPageShareBikes.this, BikeImageRemoveSharedBikesOwner.class);
                                        intentRemoveBikes.putExtra("CIdRemove", user_Db.getUid());
                                        startActivity(intentRemoveBikes);
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

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (drawerToggleUserShare.onOptionsItemSelected(item)) {
            return true;
        }

        if (item.getItemId() == R.id.userShareGoBack) {
            goBackShare();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        loadBikeShareAvailable();
        loadBikeShareCustom();
    }

    private void loadBikeShareAvailable() {

        bikesShareAvEventListener = databaseRefBikesShareAv.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                bikesListShareAv.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    BikesShare share_Bikes = postSnapshot.getValue(BikesShare.class);
                    assert share_Bikes != null;
                    share_Bikes.setShareBike_Key(postSnapshot.getKey());

                    if (!share_Bikes.getShareBikes_CustomId().equals(currentUser.getUid())) {
                        bikesListShareAv.add(share_Bikes);
                    }

                    numberBikesShareAv = bikesListShareAv.size();
                }

                tVBikesShareAv.setText(String.valueOf(numberBikesShareAv));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CustomerPageShareBikes.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadBikeShareCustom() {

        bikesShareCustomEventListener = databaseRefBikesShareCustom.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                bikesListShareCustom.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    BikesShare share_Bikes = postSnapshot.getValue(BikesShare.class);
                    assert share_Bikes != null;
                    share_Bikes.setShareBike_Key(postSnapshot.getKey());

                    if (share_Bikes.getShareBikes_CustomId().equals(currentUser.getUid())) {
                        bikesListShareCustom.add(share_Bikes);
                    }

                    numberBikesSCustom = bikesListShareCustom.size();
                }

                tVCustomerBikesShared.setText(String.valueOf(numberBikesSCustom));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CustomerPageShareBikes.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
