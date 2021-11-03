package com.example.rentandsharebikes;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginCustomer extends AppCompatActivity {

    //declare variables
    private TextInputEditText emailLogCustom, passLogCustom;
    private CheckBox rememberCheckBox;

    private String email_logCustom, pass_logCustom;

    private TextView textViewEmailLogCustom, textViewPassLogCustom;

    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_customer);

        emailLogCustom = (TextInputEditText) findViewById(R.id.etEmailLogCustom);
        passLogCustom = (TextInputEditText) findViewById(R.id.etPassLogCustom);

        TextView tvForgotPass = (TextView) findViewById(R.id.tvForgotPassCustom);
        tvForgotPass.setOnClickListener(v -> {
            Intent forgotPass = new Intent(LoginCustomer.this, ResetPassword.class);
            startActivity(forgotPass);
        });

        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();

        rememberCheckBox = (CheckBox) findViewById(R.id.cbRemember);

        SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
        String checkbox = preferences.getString("remember", "");

        assert checkbox != null;
        if (checkbox.equals("true")) {
            Intent intent = new Intent(LoginCustomer.this, CustomerPageMain.class);
            startActivity(intent);
        } else if (checkbox.equals("false")) {
            Toast.makeText(LoginCustomer.this, "Please Sign In", Toast.LENGTH_SHORT).show();
        }

        rememberCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("remember", "true");
                    editor.apply();
                    Toast.makeText(LoginCustomer.this, "Checked", Toast.LENGTH_SHORT).show();
                } else if (!compoundButton.isChecked()) {
                    SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("remember", "false");
                    editor.apply();
                    Toast.makeText(LoginCustomer.this, "Unchecked", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button buttonSignUp = (Button) findViewById(R.id.btnSignUpCustom);
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginCustomer.this, RegisterCustomer.class));
            }
        });

        Button buttonLogInCustom = (Button) findViewById(R.id.btnLoginCustom);
        buttonLogInCustom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateUserLogData()) {
                    progressDialog.setMessage("Login Customer");
                    progressDialog.show();

                    firebaseAuth.signInWithEmailAndPassword(email_logCustom, pass_logCustom).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //clear data
                                emailLogCustom.setText("");
                                passLogCustom.setText("");
                                checkEmailVerification();
                            } else {
                                Toast.makeText(LoginCustomer.this, "Log in failed, you entered a wrong Email or Password", Toast.LENGTH_SHORT).show();
                            }
                            progressDialog.dismiss();
                        }
                    });
                }
            }
        });

        textViewEmailLogCustom = (TextView) findViewById(R.id.text_dummy_hint_emailLog);
        textViewPassLogCustom = (TextView) findViewById(R.id.text_dummy_hint_password);

        // Email Address
        emailLogCustom.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            // Show white background behind floating label
                            textViewEmailLogCustom.setVisibility(View.VISIBLE);
                        }
                    }, 10);
                } else {
                    // Required to show/hide white background behind floating label during focus change
                    if (Objects.requireNonNull(emailLogCustom.getText()).length() > 0)
                        textViewEmailLogCustom.setVisibility(View.VISIBLE);
                    else
                        textViewEmailLogCustom.setVisibility(View.INVISIBLE);
                }
            }
        });

        // Password
        passLogCustom.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Show white background behind floating label
                            textViewPassLogCustom.setVisibility(View.VISIBLE);
                        }
                    }, 10);
                } else {
                    // Required to show/hide white background behind floating label during focus change
                    if (passLogCustom.getText().length() > 0)
                        textViewPassLogCustom.setVisibility(View.VISIBLE);
                    else
                        textViewPassLogCustom.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private Boolean validateUserLogData() {
        boolean result = false;
        email_logCustom = Objects.requireNonNull(emailLogCustom.getText()).toString().trim();
        pass_logCustom = Objects.requireNonNull(passLogCustom.getText()).toString().trim();

        if (email_logCustom.isEmpty()) {
            emailLogCustom.setError("Enter your Login Email");
            emailLogCustom.requestFocus();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email_logCustom).matches()) {
            Toast.makeText(LoginCustomer.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            emailLogCustom.setError("Enter a valid Email Address");
            emailLogCustom.requestFocus();
        } else if (pass_logCustom.isEmpty()) {
            passLogCustom.setError("Enter your Login Password");
            passLogCustom.requestFocus();
        } else if (email_logCustom.equals("admin@gmail.com") && (pass_logCustom.equals("admin"))) {
            progressDialog.setMessage("Login Admin");
            progressDialog.show();
            startActivity(new Intent(LoginCustomer.this, AdminPage.class));
            emailLogCustom.setText("");
            passLogCustom.setText("");
            progressDialog.dismiss();
        } else {
            result = true;
        }

        return result;
    }

    //check if the email has been verified
    private void checkEmailVerification() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        boolean emailFlag = firebaseUser.isEmailVerified();

        if (emailFlag) {
            progressDialog.dismiss();
            finish();
            Toast.makeText(LoginCustomer.this, "Log In successful", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginCustomer.this, CustomerPageMain.class));
        } else {
            progressDialog.dismiss();
            alertDialogEmailUsed();
        }
    }

    private void alertDialogEmailUsed(){
        AlertDialog.Builder builderAlert = new AlertDialog.Builder(LoginCustomer.this);
        builderAlert.setMessage("Please verify and confirm your email address before you Log in");
        builderAlert.setCancelable(true);
        builderAlert.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        firebaseAuth.signOut();
                        finish();
                    }
                });

        AlertDialog alert1 = builderAlert.create();
        alert1.show();
    }
}
