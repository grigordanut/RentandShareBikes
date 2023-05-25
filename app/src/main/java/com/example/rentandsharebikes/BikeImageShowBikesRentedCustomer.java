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

public class BikeImageShowBikesRentedCustomer extends AppCompatActivity implements BikeAdapterRentedBikesCustomer.OnItemClickListener {

    private DatabaseReference databaseRefRemoveBike;
    private DatabaseReference databaseRefRestoreBike;
    private FirebaseStorage bikesStorageRemoveBike;
    private FirebaseStorage bikesStorageReturnBike;

    private ValueEventListener bikesEventListener;

    private RecyclerView bikesListRecyclerView;
    private BikeAdapterRentedBikesCustomer bikeAdapterRentedBikesCustomer;

    private TextView tVCustomerRentBikes;

    private List<RentedBikes> rentedBikesList;

    String customerFirst_Name = "";
    String customerLast_Name = "";
    String customer_Id = "";

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_image_show_bikes_rented_customer);

        progressDialog = new ProgressDialog(this);
        progressDialog.show();

        getIntent().hasExtra("CFName");
        customerFirst_Name = Objects.requireNonNull(getIntent().getExtras()).getString("CFName");

        getIntent().hasExtra("CLName");
        customerLast_Name = Objects.requireNonNull(getIntent().getExtras()).getString("CLName");

        getIntent().hasExtra("CId");
        customer_Id = Objects.requireNonNull(getIntent().getExtras()).getString("CId");

        tVCustomerRentBikes = findViewById(R.id.tvCustomerRentBikes);
        tVCustomerRentBikes.setText("No Bikes rented by: " + customerFirst_Name + " " + customerLast_Name);

        bikesListRecyclerView = findViewById(R.id.evRecyclerView);
        bikesListRecyclerView.setHasFixedSize(true);
        bikesListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        rentedBikesList = new ArrayList<>();

        bikeAdapterRentedBikesCustomer = new BikeAdapterRentedBikesCustomer(BikeImageShowBikesRentedCustomer.this, rentedBikesList);
        bikesListRecyclerView.setAdapter(bikeAdapterRentedBikesCustomer);
        bikeAdapterRentedBikesCustomer.setOnItmClickListener(BikeImageShowBikesRentedCustomer.this);
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
            @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                rentedBikesList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    RentedBikes rent_Bikes = postSnapshot.getValue(RentedBikes.class);

                    assert rent_Bikes != null;
                    if (rent_Bikes.getCustomerId_RentBikes().equals(customer_Id)) {
                        rent_Bikes.setBike_RentKey(postSnapshot.getKey());
                        rentedBikesList.add(rent_Bikes);
                        tVCustomerRentBikes.setText(rentedBikesList.size() + " bikes rented by "
                                + rent_Bikes.getfName_RentBikes() + " " + rent_Bikes.getlName_RentBikes());
                    }
//                    else {
//                        tVCustomerRentBikes.setText("No Bikes rented by: " + customerFirst_Name + " " + customerLast_Name);
//                    }
                }

                bikeAdapterRentedBikesCustomer.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikeImageShowBikesRentedCustomer.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Action on bikes onClick
    @Override
    public void onItemClick(int position) {

        final String[] options = {"Return this Bike", "Back to Renting Page"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item,options);
        RentedBikes sel_Bike = rentedBikesList.get(position);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setCancelable(false)
                .setTitle("You selected: " + sel_Bike.getBikeModel_RentBikes() + "\nSelect an option:")
                .setAdapter(adapter, (dialog, id) -> {
                    if (id == 0) {
                        RentedBikes sel_Bike1 = rentedBikesList.get(position);
                        Intent intent_Ret = new Intent ( BikeImageShowBikesRentedCustomer.this, ReturnRentedBikes.class);
                        //Intent intent_Ret = new Intent ( BikeImageShowBikesRentedCustomer.this, ReturnRentedBikesSpinner.class);
                        //Bike key of rented bike
                        intent_Ret.putExtra("BikeRentedKey", sel_Bike1.getBike_RentKey());
                        Toast.makeText(BikeImageShowBikesRentedCustomer.this, "Return the rented Bike", Toast.LENGTH_SHORT).show();
                        startActivity(intent_Ret);
                    }
                    if (id == 1) {
                        Toast.makeText(BikeImageShowBikesRentedCustomer.this, "Go back to renting page", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(BikeImageShowBikesRentedCustomer.this, CustomerPageRentBikes.class));
                    }

                })

                .setNegativeButton("CLOSE", (dialog, id) -> dialog.dismiss());

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
