package com.example.rentandsharebikes;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class Login extends AppCompatActivity {

    //declare variables
    private TextInputEditText emailLogUser, passLogUser;
    private CheckBox rememberCheckBox;

    private String email_logUser, pass_logUser;

    private FirebaseAuth firebaseAuth;

    private DatabaseReference databaseRefAdmin;
    private DatabaseReference databaseRefCustomer;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Log in customer");

        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseRefAdmin = FirebaseDatabase.getInstance().getReference("Admins");
        databaseRefCustomer = FirebaseDatabase.getInstance().getReference("Customers");

        emailLogUser = findViewById(R.id.etEmailLogUser);
        passLogUser = findViewById(R.id.etPassLogUser);

        TextView tvForgotPass = findViewById(R.id.tvForgotPassUser);
        tvForgotPass.setOnClickListener(v -> {
            Intent forgotPass = new Intent(Login.this, ResetPassword.class);
            startActivity(forgotPass);
        });

        rememberCheckBox = findViewById(R.id.cbRemember);

        SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
        String checkbox = preferences.getString("remember", "");

        if (checkbox.equals("true")) {
            Intent intent = new Intent(Login.this, CustomerPageMain.class);
            startActivity(intent);
        } else if (checkbox.equals("false")) {
            Toast.makeText(Login.this, "Please Sign In", Toast.LENGTH_SHORT).show();
        }

        rememberCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("remember", "true");
                    editor.apply();
                    Toast.makeText(Login.this, "Checked", Toast.LENGTH_SHORT).show();
                } else if (!compoundButton.isChecked()) {
                    SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("remember", "false");
                    editor.apply();
                    Toast.makeText(Login.this, "Unchecked", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button btn_SignUp = findViewById(R.id.btnSignUpUser);


        btn_SignUp.setOnClickListener(v -> {

            databaseRefAdmin.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        startActivity(new Intent(Login.this, RegisterCustomer.class));
                    } else {
                        startActivity(new Intent(Login.this, RegisterAdmin.class));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(Login.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });


        Button btn_LogInUser = findViewById(R.id.btnLoginUser);
        btn_LogInUser.setOnClickListener(v -> {

            if (validateUserLogData()) {

                progressDialog.setTitle("User login!!");
                progressDialog.show();

                firebaseAuth.signInWithEmailAndPassword(email_logUser, pass_logUser).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            checkEmailVerification();

                        } else {
                            try {
                                throw Objects.requireNonNull(task.getException());
                            } catch (FirebaseAuthInvalidUserException e) {
                                emailLogUser.setError("This email is not registered.");
                                emailLogUser.requestFocus();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                passLogUser.setError("Invalid password");
                                passLogUser.requestFocus();
                            } catch (Exception e) {
                                Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        progressDialog.dismiss();
                    }
                });
            }
        });
    }

    private Boolean validateUserLogData() {

        boolean result = false;

        email_logUser = Objects.requireNonNull(emailLogUser.getText()).toString().trim();
        pass_logUser = Objects.requireNonNull(passLogUser.getText()).toString().trim();

        if (email_logUser.isEmpty()) {
            emailLogUser.setError("Enter your login email");
            emailLogUser.requestFocus();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email_logUser).matches()) {
            emailLogUser.setError("Enter a valid email address");
            emailLogUser.requestFocus();
        } else if (pass_logUser.isEmpty()) {
            passLogUser.setError("Enter your login password");
            passLogUser.requestFocus();
        } else if (email_logUser.equals("admin@gmail.com") && (pass_logUser.equals("admin"))) {
            progressDialog.setMessage("Login Admin");
            progressDialog.show();
            startActivity(new Intent(Login.this, AdminPage.class));
            progressDialog.dismiss();
        } else {
            result = true;
        }

        return result;
    }

    //check if the email has been verified
    @SuppressLint("SetTextI18n")
    private void checkEmailVerification() {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;

        if (firebaseUser.isEmailVerified()) {

            checkUserAccount();

        } else {

            LayoutInflater inflater = getLayoutInflater();
            @SuppressLint("InflateParams") View layout = inflater.inflate(R.layout.toast, null);
            TextView text = layout.findViewById(R.id.tvToast);
            ImageView imageView = layout.findViewById(R.id.imgToast);
            text.setText("Please verify your email!!");
            imageView.setImageResource(R.drawable.ic_baseline_mark_email_unread_24);
            Toast toast = new Toast(getApplicationContext());
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();
        }

        progressDialog.dismiss();
    }

    public void checkUserAccount() {

        //Check if the user Admin try to log in
        final String admin_emailCheck = Objects.requireNonNull(emailLogUser.getText()).toString().trim();

        databaseRefAdmin.orderByChild("email_Admin").equalTo(admin_emailCheck)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {

                            progressDialog.dismiss();

                            LayoutInflater inflater = getLayoutInflater();
                            @SuppressLint("InflateParams") View layout = inflater.inflate(R.layout.toast, null);
                            TextView text = layout.findViewById(R.id.tvToast);
                            ImageView imageView = layout.findViewById(R.id.imgToast);
                            text.setText("Admin login successful!!");
                            imageView.setImageResource(R.drawable.ic_baseline_login_24);
                            Toast toast = new Toast(getApplicationContext());
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            startActivity(new Intent(Login.this, AdminPage.class));
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(Login.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        //Check if the user Customer try to log in
        final String custom_emailCheck = Objects.requireNonNull(emailLogUser.getText()).toString().trim();

        databaseRefCustomer.orderByChild("email_Customer").equalTo(custom_emailCheck)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {

                            progressDialog.dismiss();

                            LayoutInflater inflater = getLayoutInflater();
                            @SuppressLint("InflateParams") View layout = inflater.inflate(R.layout.toast, null);
                            TextView text = layout.findViewById(R.id.tvToast);
                            ImageView imageView = layout.findViewById(R.id.imgToast);
                            text.setText("Customer login successful!!");
                            imageView.setImageResource(R.drawable.ic_baseline_login_24);
                            Toast toast = new Toast(getApplicationContext());
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            startActivity(new Intent(Login.this, CustomerPageMain.class));
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(Login.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
