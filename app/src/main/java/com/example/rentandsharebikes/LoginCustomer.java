package com.example.rentandsharebikes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginCustomer extends AppCompatActivity {

    private TextInputEditText emailLogCustom;
    private TextInputEditText passLogCustom;
    private String email_logCustom, pass_logCustom;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_customer);

        emailLogCustom = (TextInputEditText) findViewById(R.id.etEmailLogCustom);
        passLogCustom = (TextInputEditText) findViewById(R.id.etPassLogCustom);

        TextView tvForgotPass = (TextView) findViewById(R.id.tvForgotPassCustom);
        tvForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent forgotPass = new Intent(LoginCustomer.this , ResetPassword.class);
                startActivity(forgotPass);
            }
        });

        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();

        Button buttonSignUp = (Button)findViewById(R.id.btnSignUpCustom);
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginCustomer.this, RegisterCustomer.class));
            }
        });

        Button buttonLogInCustom = (Button)findViewById(R.id.btnLoginCustom);
        buttonLogInCustom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email_logCustom = emailLogCustom.getText().toString().trim();
                pass_logCustom = passLogCustom.getText().toString().trim();

                if (email_logCustom.isEmpty()){
                    emailLogCustom.setError("Enter your Login Email");
                    emailLogCustom.requestFocus();
                }

                else if(!Patterns.EMAIL_ADDRESS.matcher(email_logCustom).matches()){
                    Toast.makeText(LoginCustomer.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                    emailLogCustom.setError("Enter a valid Email Address");
                    emailLogCustom.requestFocus();
                }

                else if(pass_logCustom.isEmpty()){
                    passLogCustom.setError("Enter your Login Password");
                    passLogCustom.requestFocus();
                }

                else if (email_logCustom.equals("admin@gmail.com") && (pass_logCustom.equals("admin"))){
                    progressDialog.setMessage("Login Admin");
                    progressDialog.show();
                    startActivity(new Intent(LoginCustomer.this, AdminPage.class));
                    emailLogCustom.setText("");
                    passLogCustom.setText("");
                    progressDialog.dismiss();
                }

                else{
                    progressDialog.setMessage("Login Customer");
                    progressDialog.show();

                    firebaseAuth.signInWithEmailAndPassword(email_logCustom,pass_logCustom).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            //clear data
                            emailLogCustom.setText("");
                            passLogCustom.setText("");
                            checkEmailVerification();
                        }

                        else{
                            progressDialog.dismiss();
                            Toast.makeText(LoginCustomer.this, "Log in failed, you entered a wrong Email or Password", Toast.LENGTH_SHORT).show();
                        }
                        }
                    });
                }
            }
        });
    }

    //check if the email has been verified
    private void checkEmailVerification(){
        FirebaseUser firebaseUser = firebaseAuth.getInstance().getCurrentUser();
        Boolean emailFlag = firebaseUser.isEmailVerified();

        if(emailFlag){
            progressDialog.dismiss();
            finish();
            Toast.makeText(LoginCustomer.this, "Log In successful", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginCustomer.this, CustomerPage.class));
        }

        else{
            progressDialog.dismiss();
            Toast.makeText(this, "Please verify your Email first", Toast.LENGTH_SHORT).show();
            firebaseAuth.signOut();
        }
    }
}
