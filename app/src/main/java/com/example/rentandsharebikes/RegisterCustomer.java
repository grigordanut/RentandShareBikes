package com.example.rentandsharebikes;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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

        Objects.requireNonNull(getSupportActionBar()).setTitle("Customer Registration");

        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Customers");

        firstNameRegCustom = findViewById(R.id.etFirstNameRegCustom);
        lastNameRegCustom = findViewById(R.id.etLastNameRegCustom);
        userNameRegCustom = findViewById(R.id.etUserNameRegCustom);
        phoneNrRegCustom = findViewById(R.id.etPhoneNrRegCustom);
        emailRegCustom = findViewById(R.id.etEmailRegCustom);
        passRegCustom = findViewById(R.id.etPassRegCustom);
        confPassRegCustom = findViewById(R.id.etConfPassRegCustom);

        Button btn_SignInCustom = findViewById(R.id.btnSignInRegCustom);
        btn_SignInCustom.setOnClickListener(v -> {
            Intent intentLog = new Intent(RegisterCustomer.this, Login.class);
            startActivity(intentLog);
        });

        Button btn_Register = findViewById(R.id.btnRegCustom);
        btn_Register.setOnClickListener(view -> registerCustomer());
    }

    public void registerCustomer() {

        if (validateUserRegData()) {

            progressDialog.setTitle("Registering customer details!!");
            progressDialog.show();

            firebaseAuth.createUserWithEmailAndPassword(email_regCustom, pass_regCustom).addOnCompleteListener(task -> {

                if (task.isSuccessful()) {

                    uploadUserData();

                } else {
                    try {
                        throw Objects.requireNonNull(task.getException());
                    } catch (Exception e) {

                        LayoutInflater inflater = getLayoutInflater();
                        @SuppressLint("InflateParams") View layout = inflater.inflate(R.layout.toast, null);
                        TextView text = layout.findViewById(R.id.tvToast);
                        ImageView imageView = layout.findViewById(R.id.imgToast);
                        text.setText(e.getMessage());
                        imageView.setImageResource(R.drawable.baseline_report_gmailerrorred_24);
                        Toast toast = new Toast(getApplicationContext());
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.setView(layout);
                        toast.show();
                    }
                }

                progressDialog.dismiss();
            });
        }
    }

    @SuppressLint("SetTextI18n")
    private void uploadUserData() {

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        assert firebaseUser != null;
        String userID = firebaseUser.getUid();

        Customers customs = new Customers(firstName_regCustom, lastName_regCustom, userName_regCustom, phoneNr_RegCustom, email_regCustom);
        databaseReference.child(userID).setValue(customs).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {

                firebaseUser.sendEmailVerification();

                LayoutInflater inflater = getLayoutInflater();
                @SuppressLint("InflateParams") View layout = inflater.inflate(R.layout.toast, null);
                TextView text = layout.findViewById(R.id.tvToast);
                ImageView imageView = layout.findViewById(R.id.imgToast);
                text.setText("Registration successful. Verification email sent!!");
                imageView.setImageResource(R.drawable.baseline_person_add_alt_1_24);
                Toast toast = new Toast(getApplicationContext());
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();

                Intent intent = new Intent(RegisterCustomer.this, Login.class);
                startActivity(intent);
                finish();
            } else {
                try {
                    throw Objects.requireNonNull(task.getException());
                } catch (Exception e) {
                    Toast.makeText(RegisterCustomer.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
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
            firstNameRegCustom.setError("First name can be empty");
            firstNameRegCustom.requestFocus();
        } else if (TextUtils.isEmpty(lastName_regCustom)) {
            lastNameRegCustom.setError("Last name cannot be empty");
            lastNameRegCustom.requestFocus();
        } else if (userName_regCustom.isEmpty()) {
            userNameRegCustom.setError("User name cannot be empty");
            userNameRegCustom.requestFocus();
        } else if (phoneNr_RegCustom.isEmpty()) {
            phoneNrRegCustom.setError("User Name cannot be empty");
            phoneNrRegCustom.requestFocus();
        } else if (email_regCustom.isEmpty()) {
            emailRegCustom.setError("Email address cannot be empty");
            emailRegCustom.requestFocus();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email_regCustom).matches()) {
            emailRegCustom.setError("Enter a valid email address");
            emailRegCustom.requestFocus();
        } else if (pass_regCustom.isEmpty()) {
            passRegCustom.setError("Password cannot be empty");
            passRegCustom.requestFocus();
        } else if (pass_regCustom.length() < 6) {
            passRegCustom.setError("Password too short, enter minimum 6 character long");
        } else if (confPass_regCustom.isEmpty()) {
            confPassRegCustom.setError("Confirm password cannot be empty");
            confPassRegCustom.requestFocus();
        } else if (!pass_regCustom.equals(confPass_regCustom)) {
            confPassRegCustom.setError("The Confirm Password does not match Password");
            confPassRegCustom.requestFocus();
        } else {
            result = true;
        }
        return result;
    }
}
