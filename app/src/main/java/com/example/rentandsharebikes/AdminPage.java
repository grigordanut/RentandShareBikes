package com.example.rentandsharebikes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AdminPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_page);

        Button buttonAddBikeStore = (Button)findViewById(R.id.btnAddBikeStore);
        buttonAddBikeStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminPage.this, CalculateCoordinates.class));
            }
        });

        Button buttonShowBikeStores = (Button)findViewById(R.id.btnShowBikesStore);
        buttonShowBikeStores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminPage.this, BikeStoreImageShowStoresListAdmin.class));
            }
        });

        Button buttonAddBikesToStore = (Button)findViewById(R.id.btnAddBikesToStore);
        buttonAddBikesToStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminPage.this, BikeStoreImageAddBikesAdmin.class));
            }
        });

        Button buttonShowBikesList = (Button)findViewById(R.id.btnShowBikesList);
        buttonShowBikesList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminPage.this, BikeStoreImageShowBikesListAdmin.class));
            }
        });
    }
}
