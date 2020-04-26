package com.example.rentandsharebikes;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class ChangePassword extends AppCompatActivity {

    private TextInputEditText oldPassword, newPassword;
    private String old_Password, new_Password;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        progressDialog = new ProgressDialog(ChangePassword.this);

        oldPassword = findViewById(R.id.etOldPassCustom);
        newPassword = findViewById(R.id.etNewPassCustom);

        Button buttonUpdatePassword = (Button) findViewById(R.id.btnUpdatePassword);
        buttonUpdatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                old_Password = oldPassword.getText().toString().trim();
                new_Password = newPassword.getText().toString().trim();

                if (old_Password.isEmpty()){
                    oldPassword.setError("Enter your old Password");
                    oldPassword.requestFocus();
                }

                else if(new_Password.isEmpty()){
                    newPassword.setError("Enter your new Password");
                    newPassword.requestFocus();
                }

                else{
                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    assert user != null;
                    AuthCredential credential = EmailAuthProvider.getCredential(Objects.requireNonNull(user.getEmail()),old_Password);
                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                user.updatePassword(new_Password).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){

                                            oldPassword.getText().clear();
                                            newPassword.getText().clear();

                                            progressDialog.dismiss();
                                            finish();
                                            Toast.makeText(ChangePassword.this, "Your password was updated", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(ChangePassword.this, LoginCustomer.class));

                                        }
                                        else{
                                            Toast.makeText(ChangePassword.this, "Password update failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                            else{
                                Toast.makeText(ChangePassword.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                            }
                        progressDialog.dismiss();
                        }
                    });
                }
            }
        });

//        Button buttonUpdatePassword = (Button) findViewById(R.id.btnUpdatePassword);
//        buttonUpdatePassword.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                firebaseUser.updatePassword(new_Password).addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isComplete()) {
//                            Toast.makeText(ChangePassword.this, "Your password was updated", Toast.LENGTH_LONG).show();
//                            finish();
//                        } else {
//                            Toast.makeText(ChangePassword.this, "Password update failed", Toast.LENGTH_LONG).show();
//                        }
//
//                    }
//                });
//
//            }
//        });
    }
}
