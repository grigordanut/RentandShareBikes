package com.example.rentandsharebikes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

        //initialise the variables
        newFirstName = (EditText) findViewById(R.id.etNewFirstName);
        newLastName = (EditText) findViewById(R.id.etNewLastName);
        newUserName = (EditText) findViewById(R.id.etNewUserName);
        newPhone = (EditText) findViewById(R.id.etNewPhone);
        newEmail = (EditText)findViewById(R.id.etNewEmail);

        textViewEditProfile = (TextView)findViewById(R.id.tvEditProfile);

        progressDialog = new ProgressDialog(EditCustomerProfile.this);

        firebaseAuth = FirebaseAuth.getInstance();

        //load the user details in the edit texts
        databaseReference = FirebaseDatabase.getInstance().getReference("Customers");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressDialog.show();
                //retrieve data from database
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();

                    Customers customers = child.getValue(Customers.class);

                    progressDialog.dismiss();
                    assert user != null;
                    assert customers != null;
                    if (user.getUid().equals(child.getKey())){
                        newFirstName.setText(customers.getfName_Customer());
                        newLastName.setText(customers.getlName_Customer());
                        newUserName.setText(customers.getuName_Customer());
                        newPhone.setText(customers.getPhoneNumb_Customer());
                        newEmail.setText(customers.getEmail_Customer());
                        textViewEditProfile.setText("Edit profile of: " +customers.getfName_Customer()+" "+customers.getlName_Customer());
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
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                newFirstName.setText("");
                newLastName.setText("");
                newUserName.setText("");
                newPhone.setText("");
                newEmail.setText("");


                firebaseAuth.signOut();
                finish();
                Toast.makeText(EditCustomerProfile.this, "Your details has been changed successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(EditCustomerProfile.this, LoginCustomer.class));
                progressDialog.dismiss();
            }
        });
    }
}
