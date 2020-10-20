package com.example.rentandsharebikes;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassword extends AppCompatActivity {

    //declare variables
    private EditText emailResetPass;
    private Button buttonResetPass;
    private FirebaseAuth firebaseAuth;

    private TextView textViewEmailResetPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        //initialize variables
        emailResetPass = (EditText) findViewById(R.id.etResetPass);
        buttonResetPass = (Button) findViewById(R.id.btnResetPassword);
        firebaseAuth = FirebaseAuth.getInstance();

        //Action of the button Reset password
        buttonResetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email_ResetPass = emailResetPass.getText().toString().trim();

                 //check the input fields
                if (TextUtils.isEmpty(email_ResetPass)){
                    emailResetPass.setError("The Email address can not be empty");
                }

                else if(!Patterns.EMAIL_ADDRESS.matcher(email_ResetPass).matches()){
                    emailResetPass.setError("Enter a valid Email Address");
                }

                //change the old password to a new password
                else{
                    firebaseAuth.sendPasswordResetEmail(email_ResetPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(ResetPassword.this, "The password reset email was sent",Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(new Intent(ResetPassword.this, LoginCustomer.class));
                        }
                        else{
                            Toast.makeText(ResetPassword.this, "Error in sending password reset email",Toast.LENGTH_SHORT).show();
                        }
                        }
                    });
                }
            }
        });

        textViewEmailResetPass = (TextView) findViewById(R.id.text_dummy_hint_emailResetPass);
        //Email Address
        emailResetPass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            // Show white background behind floating label
                            textViewEmailResetPass.setVisibility(View.VISIBLE);
                        }
                    }, 10);
                } else {
                    // Required to show/hide white background behind floating label during focus change
                    if (emailResetPass.getText().length() > 0)
                        textViewEmailResetPass.setVisibility(View.VISIBLE);
                    else
                        textViewEmailResetPass.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
}
