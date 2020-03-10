package com.example.rentandsharebikes;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

public class UpdateBikeStoreDetails extends AppCompatActivity {

    private String store_Number, store_Location, store_Address, store_NrSlots;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_bike_store_details);

        getIntent().hasExtra("SNumber");
        store_Number = String.valueOf((getIntent().getExtras()).getInt("SNumber"));
        TextView storeNumber = (TextView) findViewById(R.id.tvStoreNumber);
        storeNumber.setText(String.valueOf(store_Number));

        getIntent().hasExtra("SLocation");
        store_Location = (getIntent().getExtras()).getString("SLocation");
        TextInputEditText storeLocation = (TextInputEditText)findViewById(R.id.etStoreLocation);
        storeLocation.setText(store_Location);

        getIntent().hasExtra("SAddress");
        store_Address = (getIntent().getExtras()).getString("SAddress");
        TextInputEditText storeAddress = (TextInputEditText)findViewById(R.id.etStoreAddress);
        storeAddress.setText(store_Address);

        getIntent().hasExtra("SNrSlots");
        store_NrSlots = String.valueOf((getIntent().getExtras()).getInt("SNrSlots"));
        TextInputEditText storeNrSlots = (TextInputEditText)findViewById(R.id.etStoreNrSlots);
        storeNrSlots.setText(String.valueOf(store_NrSlots));


    }
}
