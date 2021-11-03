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

public class CustomerPageShareBikes extends AppCompatActivity {

    //Display data from Rent BikesRent table database
    private FirebaseStorage firebaseStBikesShareCustom;
    private DatabaseReference databaseRefBikesShareCustom;
    private ValueEventListener bikesShareCustomEventListener;

    //Display data from Rent BikesRent table database
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
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_page_share_bikes);

        bikesListShareCustom = new ArrayList<>();
        bikesListShareAv = new ArrayList<>();

        //initialise the variables
        tVCustomPageShare = (TextView) findViewById(R.id.tvCustomPageShare);
        tVCustomPageSharePerDetails = (TextView)findViewById(R.id.tvCustomPageSharePerDetails);
        tVCustomerBikesShared = (TextView)findViewById(R.id.tvCustomerBikesShared);
        tVBikesShareAv = (TextView)findViewById(R.id.tvCustomerBikesSharedAv);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        drawerLayoutUserShare = findViewById(R.id.activity_customer_page_share_bikes);
        navigationViewUserShare = findViewById(R.id.navViewCustomShare);
        toolbar = findViewById(R.id.toolbarCustomPageShare);

        drawerToggleUserShare = new ActionBarDrawerToggle(this, drawerLayoutUserShare, toolbar, R.string.open_customPageShare, R.string.close_customPageShare);

        drawerLayoutUserShare.addDrawerListener(drawerToggleUserShare);
        drawerToggleUserShare.syncState();

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
                    if (user_Db.getUid().equals(ds_User.getKey())) {
                        tVCustomPageShare.setText("Welcome: " + custom_Data.getfName_Customer() + " " + custom_Data.getlName_Customer());
                        tVCustomPageSharePerDetails.setText("Phone: \n"+custom_Data.getPhoneNumb_Customer()+"\n\nEmail: \n"+custom_Data.getEmail_Customer());
                        //Adding Click Events to our navigation drawer item
                        navigationViewUserShare.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                            @SuppressLint("NonConstantResourceId")
                            @Override
                            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                                int id = item.getItemId();
                                switch (id) {
                                    case R.id.userShow_shareBikesAvailable:
                                        Intent intentNoOwnerBikes = new Intent(CustomerPageShareBikes.this, BikesImageShowSharedBikesNoOwner.class);
                                        intentNoOwnerBikes.putExtra("CIdNoOwner", user_Db.getUid());
                                        startActivity(intentNoOwnerBikes);
                                        break;
                                    case R.id.userAdd_shareBikes:
                                        startActivity(new Intent(CustomerPageShareBikes.this, AddBikeShare.class));
                                        break;
                                    //Show the customer's own bikes added available to be shared
                                    case R.id.userShow_ownShareBikes:
                                        Intent intentShowBikes = new Intent(CustomerPageShareBikes.this, BikesImageShowSharedBikesOwner.class);
                                        intentShowBikes.putExtra("CFNameShare", custom_Data.getfName_Customer());
                                        intentShowBikes.putExtra("CLNameShare", custom_Data.getlName_Customer());
                                        intentShowBikes.putExtra("CIdShare", user_Db.getUid());
                                        startActivity(intentShowBikes);
                                        break;
                                    case R.id.userUpdate_ownShareBikes:
                                        Intent intentUpdate = new Intent(CustomerPageShareBikes.this, BikesImageShowSharedBikesToUpdate.class);
                                        intentUpdate.putExtra("CIdUpdate", user_Db.getUid());
                                        startActivity(intentUpdate);
                                        break;
                                    case R.id.userRemove_ownShareBikes:
                                        Intent intentRemoveBikes = new Intent(CustomerPageShareBikes.this, BikesImageRemoveSharedBikesOwner.class);
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

        switch (item.getItemId()) {
            case R.id.userShareGoBack: {
                goBackShare();
            }
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
        //initialize the bike storage database
        firebaseStBikesShareAv = FirebaseStorage.getInstance();
        databaseRefBikesShareAv = FirebaseDatabase.getInstance().getReference("Share Bikes");

        bikesShareAvEventListener = databaseRefBikesShareAv.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bikesListShareAv.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    BikesShare share_Bikes = postSnapshot.getValue(BikesShare.class);
                    assert share_Bikes != null;
                    if (!share_Bikes.getShareBikes_CustomId().equals(currentUser.getUid())){
                        share_Bikes.setShareBike_Key(postSnapshot.getKey());
                        bikesListShareAv.add(share_Bikes);
                        numberBikesShareAv = bikesListShareAv.size();
                        tVBikesShareAv.setText(String.valueOf(numberBikesShareAv));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CustomerPageShareBikes.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadBikeShareCustom() {
        //initialize the bike storage database
        firebaseStBikesShareCustom = FirebaseStorage.getInstance();
        databaseRefBikesShareCustom = FirebaseDatabase.getInstance().getReference("Share Bikes");

        bikesShareCustomEventListener = databaseRefBikesShareCustom.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bikesListShareCustom.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    BikesShare share_Bikes = postSnapshot.getValue(BikesShare.class);
                    assert share_Bikes != null;
                    if (share_Bikes.getShareBikes_CustomId().equals(currentUser.getUid())){
                        share_Bikes.setShareBike_Key(postSnapshot.getKey());
                        bikesListShareCustom.add(share_Bikes);
                        numberBikesSCustom = bikesListShareCustom.size();
                        tVCustomerBikesShared.setText(String.valueOf(numberBikesSCustom));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CustomerPageShareBikes.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
