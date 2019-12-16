package com.example.rentandsharebikes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterCustomer extends AppCompatActivity {
    private TextInputEditText firstNameRegCustom;
    private TextInputEditText lastNameRegCustom;
    private TextInputEditText userNameRegCustom;
    private TextInputEditText phoneNrRegCustom;
    private TextInputEditText emailRegCustom;
    private TextInputEditText passRegCustom;
    private TextInputEditText confPassRegCustom;
    private String firstName_regCustom, lastName_regCustom,userName_regCustom, phoneNr_RegCustom, email_regCustom, pass_regCustom, confPass_regCustom;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_customer);

        firstNameRegCustom = (TextInputEditText) findViewById(R.id.etFirstNameRegCustom);
        lastNameRegCustom = (TextInputEditText) findViewById(R.id.etLastNameRegCustom);
        userNameRegCustom = (TextInputEditText) findViewById(R.id.etUserNameRegCustom);
        phoneNrRegCustom = (TextInputEditText) findViewById(R.id.etPhoneNrRegCustom);
        emailRegCustom = (TextInputEditText) findViewById(R.id.etEmailRegCustom);
        passRegCustom = (TextInputEditText) findViewById(R.id.etPassRegCustom);
        confPassRegCustom = (TextInputEditText) findViewById(R.id.etConfPassRegCustom);

        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Customers");


        Button buttonSignInCustom = (Button) findViewById(R.id.btnSignInRegCustom);
        buttonSignInCustom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentLog = new Intent(RegisterCustomer.this, LoginCustomer.class);
                startActivity(intentLog);
            }
        });


        Button buttonRegister = (Button) findViewById(R.id.btnRegCustom);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                firstName_regCustom = firstNameRegCustom.getText().toString().trim();
                lastName_regCustom = lastNameRegCustom.getText().toString().trim();
                userName_regCustom = userNameRegCustom.getText().toString().trim();
                phoneNr_RegCustom = phoneNrRegCustom.getText().toString().trim();
                email_regCustom = emailRegCustom.getText().toString().trim();
                pass_regCustom = passRegCustom.getText().toString().trim();
                confPass_regCustom = confPassRegCustom.getText().toString().trim();

                if (firstName_regCustom.isEmpty()) {
                    firstNameRegCustom.setError("First Name can be empty");
                    firstNameRegCustom.requestFocus();
                }

                else if (lastName_regCustom.isEmpty()) {
                    lastNameRegCustom.setError("Last Name cannot be empty");
                    lastNameRegCustom.requestFocus();
                }

                else if (userName_regCustom.isEmpty()){
                    userNameRegCustom.setError("User Name cannot be empty");
                    userNameRegCustom.requestFocus();
                }

                else if (phoneNr_RegCustom.isEmpty()){
                    phoneNrRegCustom.setError("User Name cannot be empty");
                    phoneNrRegCustom.requestFocus();
                }

                else if (email_regCustom.isEmpty()) {
                    emailRegCustom.setError("Email Address cannot be empty");
                    emailRegCustom.requestFocus();
                }

                else if (!Patterns.EMAIL_ADDRESS.matcher(email_regCustom).matches()) {
                    Toast.makeText(RegisterCustomer.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                    emailRegCustom.setError("Enter a valid Email Address");
                    emailRegCustom.requestFocus();
                }

                else if (pass_regCustom.isEmpty()) {
                    passRegCustom.setError("Password cannot be empty");
                    passRegCustom.requestFocus();
                }

                else if (pass_regCustom.length()>0 && pass_regCustom.length()<6) {
                    passRegCustom.setError("The password is too short, enter minimum 6 character long");
                    Toast.makeText(RegisterCustomer.this, "The password is too short, enter minimum 6 character long", Toast.LENGTH_SHORT).show();
                }

                else if (confPass_regCustom.isEmpty()) {
                    confPassRegCustom.setError("Confirm Password cannot be empty");
                    confPassRegCustom.requestFocus();
                }

                else if (!pass_regCustom.equals(confPass_regCustom)) {
                    Toast.makeText(RegisterCustomer.this, "Confirm Password does not match Password", Toast.LENGTH_SHORT).show();
                    confPassRegCustom.setError("The Password does not match");
                    confPassRegCustom.requestFocus();
                }

                else {
                    //clear input text fields
                    firstNameRegCustom.setText("");
                    lastNameRegCustom.setText("");
                    userNameRegCustom.setText("");
                    phoneNrRegCustom.setText("");
                    emailRegCustom.setText("");
                    passRegCustom.setText("");
                    confPassRegCustom.setText("");

                    progressDialog.setMessage("Register User Details");
                    progressDialog.show();

                    firebaseAuth.createUserWithEmailAndPassword(email_regCustom, pass_regCustom).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                sendEmailVerification();
                            }

                            else {
                                progressDialog.dismiss();
                                Toast.makeText(RegisterCustomer.this, "Registration Failed, this email address was already used.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void sendEmailVerification() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        sendCustomerData();
                        progressDialog.dismiss();
                        Toast.makeText(RegisterCustomer.this, "User Registered, Email verification was sent", Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();
                        finish();
                        startActivity(new Intent(RegisterCustomer.this, LoginCustomer.class));
                    }
                    else {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterCustomer.this, "Verification email has not been sent", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void sendCustomerData(){

        FirebaseUser user = firebaseAuth.getCurrentUser();
        assert user != null;
        String userID = user.getUid();
        Customers customs = new Customers(firstName_regCustom, lastName_regCustom, userName_regCustom, phoneNr_RegCustom, email_regCustom);
        databaseReference.child(userID).setValue(customs);
    }
}
