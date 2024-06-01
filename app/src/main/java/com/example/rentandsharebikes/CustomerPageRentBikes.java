package com.example.rentandsharebikes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CustomerPageRentBikes extends AppCompatActivity {

    //Access customer database
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private DatabaseReference databaseRefCustom;

    private TextView tVCustomPageRent, tVCustomPageRentPerDetails;

    //Display data from Bike Stores database
    private DatabaseReference dbReferenceStoresAvCustom;
    private ValueEventListener evListenerStoresAvCustom;
    private List<BikeStores> listStoresAvCustom;
    private int numberStoresAvCustom;
    private TextView tVStoresAvCustom;

    //Display data from Bikes database
    private DatabaseReference dbReferenceBikesAvCustom;
    private ValueEventListener eventListenerBikesAvRentCustom;
    private List<Bikes> listBikesAvCustom;
    //private int numberBikesAvCustom;
    private TextView tVBikesAvCustom;

    //Display data from Rent Bikes database
    private DatabaseReference dbReferenceBikesRentedCustom;
    private ValueEventListener eventListenerBikesRentedCustom;
    private List<RentedBikes> listBikesRentedCustom;
    private int numberBikesRentedCustom;
    private TextView tVBikesRentedCustom;

    //Declare objects
    private DrawerLayout drawerLayoutUserRent;
    private ActionBarDrawerToggle drawerToggleUserRent;
    private NavigationView navigationViewUserRent;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_page_rent_bikes);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Customer page rent bikes");

        progressDialog = new ProgressDialog(CustomerPageRentBikes.this);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        tVCustomPageRent = findViewById(R.id.tvCustomPageRent);
        tVCustomPageRentPerDetails = findViewById(R.id.tvCustomPageRentPerDetails);

        //retrieve data from Bike Stores database
        dbReferenceStoresAvCustom = FirebaseDatabase.getInstance().getReference("Bike Stores");
        listStoresAvCustom = new ArrayList<>();
        tVStoresAvCustom = findViewById(R.id.tvStoresAvCustom);

        //retrieve data from Bikes database
        dbReferenceBikesAvCustom = FirebaseDatabase.getInstance().getReference("Bikes");
        listBikesAvCustom = new ArrayList<>();
        tVBikesAvCustom = findViewById(R.id.tvBikesAvCustom);

        //Retrieve data from Rent Bikes database
        dbReferenceBikesRentedCustom = FirebaseDatabase.getInstance().getReference("Rent Bikes");
        listBikesRentedCustom = new ArrayList<>();
        tVBikesRentedCustom = (TextView)findViewById(R.id.tvBikesRentedCustom);

        drawerLayoutUserRent = findViewById(R.id.activity_customer_page_rent_bikes);
        navigationViewUserRent = findViewById(R.id.navViewCustomRent);

        drawerToggleUserRent = new ActionBarDrawerToggle(this, drawerLayoutUserRent, R.string.open_customPageRent, R.string.close_customPageRent);

        drawerLayoutUserRent.addDrawerListener(drawerToggleUserRent);
        drawerToggleUserRent.syncState();

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        //retrieve data from database into text views
        databaseRefCustom = FirebaseDatabase.getInstance().getReference("Customers");
        databaseRefCustom.addValueEventListener(new ValueEventListener() {
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
                                        startActivity(new Intent(CustomerPageRentBikes.this, BikeStoreImageCustomerShowStores.class));
                                        break;
                                    //Show the list of Bikes available from customer page ordered by Bike Stores
                                    case R.id.userShow_bikesList:
                                        startActivity(new Intent(CustomerPageRentBikes.this, BikeStoreImageCustomerShowBikes.class));
                                        break;
                                    //Show the list of all Bikes available from customer page
                                    case R.id.userShow_bikesListAll:
                                        startActivity(new Intent(CustomerPageRentBikes.this, BikeImageCustomerShowBikesAll.class));
                                        break;
                                    //The activity of renting bikes by the customer
                                    case R.id.userRent_bikes:
                                        startActivity(new Intent(CustomerPageRentBikes.this, BikeStoreImageCustomerRentBikes.class));
                                        break;
                                    //Show the list of Bikes rented by customer
                                    case R.id.userShow_bikesRented:
                                        Intent intentRent = new Intent(CustomerPageRentBikes.this, BikeImageCustomerShowBikesRented.class);
                                        intentRent.putExtra("CFName", custom_Data.getfName_Customer());
                                        intentRent.putExtra("CLName", custom_Data.getlName_Customer());
                                        intentRent.putExtra("CId", user_Db.getUid());
                                        startActivity(intentRent);
                                        break;
                                    //The activity of returning rented bikes by the customer
                                    case R.id.userReturn_bikes:
                                        Intent intentReturn = new Intent(CustomerPageRentBikes.this, BikeImageReturnRentedBikesCustomer.class);
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
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
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
        loadStoresAvCustom();
        loadBikesAvCustom();
        loadBikeRentedCustom();
    }

    private void loadStoresAvCustom() {

        progressDialog.show();

        evListenerStoresAvCustom = dbReferenceStoresAvCustom.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                listStoresAvCustom.clear();

                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        BikeStores bike_Stores = postSnapshot.getValue(BikeStores.class);
                        assert bike_Stores != null;
                        bike_Stores.setBikeStore_Key(postSnapshot.getKey());
                        listStoresAvCustom.add(bike_Stores);
                        numberStoresAvCustom = listStoresAvCustom.size();
                        tVStoresAvCustom.setText(String.valueOf(numberStoresAvCustom));
                    }
                }

                else {
                    tVStoresAvCustom.setText(String.valueOf(0));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CustomerPageRentBikes.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        progressDialog.dismiss();
    }

    private void loadBikesAvCustom() {

        progressDialog.show();

        eventListenerBikesAvRentCustom = dbReferenceBikesAvCustom.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                listBikesAvCustom.clear();

                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Bikes bikes = postSnapshot.getValue(Bikes.class);
                        assert bikes != null;
                        bikes.setBike_Key(postSnapshot.getKey());
                        listBikesAvCustom.add(bikes);
                        tVBikesAvCustom.setText(String.valueOf(listBikesAvCustom.size()));
                    }
                }

                else {
                    tVBikesAvCustom.setText(String.valueOf(0));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CustomerPageRentBikes.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        progressDialog.dismiss();
    }

    private void loadBikeRentedCustom() {

        progressDialog.show();

        eventListenerBikesRentedCustom = dbReferenceBikesRentedCustom.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                listBikesRentedCustom.clear();

                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        RentedBikes rented_Bikes = postSnapshot.getValue(RentedBikes.class);
                        assert rented_Bikes != null;
                        if(rented_Bikes.getCustomerId_RentBikes().equals(currentUser.getUid())){
                            rented_Bikes.setBike_RentKey(postSnapshot.getKey());
                            listBikesRentedCustom.add(rented_Bikes);
                            numberBikesRentedCustom = listBikesRentedCustom.size();
                            tVBikesRentedCustom.setText(String.valueOf(numberBikesRentedCustom));
                        }
                    }
                }

                else {
                    tVBikesRentedCustom.setText(String.valueOf(0));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CustomerPageRentBikes.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        progressDialog.dismiss();
    }
}
