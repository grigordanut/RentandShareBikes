package com.example.rentandsharebikes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ContactUs extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        //Declare variable
        final EditText usrName   = findViewById(R.id.etNameContactUs);
        final EditText usrEmail  = findViewById(R.id.etEmailContactUs);
        final EditText usrObject = findViewById(R.id.etSubjectContactUs);
        final EditText usrMessage = findViewById(R.id.etMessageContactUs);

        Button post_Message = (Button) findViewById(R.id.post_message);
        post_Message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = usrName.getText().toString();
                String email = usrEmail.getText().toString();
                String subject = usrObject.getText().toString();
                String message = usrMessage.getText().toString();

                if (TextUtils.isEmpty(name)){
                    usrName.setError("Please Enter Your Name");
                    usrName.requestFocus();
                }

                else if (TextUtils.isEmpty(email)){
                    usrEmail.setError("Please Enter Your Email");
                    usrEmail.requestFocus();
                }

                else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(ContactUs.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                    usrEmail.setError("Enter a valid Email Address");
                    usrEmail.requestFocus();
                }

                else if (TextUtils.isEmpty(subject)){
                    usrObject.setError("Enter Your Subject");
                    usrObject.requestFocus();
                }

                else if (TextUtils.isEmpty(message)){
                    usrMessage.setError("Enter Your Message");
                    usrMessage.requestFocus();
                }
                else {
                    Intent sendEmail = new Intent(android.content.Intent.ACTION_SEND);

                    /* insert Data in email*/
                    sendEmail.setType("plain/text");
                    sendEmail.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"rent.bikes19@gmail.com"});
                    sendEmail.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
                    sendEmail.putExtra(android.content.Intent.EXTRA_TEXT,
                            "name:" + name + '\n' + "Email ID:" + email + '\n' + "Message:" + '\n' + message);

                    // Send message to the Activity
                    startActivity(Intent.createChooser(sendEmail, "Send mail..."));
                }

            }
        });
    }
}
