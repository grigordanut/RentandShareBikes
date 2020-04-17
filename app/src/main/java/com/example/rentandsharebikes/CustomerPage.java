package com.example.rentandsharebikes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class CustomerPage extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private TextView textViewCustomerPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_page);

        //initialise the variables
        textViewCustomerPage = (TextView) findViewById(R.id.tvCustomerPage);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        Button buttonRentBikes = (Button)findViewById(R.id.btnRentBikeCustom);
        buttonRentBikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CustomerPage.this, BikeStoreImageRentBikesCustomer.class));
            }
        });

        Button buttonShowBikeStores = (Button)findViewById(R.id.btnShowBikeStoresListCustom);
        buttonShowBikeStores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CustomerPage.this, BikeStoreImageShowStoresListCustomer.class));
            }
        });

        Button buttonShareBikes = (Button)findViewById(R.id.btnShareBikesCustom);
        buttonShareBikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(CustomerPage.this, UploadFiles.class));
            }
        });

        //retrieve data from database into text views
        databaseReference = FirebaseDatabase.getInstance().getReference("Customers");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"SetTextI18n", "NewApi"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //retrieve data from database
                for (DataSnapshot dsUser : dataSnapshot.getChildren()) {
                    final FirebaseUser custom_Details = firebaseAuth.getCurrentUser();

                    final Customers custom_data = dsUser.getValue(Customers.class);

                    assert custom_Details != null;
                    assert custom_data != null;
                    if (Objects.requireNonNull(custom_Details.getEmail()).equalsIgnoreCase(custom_data.getEmail_Customer())){
                        textViewCustomerPage.setText("Welcome: "+custom_data.getfName_Customer()+" "+custom_data.getlName_Customer());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CustomerPage.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //user log out
    private void LogOut(){
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(CustomerPage.this, MainActivity.class));
    }

    private void editProfile() {
        finish();
        startActivity(new Intent(CustomerPage.this, EditCustomerProfile.class));
    }

    private void changePassword() {
        finish();
        startActivity(new Intent(CustomerPage.this, ChangePassword.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_customer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logOutUser:{
                LogOut();
            }
        }
        switch (item.getItemId()) {
            case R.id.editProfile:{
                editProfile();
            }
        }

        switch (item.getItemId()) {
            case R.id.changePassword:{
                changePassword();
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
