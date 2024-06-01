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

public class BikeImageCustomerShowBikesRented extends AppCompatActivity implements BikeAdapterCustomerShowBikesRented.OnItemClickListener {

    private DatabaseReference databaseRefRemoveBike;
    private DatabaseReference databaseRefRestoreBike;
    private FirebaseStorage bikesStorageRemoveBike;
    private FirebaseStorage bikesStorageReturnBike;

    private ValueEventListener bikesEventListener;

    private RecyclerView rvBikesImgCustom_ShowBikesRented;
    private BikeAdapterCustomerShowBikesRented bikeAdapterCustomerShowBikesRented;

    private TextView tVBikesImgCustomShowBikesRented;

    private List<RentedBikes> listCustomerBikesRented;

    String customerFirst_Name = "";
    String customerLast_Name = "";
    String customer_Id = "";

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_image_customer_show_bikes_rented);

        progressDialog = new ProgressDialog(this);

        //initialize the bike storage database
        bikesStorageRemoveBike = FirebaseStorage.getInstance();
        databaseRefRemoveBike = FirebaseDatabase.getInstance().getReference("Rent Bikes");

        getIntent().hasExtra("CFName");
        customerFirst_Name = Objects.requireNonNull(getIntent().getExtras()).getString("CFName");

        getIntent().hasExtra("CLName");
        customerLast_Name = Objects.requireNonNull(getIntent().getExtras()).getString("CLName");

        getIntent().hasExtra("CId");
        customer_Id = Objects.requireNonNull(getIntent().getExtras()).getString("CId");

        Objects.requireNonNull(getSupportActionBar()).setTitle("Bikes rented by: " + customerFirst_Name + " " + customerLast_Name);

        tVBikesImgCustomShowBikesRented = findViewById(R.id.tvBikesImgCustomShowBikesRented);

        rvBikesImgCustom_ShowBikesRented = findViewById(R.id.rvBikesImgCustomShowBikesRented);
        rvBikesImgCustom_ShowBikesRented.setHasFixedSize(true);
        rvBikesImgCustom_ShowBikesRented.setLayoutManager(new LinearLayoutManager(this));

        listCustomerBikesRented = new ArrayList<>();

        bikeAdapterCustomerShowBikesRented = new BikeAdapterCustomerShowBikesRented(BikeImageCustomerShowBikesRented.this, listCustomerBikesRented);
        rvBikesImgCustom_ShowBikesRented.setAdapter(bikeAdapterCustomerShowBikesRented);
        bikeAdapterCustomerShowBikesRented.setOnItmClickListener(BikeImageCustomerShowBikesRented.this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onStart() {
        super.onStart();
        loadBikesListCustomer();
    }

    private void loadBikesListCustomer() {

        progressDialog.show();

        bikesEventListener = databaseRefRemoveBike.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                listCustomerBikesRented.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    RentedBikes rent_Bikes = postSnapshot.getValue(RentedBikes.class);
                    assert rent_Bikes != null;
                    rent_Bikes.setBike_RentKey(postSnapshot.getKey());

                    if (rent_Bikes.getCustomerId_RentBikes().equals(customer_Id)) {
                        listCustomerBikesRented.add(rent_Bikes);
                    }
                }

                if (listCustomerBikesRented.size() == 1) {
                    tVBikesImgCustomShowBikesRented.setText(listCustomerBikesRented.size() + " bike rented by: "
                            + customerFirst_Name + " " +  customerLast_Name);
                }
                else if (listCustomerBikesRented.size() > 1) {
                    tVBikesImgCustomShowBikesRented.setText(listCustomerBikesRented.size() + " bikes rented by: "
                            + customerFirst_Name + " " +  customerLast_Name);
                }
                else {
                    tVBikesImgCustomShowBikesRented.setText("No Bikes rented by: " + customerFirst_Name + " " + customerLast_Name);
                }

                bikeAdapterCustomerShowBikesRented.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikeImageCustomerShowBikesRented.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        progressDialog.dismiss();
    }

    //Action on bikes onClick
    @Override
    public void onItemClick(int position) {

        final String[] options = {"Return this Bike", "Back to Renting Page"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item,options);
        RentedBikes sel_Bike = listCustomerBikesRented.get(position);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setCancelable(false)
                .setTitle("You selected: " + sel_Bike.getBikeModel_RentBikes() + "\nSelect an option:")
                .setAdapter(adapter, (dialog, id) -> {
                    if (id == 0) {
                        RentedBikes sel_Bike1 = listCustomerBikesRented.get(position);
                        Intent intent_Ret = new Intent ( BikeImageCustomerShowBikesRented.this, ReturnRentedBikes.class);
                        //Intent intent_Ret = new Intent ( BikeImageCustomerShowBikesRented.this, ReturnRentedBikesSpinner.class);
                        //Bike key of rented bike
                        intent_Ret.putExtra("BikeRentedKey", sel_Bike1.getBike_RentKey());
                        Toast.makeText(BikeImageCustomerShowBikesRented.this, "Return the rented Bike", Toast.LENGTH_SHORT).show();
                        startActivity(intent_Ret);
                    }
                    if (id == 1) {
                        Toast.makeText(BikeImageCustomerShowBikesRented.this, "Go back to renting page", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(BikeImageCustomerShowBikesRented.this, CustomerPageRentBikes.class));
                    }

                })

                .setNegativeButton("CLOSE", (dialog, id) -> dialog.dismiss());

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
