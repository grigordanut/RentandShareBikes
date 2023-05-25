package com.example.rentandsharebikes;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class RegisterCustomer extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private TextInputEditText firstNameRegCustom;
    private TextInputEditText lastNameRegCustom;
    private TextInputEditText userNameRegCustom;
    private TextInputEditText phoneNrRegCustom;
    private TextInputEditText emailRegCustom;
    private TextInputEditText passRegCustom;
    private TextInputEditText confPassRegCustom;
    private String firstName_regCustom, lastName_regCustom, userName_regCustom, phoneNr_RegCustom, email_regCustom, pass_regCustom, confPass_regCustom;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_customer);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Register Customer");

        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Customers");

        firstNameRegCustom = findViewById(R.id.etFirstNameRegCustom);
        lastNameRegCustom = findViewById(R.id.etLastNameRegCustom);
        userNameRegCustom = findViewById(R.id.etUserNameRegCustom);
        phoneNrRegCustom = findViewById(R.id.etPhoneNrRegCustom);
        emailRegCustom = findViewById(R.id.etEmailRegCustom);
        passRegCustom = findViewById(R.id.etPassRegCustom);
        confPassRegCustom = findViewById(R.id.etConfPassRegCustom);

        Button btn_SignInCustom = findViewById(R.id.btnSignInRegCustom);
        btn_SignInCustom.setOnClickListener(v -> {
            Intent intentLog = new Intent(RegisterCustomer.this, LoginCustomer.class);
            startActivity(intentLog);
        });

        Button btn_Register = findViewById(R.id.btnRegCustom);
        btn_Register.setOnClickListener(view -> {
            if (validateUserRegData()) {

                progressDialog.setMessage("Register User Details");
                progressDialog.show();

                firebaseAuth.createUserWithEmailAndPassword(email_regCustom, pass_regCustom)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                sendEmailVerification();
                                //clear input text fields
                                firstNameRegCustom.setText("");
                                lastNameRegCustom.setText("");
                                userNameRegCustom.setText("");
                                phoneNrRegCustom.setText("");
                                emailRegCustom.setText("");
                                passRegCustom.setText("");
                                confPassRegCustom.setText("");

                            } else {
                                alertDialogEmailUsed();
                            }
                            progressDialog.dismiss();
                        });
            }
        });
    }

    private Boolean validateUserRegData() {
        boolean result = false;
        firstName_regCustom = Objects.requireNonNull(firstNameRegCustom.getText()).toString().trim();
        lastName_regCustom = Objects.requireNonNull(lastNameRegCustom.getText()).toString().trim();
        userName_regCustom = Objects.requireNonNull(userNameRegCustom.getText()).toString().trim();
        phoneNr_RegCustom = Objects.requireNonNull(phoneNrRegCustom.getText()).toString().trim();
        email_regCustom = Objects.requireNonNull(emailRegCustom.getText()).toString().trim();
        pass_regCustom = Objects.requireNonNull(passRegCustom.getText()).toString().trim();
        confPass_regCustom = Objects.requireNonNull(confPassRegCustom.getText()).toString().trim();

        if (TextUtils.isEmpty(firstName_regCustom)) {
            firstNameRegCustom.setError("First Name can be empty");
            firstNameRegCustom.requestFocus();
        } else if (TextUtils.isEmpty(lastName_regCustom)) {
            lastNameRegCustom.setError("Last Name cannot be empty");
            lastNameRegCustom.requestFocus();
        } else if (userName_regCustom.isEmpty()) {
            userNameRegCustom.setError("User Name cannot be empty");
            userNameRegCustom.requestFocus();
        } else if (phoneNr_RegCustom.isEmpty()) {
            phoneNrRegCustom.setError("User Name cannot be empty");
            phoneNrRegCustom.requestFocus();
        } else if (email_regCustom.isEmpty()) {
            emailRegCustom.setError("Email Address cannot be empty");
            emailRegCustom.requestFocus();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email_regCustom).matches()) {
            Toast.makeText(RegisterCustomer.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            emailRegCustom.setError("Enter a valid Email Address");
            emailRegCustom.requestFocus();
        } else if (pass_regCustom.isEmpty()) {
            passRegCustom.setError("Password cannot be empty");
            passRegCustom.requestFocus();
        } else if (pass_regCustom.length() < 6) {
            passRegCustom.setError("The password is too short, enter minimum 6 character long");
            Toast.makeText(RegisterCustomer.this, "The password is too short, enter minimum 6 character long", Toast.LENGTH_SHORT).show();
        } else if (confPass_regCustom.isEmpty()) {
            confPassRegCustom.setError("Confirm Password cannot be empty");
            confPassRegCustom.requestFocus();
        } else if (!pass_regCustom.equals(confPass_regCustom)) {
            Toast.makeText(RegisterCustomer.this, "Confirm Password does not match Password", Toast.LENGTH_SHORT).show();
            confPassRegCustom.setError("The Password does not match");
            confPassRegCustom.requestFocus();
        } else {
            result = true;
        }
        return result;
    }

    private void sendEmailVerification() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            firebaseUser.sendEmailVerification().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    sendUserRegData();
                    alertDialogUserRegistered();
                } else {
                    Toast.makeText(RegisterCustomer.this, "Verification email has not been sent", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            });
        }
    }

    private void sendUserRegData() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        assert user != null;
        String userID = user.getUid();
        Customers customs = new Customers(firstName_regCustom, lastName_regCustom, userName_regCustom, phoneNr_RegCustom, email_regCustom);
        databaseReference.child(userID).setValue(customs);
    }

    private void alertDialogUserRegistered() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegisterCustomer.this);
        alertDialogBuilder
                .setMessage("Hi " + firstName_regCustom + " " + lastName_regCustom + " you are successfully registered, Email verification was sent. Please verify your email before Log in")
                .setCancelable(true)
                .setPositiveButton("Ok", (dialog, id) -> {
                            firebaseAuth.signOut();
                            finish();
                            startActivity(new Intent(RegisterCustomer.this, LoginCustomer.class));
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void alertDialogEmailUsed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegisterCustomer.this);
        alertDialogBuilder
                .setMessage("Registration failed, the email: \n" + email_regCustom + " was already used to open an account on this app.")
                .setCancelable(true)
                .setPositiveButton("OK", (dialog, id) -> emailRegCustom.requestFocus());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
