package com.example.rentandsharebikes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
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

public class BikeImageCustomerShowBikes extends AppCompatActivity implements BikeAdapterBikesCustomer.OnItemClickListener {

    private FirebaseStorage bikesStorage;
    private DatabaseReference dbRefBikes;
    private ValueEventListener bikesEventListener;

    private RecyclerView rvBikeImgImgCustom_ShowBikes;
    private BikeAdapterBikesCustomer bikeAdapterBikesCustomer;

    private TextView tVBikeImgImgCustomShowBikes;

    private List<Bikes> listCustomerBikes;

    String bikeStore_NameRent = "";
    String bikeStore_KeyRent = "";

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_image_customer_show_bikes);

        progressDialog = new ProgressDialog(this);

        //initialize the bike storage database
        bikesStorage = FirebaseStorage.getInstance();
        dbRefBikes = FirebaseDatabase.getInstance().getReference().child("Bikes");

        getIntent().hasExtra("SNameRent");
        bikeStore_NameRent = Objects.requireNonNull(getIntent().getExtras()).getString("SNameRent");

        getIntent().hasExtra("SKeyRent");
        bikeStore_KeyRent = Objects.requireNonNull(getIntent().getExtras()).getString("SKeyRent");

        tVBikeImgImgCustomShowBikes = findViewById(R.id.tvBikeImgImgCustomShowBikes);

        rvBikeImgImgCustom_ShowBikes = findViewById(R.id.rvBikeImgCustomShowBikes);
        rvBikeImgImgCustom_ShowBikes.setHasFixedSize(true);
        rvBikeImgImgCustom_ShowBikes.setLayoutManager(new LinearLayoutManager(this));

        listCustomerBikes = new ArrayList<>();

        bikeAdapterBikesCustomer = new BikeAdapterBikesCustomer(BikeImageCustomerShowBikes.this, listCustomerBikes);
        rvBikeImgImgCustom_ShowBikes.setAdapter(bikeAdapterBikesCustomer);
        bikeAdapterBikesCustomer.setOnItmClickListener(BikeImageCustomerShowBikes.this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onStart() {
        super.onStart();
        loadBikesListCustomer();
    }

    private void loadBikesListCustomer() {

        progressDialog.show();

        bikesEventListener = dbRefBikes.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                listCustomerBikes.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Bikes bikes = postSnapshot.getValue(Bikes.class);
                    assert bikes != null;
                    bikes.setBike_Key(postSnapshot.getKey());
                    if (bikes.getBikeStoreKey().equals(bikeStore_KeyRent)) {
                        listCustomerBikes.add(bikes);
                    }
                }

                if (listCustomerBikes.size() == 1) {
                    tVBikeImgImgCustomShowBikes.setText(listCustomerBikes.size() + " bike available in " + bikeStore_NameRent + " store");
                }
                else if (listCustomerBikes.size() > 1) {
                    tVBikeImgImgCustomShowBikes.setText(listCustomerBikes.size() + " bikes available in " + bikeStore_NameRent + " store");
                }
                else {
                    tVBikeImgImgCustomShowBikes.setText("No bikes available in " + bikeStore_NameRent + " store");
                }

                bikeAdapterBikesCustomer.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikeImageCustomerShowBikes.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        progressDialog.dismiss();
    }

    //Action on bikes onClick
    @Override
    public void onItemClick(final int position) {
        final String[] options = {"Rent this Bike", "Back Main Page"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, options);
        Bikes selected_Bike = listCustomerBikes.get(position);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setCancelable(false)
                .setTitle("You selected " + selected_Bike.getBike_Model() + "\nSelect an option:")
                .setAdapter(adapter, (dialog, id) -> {

                    if (id == 0) {
                        Intent intent = new Intent(BikeImageCustomerShowBikes.this, RentBikesCustomer.class);
                        Bikes selected_Bike1 = listCustomerBikes.get(position);
                        intent.putExtra("BCondition", selected_Bike1.getBike_Condition());
                        intent.putExtra("BModel", selected_Bike1.getBike_Model());
                        intent.putExtra("BManufact", selected_Bike1.getBike_Manufacturer());
                        intent.putExtra("BImage", selected_Bike1.getBike_Image());
                        intent.putExtra("BStoreName", selected_Bike1.getBikeStoreName());
                        intent.putExtra("BStoreKey", selected_Bike1.getBikeStoreKey());
                        intent.putExtra("BPrice", String.valueOf(selected_Bike1.getBike_Price()));
                        intent.putExtra("BKey", selected_Bike1.getBike_Key());
                        startActivity(intent);
                    }

                    if (id == 1) {
                        startActivity(new Intent(BikeImageCustomerShowBikes.this, CustomerPageRentBikes.class));
                        Toast.makeText(BikeImageCustomerShowBikes.this, "Back to main page", Toast.LENGTH_SHORT).show();
                    }
                })

                .setNegativeButton("CLOSE",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_bikes_custom, menu);
        return true;
    }

    private void goBackBikesCustom() {
        finish();
        startActivity(new Intent(BikeImageCustomerShowBikes.this, CustomerPageRentBikes.class));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.showBikesCustomGoBack) {
            goBackBikesCustom();
        }

        return super.onOptionsItemSelected(item);
    }
}
