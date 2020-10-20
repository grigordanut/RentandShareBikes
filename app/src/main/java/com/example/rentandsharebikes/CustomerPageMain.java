package com.example.rentandsharebikes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class CustomerPageMain extends AppCompatActivity {

    //Variable to access current user database
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private TextView tVCustomPageMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_main_page);

        //initialise the variables
        tVCustomPageMain = (TextView) findViewById(R.id.tvCustomPageMain);

        firebaseAuth = FirebaseAuth.getInstance();
        //currentUser = firebaseAuth.getCurrentUser();

        Button buttonRentBikes = (Button) findViewById(R.id.btnRentBikeCustom);
        buttonRentBikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CustomerPageMain.this, CustomerPageRentBikes.class));
            }
        });

        Button buttonShareBikes = (Button) findViewById(R.id.btnShareBikesCustom);
        buttonShareBikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CustomerPageMain.this, CustomerPageShareBikes.class));
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        loadCustomerDetailsMainPage();
    }

    //load current user details
    private void loadCustomerDetailsMainPage() {
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

                    if(custom_Details.getUid().equals(dsUser.getKey())){
                        tVCustomPageMain.setText("Welcome: " + custom_data.getfName_Customer() + " " + custom_data.getlName_Customer());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CustomerPageMain.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void editProfile() {
        finish();
        startActivity(new Intent(CustomerPageMain.this, EditCustomerProfile.class));
    }

    private void changePassword() {
        finish();
        startActivity(new Intent(CustomerPageMain.this, ChangePassword.class));
    }

    //user log out
    private void LogOut() {
        SharedPreferences preferences = getSharedPreferences("checkbox",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("remember", "false");
        editor.apply();
        finish();
        startActivity(new Intent(CustomerPageMain.this, MainActivity.class));
        Toast.makeText(CustomerPageMain.this, "You are Log Out", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_customer_main_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.editProfile) {
            editProfile();
        }

        if (item.getItemId() == R.id.changePassword) {
            changePassword();
        }
        if (item.getItemId() == R.id.logOutUser) {
            LogOut();
        }

        return super.onOptionsItemSelected(item);
    }
}
