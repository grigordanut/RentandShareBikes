package com.example.rentandsharebikes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BikeImageMainShowBikes extends AppCompatActivity implements BikeAdapterMainShowBikes.OnItemClickListener {

    //Access Bikes table from database
    private DatabaseReference databaseRefMain;
    private FirebaseStorage bikesStorageMain;
    private ValueEventListener bikesEventListenerMain;

    private RecyclerView rVBikeImgMain_ShowBikes;

    private BikeAdapterMainShowBikes bikeAdapterMainShowBikes;

    private TextView tVBikeImgMainShowBikes;

    private List<Bikes> listMainBikes;

    String bikeStore_NameMain = "";
    String bikeStore_KeyMain = "";

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_image_main_show_bikes);

        Objects.requireNonNull(getSupportActionBar()).setTitle("MAIN: Bike available");

        progressDialog = new ProgressDialog(this);

        //initialize the bike storage database
        bikesStorageMain = FirebaseStorage.getInstance();
        databaseRefMain = FirebaseDatabase.getInstance().getReference("Bikes");

        getIntent().hasExtra("SNameMain");
        bikeStore_NameMain = Objects.requireNonNull(getIntent().getExtras()).getString("SNameMain");

        getIntent().hasExtra("SKeyMain");
        bikeStore_KeyMain = Objects.requireNonNull(getIntent().getExtras()).getString("SKeyMain");

        tVBikeImgMainShowBikes = findViewById(R.id.tvBikeImgMainShowBikes);

        rVBikeImgMain_ShowBikes = findViewById(R.id.rVBikeImgMainShowBikes);
        rVBikeImgMain_ShowBikes.setHasFixedSize(true);
        rVBikeImgMain_ShowBikes.setLayoutManager(new LinearLayoutManager(this));

        listMainBikes = new ArrayList<>();

        bikeAdapterMainShowBikes = new BikeAdapterMainShowBikes(BikeImageMainShowBikes.this, listMainBikes);
        rVBikeImgMain_ShowBikes.setAdapter(bikeAdapterMainShowBikes);
        bikeAdapterMainShowBikes.setOnItmClickListener(BikeImageMainShowBikes.this);
    }

    @Override
    public void onStart() {
        super.onStart();
        loadBikesListMainPage();
    }

    public void loadBikesListMainPage() {

        progressDialog.show();

        bikesEventListenerMain = databaseRefMain.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                listMainBikes.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Bikes bikes = postSnapshot.getValue(Bikes.class);
                    assert bikes != null;
                    bikes.setBike_Key(postSnapshot.getKey());

                    if (bikes.getBikeStoreKey().equals(bikeStore_KeyMain)) {
                        listMainBikes.add(bikes);
                    }
                }

                if (listMainBikes.size() == 1) {
                    tVBikeImgMainShowBikes.setText(listMainBikes.size() + " bike available in " + bikeStore_NameMain + " store");
                }
                else if (listMainBikes.size() > 1) {
                    tVBikeImgMainShowBikes.setText(listMainBikes.size() + " bikes available in " + bikeStore_NameMain + " store");
                }
                else {
                    tVBikeImgMainShowBikes.setText("No bikes available in " + bikeStore_NameMain + " store");
                }

                bikeAdapterMainShowBikes.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikeImageMainShowBikes.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        progressDialog.dismiss();
    }

    //Action of the menu onClick
    @Override
    public void onItemClick(int position) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BikeImageMainShowBikes.this);
        alertDialogBuilder
                .setMessage("Register and Log into your account to access the:\nRent and Share Bikes services.")
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, id) -> dialog.dismiss());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_bikes_main, menu);
        return true;
    }

    private void goBackBikesMain() {
        finish();
        startActivity(new Intent(BikeImageMainShowBikes.this, MainActivity.class));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.showBikesMainGoBack) {
            goBackBikesMain();
        }

        return super.onOptionsItemSelected(item);
    }
}
