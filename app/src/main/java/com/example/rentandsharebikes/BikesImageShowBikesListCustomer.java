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

public class BikesImageShowBikesListCustomer extends AppCompatActivity implements BikesAdapterShowBikesListCustomer.OnItemClickListener {

    private FirebaseStorage bikesStorage;
    private DatabaseReference databaseReference;
    private ValueEventListener bikesEventListener;

    private RecyclerView bikesListRecyclerView;
    private BikesAdapterShowBikesListCustomer bikesAdapterShowBikesListCustomer;

    private TextView textViewBikesImageList;

    private List<BikesRent> bikesRentList;

    String bikeStore_NameRent = "";
    String bikeStore_KeyRent = "";

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bikes_image_show_bikes_list_customer);

        getIntent().hasExtra("SNameRent");
        bikeStore_NameRent = Objects.requireNonNull(getIntent().getExtras()).getString("SNameRent");

        getIntent().hasExtra("SKeyRent");
        bikeStore_KeyRent = Objects.requireNonNull(getIntent().getExtras()).getString("SKeyRent");

        textViewBikesImageList = (TextView) findViewById(R.id.tvBikeImageList);
        textViewBikesImageList.setText("No bikes available in " +bikeStore_NameRent+ " store");

        bikesListRecyclerView = (RecyclerView) findViewById(R.id.evRecyclerView);
        bikesListRecyclerView.setHasFixedSize(true);
        bikesListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressDialog = new ProgressDialog(this);
        bikesRentList = new ArrayList<>();

        progressDialog.show();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onStart() {
        super.onStart();
        loadBikesListCustomer();
    }

    private void loadBikesListCustomer() {
        //initialize the bike storage database
        bikesStorage = FirebaseStorage.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Bikes");

        bikesEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bikesRentList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    BikesRent bikesRent = postSnapshot.getValue(BikesRent.class);
                    assert bikesRent != null;
                    if (bikesRent.getBikeStoreKey().equals(bikeStore_KeyRent)) {
                        bikesRent.setBike_Key(postSnapshot.getKey());
                        bikesRentList.add(bikesRent);
                        textViewBikesImageList.setText(bikesRentList.size()+" bikes available in "+bikeStore_NameRent+" store");
                    }
                }
                bikesAdapterShowBikesListCustomer = new BikesAdapterShowBikesListCustomer(BikesImageShowBikesListCustomer.this, bikesRentList);
                bikesListRecyclerView.setAdapter(bikesAdapterShowBikesListCustomer);
                bikesAdapterShowBikesListCustomer.setOnItmClickListener(BikesImageShowBikesListCustomer.this);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikesImageShowBikesListCustomer.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Action on bikes onClick
    @Override
    public void onItemClick(final int position) {
        final String[] options = {"Rent this Bike", "Back Main Page"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, options);
        BikesRent selected_Bike = bikesRentList.get(position);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setCancelable(false)
                .setTitle("You selected "+selected_Bike.getBike_Model()+"\nSelect an option:")
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (which == 0) {
                    Intent intent = new Intent(BikesImageShowBikesListCustomer.this, RentBikesCustomer.class);
                    BikesRent selected_Bike = bikesRentList.get(position);
                    intent.putExtra("BCondition",selected_Bike.getBike_Condition());
                    intent.putExtra("BModel",selected_Bike.getBike_Model());
                    intent.putExtra("BManufact",selected_Bike.getBike_Manufacturer());
                    intent.putExtra("BImage",selected_Bike.getBike_Image());
                    intent.putExtra("BStoreName",selected_Bike.getBikeStoreName());
                    intent.putExtra("BStoreKey",selected_Bike.getBikeStoreKey());
                    intent.putExtra("BPrice",String.valueOf(selected_Bike.getBike_Price()));
                    intent.putExtra("BKey",selected_Bike.getBike_Key());
                    startActivity(intent);
                }

                if (which == 1) {
                    startActivity(new Intent(BikesImageShowBikesListCustomer.this, CustomerPageRentBikes.class));
                    Toast.makeText(BikesImageShowBikesListCustomer.this, "Back to main page", Toast.LENGTH_SHORT).show();
                }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_bikes_custom, menu);
        return true;
    }

    private void goBackBikesCustom(){
        finish();
        startActivity(new Intent(BikesImageShowBikesListCustomer.this, CustomerPageRentBikes.class));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.showBikesCustomGoBack) {
            goBackBikesCustom();
        }

        return super.onOptionsItemSelected(item);
    }
}
