package com.example.rentandsharebikes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class AdminPage extends AppCompatActivity {

    //Declaring some objects
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navigationViewAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_page);

        drawerLayout = findViewById(R.id.activity_admin_page);
        drawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.open, R.string.close);

        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationViewAdmin = findViewById(R.id.navViewAdmin);

        //Adding Click Events to our navigation drawer item
        navigationViewAdmin.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id){
                    case R.id.addBikeStores:
                        Toast.makeText(AdminPage.this, "Add Bike Stores",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AdminPage.this,CalculateCoordinates.class));
                        break;
                    case R.id.showBikeStores:
                        Toast.makeText(AdminPage.this, "Show Bike Stores",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AdminPage.this,BikeStoreImageShowStoresListAdmin.class));
                        break;
                    case R.id.addBikesToStore:
                        Toast.makeText(AdminPage.this, "Add Bikes to Store",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AdminPage.this, BikeStoreImageAddBikesAdmin.class));
                        break;
                    case R.id.showBikesList:
                        Toast.makeText(AdminPage.this, "Show Bikes List",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AdminPage.this, BikeStoreImageShowBikesListAdmin.class));
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
        startActivity(new Intent(AdminPage.this, LoginCustomer.class));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        if(drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.logOutUser:{
                LogOut();
            }
        }

        return super.onOptionsItemSelected(item);
    }




}
