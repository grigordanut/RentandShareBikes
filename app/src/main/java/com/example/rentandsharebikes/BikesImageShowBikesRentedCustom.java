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

public class BikesImageShowBikesRentedCustom extends AppCompatActivity implements BikesAdapterShowBikesRentedCustom.OnItemClickListener {

    private DatabaseReference databaseRefRemoveBike;
    private DatabaseReference databaseRefRestoreBike;
    private FirebaseStorage bikesStorageRemoveBike;
    private FirebaseStorage bikesStorageReturnBike;

    private ValueEventListener bikesEventListener;

    private RecyclerView bikesListRecyclerView;
    private BikesAdapterShowBikesRentedCustom bikesAdapterShowBikesRentedCustom;

    private TextView tVCustomerRentBikes;

    private List<RentBikes> rentBikesList;

    String customerFirst_Name = "";
    String customerLast_Name = "";
    String customer_Id = "";

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bikes_image_show_bikes_rented_customer);

        getIntent().hasExtra("CFName");
        customerFirst_Name = Objects.requireNonNull(getIntent().getExtras()).getString("CFName");

        getIntent().hasExtra("CLName");
        customerLast_Name = Objects.requireNonNull(getIntent().getExtras()).getString("CLName");

        getIntent().hasExtra("CId");
        customer_Id = Objects.requireNonNull(getIntent().getExtras()).getString("CId");

        tVCustomerRentBikes = (TextView) findViewById(R.id.tvCustomerRentBikes);
        tVCustomerRentBikes.setText("No Bikes rented by: " + customerFirst_Name + " " + customerLast_Name);

        bikesListRecyclerView = (RecyclerView) findViewById(R.id.evRecyclerView);
        bikesListRecyclerView.setHasFixedSize(true);
        bikesListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressDialog = new ProgressDialog(this);
        rentBikesList = new ArrayList<>();

        progressDialog.show();
    }

    //Action on bikes onClick
    @Override
    public void onItemClick(int position) {
        alertDialogShowRentedBikesOptions(position);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onStart() {
        super.onStart();
        loadBikesListCustomer();
    }

    private void loadBikesListCustomer() {
        //initialize the bike storage database
        bikesStorageRemoveBike = FirebaseStorage.getInstance();
        databaseRefRemoveBike = FirebaseDatabase.getInstance().getReference("Rent Bikes");
        bikesEventListener = databaseRefRemoveBike.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                rentBikesList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    RentBikes rent_Bikes = postSnapshot.getValue(RentBikes.class);

                    assert rent_Bikes != null;
                    if (rent_Bikes.getCustomerId_RentBikes().equals(customer_Id)) {
                        rent_Bikes.setBike_RentKey(postSnapshot.getKey());
                        rentBikesList.add(rent_Bikes);
                        tVCustomerRentBikes.setText(rentBikesList.size() + " bikes rented by "
                                + rent_Bikes.getfName_RentBikes() + " " + rent_Bikes.getlName_RentBikes());
                    }
                }
                bikesAdapterShowBikesRentedCustom = new BikesAdapterShowBikesRentedCustom(
                        BikesImageShowBikesRentedCustom.this, rentBikesList);
                bikesListRecyclerView.setAdapter(bikesAdapterShowBikesRentedCustom);
                bikesAdapterShowBikesRentedCustom.setOnItmClickListener(BikesImageShowBikesRentedCustom.this);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikesImageShowBikesRentedCustom.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void alertDialogShowRentedBikesOptions(final int position) {

        final String[] options = {"Return this Bike", "Back to Renting Page"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item,options);
        RentBikes sel_Bike = rentBikesList.get(position);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setCancelable(false)
                .setTitle("You selected " + sel_Bike.getBikeModel_RentBikes() + "\nSelect an option:")
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    RentBikes sel_Bike = rentBikesList.get(position);
                    Intent intent_Ret = new Intent ( BikesImageShowBikesRentedCustom.this, ReturnRentedBikes.class);
                    //Bike key of rented bike
                    intent_Ret.putExtra("BikeRentedKey",sel_Bike.getBike_RentKey());
                    Toast.makeText(BikesImageShowBikesRentedCustom.this, "Return the rented Bike", Toast.LENGTH_SHORT).show();
                    startActivity(intent_Ret);
                }
                if (which == 1) {
                    Toast.makeText(BikesImageShowBikesRentedCustom.this, "Go back to renting page", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(BikesImageShowBikesRentedCustom.this, CustomerPageRentBikes.class));
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
}
