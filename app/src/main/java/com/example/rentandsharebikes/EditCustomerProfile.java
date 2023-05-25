package com.example.rentandsharebikes;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class EditCustomerProfile extends AppCompatActivity {

    //declare variables
    private EditText newFirstName, newLastName,newUserName, newPhone, newEmail;
    private TextView textViewEditProfile;
    private Button buttonSave;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_customer_profile);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Edit Customer profile");

        progressDialog = new ProgressDialog(EditCustomerProfile.this);

        firebaseAuth = FirebaseAuth.getInstance();

        //initialise the variables
        newFirstName = (EditText) findViewById(R.id.etNewFirstName);
        newLastName = (EditText) findViewById(R.id.etNewLastName);
        newUserName = (EditText) findViewById(R.id.etNewUserName);
        newPhone = (EditText) findViewById(R.id.etNewPhone);
        newEmail = (EditText)findViewById(R.id.etNewEmail);

        textViewEditProfile = (TextView)findViewById(R.id.tvEditProfile);

        //load the user details in the edit texts
        databaseReference = FirebaseDatabase.getInstance().getReference("Customers");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //retrieve data from database
                for (DataSnapshot ds_User : dataSnapshot.getChildren()) {
                    FirebaseUser user_Db = firebaseAuth.getCurrentUser();

                    Customers custom_Data = ds_User.getValue(Customers.class);

                    assert user_Db != null;
                    assert custom_Data != null;
                    if (user_Db.getUid().equals(ds_User.getKey())){
                        newFirstName.setText(custom_Data.getfName_Customer());
                        newLastName.setText(custom_Data.getlName_Customer());
                        newUserName.setText(custom_Data.getuName_Customer());
                        newPhone.setText(custom_Data.getPhoneNumb_Customer());
                        newEmail.setText(custom_Data.getEmail_Customer());
                        textViewEditProfile.setText("Edit profile of: " +custom_Data.getfName_Customer()+" "+custom_Data.getlName_Customer());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EditCustomerProfile.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        //save the user details in the database
        buttonSave = (Button) findViewById(R.id.btnSave);
        buttonSave.setOnClickListener(v -> {
            progressDialog.show();
            String newFirst_Name = newFirstName.getText().toString().trim();
            String newLast_Name = newLastName.getText().toString().trim();
            String newUser_Name = newUserName.getText().toString().trim();
            String new_Phone = newPhone.getText().toString().trim();
            String newEmail_Address = newEmail.getText().toString();

            String user_id = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
            DatabaseReference currentUser = databaseReference.child(user_id);
            Customers customersProf = new Customers(newFirst_Name, newLast_Name, newUser_Name, new_Phone,newEmail_Address);
            currentUser.setValue(customersProf);

            //clear data input fields
            newFirstName.getText().clear();
            newLastName.getText().clear();
            newUserName.getText().clear();
            newPhone.getText().clear();
            newEmail.getText().clear();

            progressDialog.dismiss();
            Toast.makeText(EditCustomerProfile.this, "Your details has been changed successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(EditCustomerProfile.this, LoginCustomer.class));
            finish();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_details_custom, menu);
        return true;
    }

    private void goBackEditCustom(){
        finish();
        startActivity(new Intent(EditCustomerProfile.this, CustomerPageMain.class));
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.editDetailsCustomGoBack) {
            goBackEditCustom();
        }

        return super.onOptionsItemSelected(item);
    }
}
