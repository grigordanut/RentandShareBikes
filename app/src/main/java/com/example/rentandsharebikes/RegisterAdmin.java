package com.example.rentandsharebikes;

import androidx.appcompat.app.AppCompatActivity;

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

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class RegisterAdmin extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseRefAdmin;

    private TextInputEditText firstNameRegAdmin;
    private TextInputEditText lastNameRegAdmin;
    private TextInputEditText userNameRegAdmin;
    private TextInputEditText phoneNrRegAdmin;
    private TextInputEditText emailRegAdmin;
    private TextInputEditText passRegAdmin;
    private TextInputEditText confPassRegAdmin;
    private String firstName_regAdmin, lastName_regAdmin, userName_regAdmin, phoneNr_regAdmin, email_regAdmin, pass_regAdmin, confPass_regAdmin;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_admin);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Admin registration");

        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseRefAdmin = FirebaseDatabase.getInstance().getReference().child("Admins");

        firstNameRegAdmin = findViewById(R.id.etFirstNameRegAdmin);
        lastNameRegAdmin = findViewById(R.id.etLastNameRegAdmin);
        userNameRegAdmin = findViewById(R.id.etUserNameRegAdmin);
        phoneNrRegAdmin = findViewById(R.id.etPhoneNrRegAdmin);
        emailRegAdmin = findViewById(R.id.etEmailRegAdmin);
        passRegAdmin = findViewById(R.id.etPassRegAdmin);
        confPassRegAdmin = findViewById(R.id.etConfPassRegAdmin);

        Button btn_SignInAdmin = findViewById(R.id.btnSignInRegAdmin);
        btn_SignInAdmin.setOnClickListener(v -> {
            Intent intentLog = new Intent(RegisterAdmin.this, Login.class);
            startActivity(intentLog);
        });

        Button btn_Register = findViewById(R.id.btnRegAdmin);
        btn_Register.setOnClickListener(view -> registerAdmin());
    }

    public void registerAdmin() {

        if (validateAdminRegData()) {

            progressDialog.setTitle("Registering admin details!!");
            progressDialog.show();

            firebaseAuth.createUserWithEmailAndPassword(email_regAdmin, pass_regAdmin).addOnCompleteListener(task -> {

                if (task.isSuccessful()) {

                    uploadAdminData();

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
    private void uploadAdminData() {

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        assert firebaseUser != null;
        String userID = firebaseUser.getUid();

        Admins admins_Data = new Admins(firstName_regAdmin, lastName_regAdmin, userName_regAdmin, phoneNr_regAdmin, email_regAdmin);
        databaseRefAdmin.child(userID).setValue(admins_Data).addOnCompleteListener(task -> {

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

                Intent intent = new Intent(RegisterAdmin.this, Login.class);
                startActivity(intent);
                finish();
            } else {
                try {
                    throw Objects.requireNonNull(task.getException());
                } catch (Exception e) {
                    Toast.makeText(RegisterAdmin.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private Boolean validateAdminRegData() {

        boolean result = false;

        firstName_regAdmin = Objects.requireNonNull(firstNameRegAdmin.getText()).toString().trim();
        lastName_regAdmin = Objects.requireNonNull(lastNameRegAdmin.getText()).toString().trim();
        userName_regAdmin = Objects.requireNonNull(userNameRegAdmin.getText()).toString().trim();
        phoneNr_regAdmin = Objects.requireNonNull(phoneNrRegAdmin.getText()).toString().trim();
        email_regAdmin = Objects.requireNonNull(emailRegAdmin.getText()).toString().trim();
        pass_regAdmin = Objects.requireNonNull(passRegAdmin.getText()).toString().trim();
        confPass_regAdmin = Objects.requireNonNull(confPassRegAdmin.getText()).toString().trim();

        if (TextUtils.isEmpty(firstName_regAdmin)) {
            firstNameRegAdmin.setError("First name can be empty");
            firstNameRegAdmin.requestFocus();
        } else if (TextUtils.isEmpty(lastName_regAdmin)) {
            lastNameRegAdmin.setError("Last name cannot be empty");
            lastNameRegAdmin.requestFocus();
        } else if (userName_regAdmin.isEmpty()) {
            userNameRegAdmin.setError("User name cannot be empty");
            userNameRegAdmin.requestFocus();
        } else if (phoneNr_regAdmin.isEmpty()) {
            phoneNrRegAdmin.setError("User Name cannot be empty");
            phoneNrRegAdmin.requestFocus();
        } else if (email_regAdmin.isEmpty()) {
            emailRegAdmin.setError("Email address cannot be empty");
            emailRegAdmin.requestFocus();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email_regAdmin).matches()) {
            emailRegAdmin.setError("Enter a valid email address");
            emailRegAdmin.requestFocus();
        } else if (pass_regAdmin.isEmpty()) {
            passRegAdmin.setError("Password cannot be empty");
            passRegAdmin.requestFocus();
        } else if (pass_regAdmin.length() < 6) {
            passRegAdmin.setError("Password too short, enter minimum 6 character long");
        } else if (confPass_regAdmin.isEmpty()) {
            confPassRegAdmin.setError("Confirm password cannot be empty");
            confPassRegAdmin.requestFocus();
        } else if (!pass_regAdmin.equals(confPass_regAdmin)) {
            confPassRegAdmin.setError("The Confirm Password does not match Password");
            confPassRegAdmin.requestFocus();
        } else {
            result = true;
        }
        return result;
    }
}