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

public class BikeImageShowBikesListMainPage extends AppCompatActivity implements BikeAdapterShowBikesListMainPage.OnItemClickListener {

    //Access Bikes table from database
    private DatabaseReference databaseRefMain;
    private FirebaseStorage bikesStorageMain;
    private ValueEventListener bikesEventListenerMain;

    private RecyclerView bikesListRecyclerView;

    private BikeAdapterShowBikesListMainPage bikeAdapterShowBikesListMainPage;

    private TextView textViewBikesImageMain;

    private List<Bikes> bikesListMain;

    String bikeStore_NameMain = "";
    String bikeStore_KeyMain = "";

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_image_show_bikes_list_main_page);

        progressDialog = new ProgressDialog(this);
        progressDialog.show();

        getIntent().hasExtra("SNameMain");
        bikeStore_NameMain = Objects.requireNonNull(getIntent().getExtras()).getString("SNameMain");

        getIntent().hasExtra("SKeyMain");
        bikeStore_KeyMain = Objects.requireNonNull(getIntent().getExtras()).getString("SKeyMain");

        textViewBikesImageMain = findViewById(R.id.tvBikeImageBikesListMainPage);
        //textViewBikesImageMain.setText("No bikes available in " + bikeStore_NameMain + " store");

        bikesListRecyclerView = findViewById(R.id.evRecyclerView);
        bikesListRecyclerView.setHasFixedSize(true);
        bikesListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        bikesListMain = new ArrayList<>();

        bikeAdapterShowBikesListMainPage = new BikeAdapterShowBikesListMainPage(BikeImageShowBikesListMainPage.this, bikesListMain);
        bikesListRecyclerView.setAdapter(bikeAdapterShowBikesListMainPage);
        bikeAdapterShowBikesListMainPage.setOnItmClickListener(BikeImageShowBikesListMainPage.this);
    }

    @Override
    public void onStart() {
        super.onStart();
        loadBikesListMainPage();
    }

    public void loadBikesListMainPage(){
        //initialize the bike storage database
        bikesStorageMain = FirebaseStorage.getInstance();
        databaseRefMain = FirebaseDatabase.getInstance().getReference("Bikes");

        bikesEventListenerMain = databaseRefMain.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bikesListMain.clear();
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Bikes bikes = postSnapshot.getValue(Bikes.class);
                    assert bikes != null;
                    if (bikes.getBikeStoreKey().equals(bikeStore_KeyMain)) {
                        bikes.setBike_Key(postSnapshot.getKey());
                        bikesListMain.add(bikes);
                    }
                }

                bikeAdapterShowBikesListMainPage.notifyDataSetChanged();

                if (bikesListMain.size() == 1) {
                    textViewBikesImageMain.setText(bikesListMain.size()+" bike available in " + bikeStore_NameMain+" store");
                }

                else {
                    textViewBikesImageMain.setText(bikesListMain.size()+" bikes available in " + bikeStore_NameMain+" store");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikeImageShowBikesListMainPage.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        progressDialog.dismiss();
    }

    //Action of the menu onClick
    @Override
    public void onItemClick(int position) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BikeImageShowBikesListMainPage.this);
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

    private void goBackBikesMain(){
        finish();
        startActivity(new Intent(BikeImageShowBikesListMainPage.this, MainActivity.class));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.showBikesMainGoBack) {
            goBackBikesMain();
        }

        return super.onOptionsItemSelected(item);
    }
}
