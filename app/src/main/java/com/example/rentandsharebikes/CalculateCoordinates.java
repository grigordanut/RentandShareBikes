package com.example.rentandsharebikes;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class CalculateCoordinates extends AppCompatActivity {

    private EditText etStorePlace;
    private String store_Place;

    private TextView tvStoreAddress, tvLatitude, tvLongitude;

    private Button btn_ShowCoordinates, btn_SaveCoordinates;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate_coordinates);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Calculate Coordinates");

        progressDialog = new ProgressDialog(this);

        etStorePlace = findViewById(R.id.etBikeStorePlace);
        tvStoreAddress = findViewById(R.id.tvShowStoreAddress);
        tvLatitude = findViewById(R.id.tvStoreLatitude);
        tvLongitude = findViewById(R.id.tvStoreLongitude);

        ImageButton btn_ClearStoreAddress = findViewById(R.id.btnClearStoreAddress);
        btn_ClearStoreAddress.setOnClickListener(v -> {
            etStorePlace.setText("");
            tvStoreAddress.setText("Store Address");
            tvLatitude.setText("");
            tvLongitude.setText("");
        });

        btn_ShowCoordinates = findViewById(R.id.btnShowCoordinates);
        btn_ShowCoordinates.setOnClickListener(v -> {

            store_Place = etStorePlace.getText().toString().trim();

            if (TextUtils.isEmpty(store_Place)) {
                etStorePlace.setError("Enter store Address");
                etStorePlace.requestFocus();
            } else {
                progressDialog.setMessage("Calculate Coordinates");
                progressDialog.show();
                GeoLocation.getAddress(store_Place, getApplicationContext(), new GeoHandler());
            }
            progressDialog.dismiss();
        });
    }

    @SuppressLint("HandlerLeak")
    private class GeoHandler extends Handler {
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(@NonNull Message msg) {
            final String storeAddress, addressStoreLat, addressStoreLong;
            if (msg.what == 1) {
                Bundle bundle = msg.getData();
                storeAddress = bundle.getString("locationAddress");
                addressStoreLat = bundle.getString("addressLat");
                addressStoreLong = bundle.getString("addressLong");
            } else {
                storeAddress = null;
                addressStoreLat = null;
                addressStoreLong = null;
            }
            tvStoreAddress.setText("Address: " + storeAddress);
            tvLatitude.setText(addressStoreLat);
            tvLongitude.setText(addressStoreLong);

            btn_SaveCoordinates = findViewById(R.id.btnSaveCoordinates);
            btn_SaveCoordinates.setOnClickListener(v -> {
                progressDialog.setMessage("Save Coordinates");
                progressDialog.show();
                etStorePlace.setText("");
                tvStoreAddress.setText("Store Address");
                tvLatitude.setText("");
                tvLongitude.setText("");

                progressDialog.dismiss();
                Toast.makeText(CalculateCoordinates.this,"The coordinates has been saved",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CalculateCoordinates.this, AddBikeStore.class);
                intent.putExtra("Address", storeAddress);
                intent.putExtra("Latitude", addressStoreLat);
                intent.putExtra("Longitude", addressStoreLong);
                startActivity(intent);
                finish();
            });
        }
    }
}
