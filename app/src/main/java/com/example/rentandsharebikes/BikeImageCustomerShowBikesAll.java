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

public class BikeImageCustomerShowBikesAll extends AppCompatActivity implements BikeAdapterBikesCustomer.OnItemClickListener {

    private FirebaseStorage bikesStorage;
    private DatabaseReference databaseReference;
    private ValueEventListener bikesEventListener;

    private RecyclerView rvBikeImgCustom_ShowBikesAll;
    private BikeAdapterBikesCustomer bikeAdapterBikesCustomer;

    private TextView tVBikeImgCustomShowBikesAll;

    private List<Bikes> listCustomerBikesAll;

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_image_customer_show_bikes_all);

        progressDialog = new ProgressDialog(this);

        //initialize the bike storage database
        bikesStorage = FirebaseStorage.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Bikes");


        tVBikeImgCustomShowBikesAll = findViewById(R.id.tvBikeImgCustomShowBikesAll);

        rvBikeImgCustom_ShowBikesAll = findViewById(R.id.rvBikeImgCustomShowBikesAll);
        rvBikeImgCustom_ShowBikesAll.setHasFixedSize(true);
        rvBikeImgCustom_ShowBikesAll.setLayoutManager(new LinearLayoutManager(this));

        listCustomerBikesAll = new ArrayList<>();

        bikeAdapterBikesCustomer = new BikeAdapterBikesCustomer(BikeImageCustomerShowBikesAll.this, listCustomerBikesAll);
        rvBikeImgCustom_ShowBikesAll.setAdapter(bikeAdapterBikesCustomer);
        bikeAdapterBikesCustomer.setOnItmClickListener(BikeImageCustomerShowBikesAll.this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onStart() {
        super.onStart();
        loadBikesListCustomer();
    }

    private void loadBikesListCustomer() {

        progressDialog.show();

        bikesEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                listCustomerBikesAll.clear();

                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Bikes bikes = postSnapshot.getValue(Bikes.class);
                        assert bikes != null;
                        bikes.setBike_Key(postSnapshot.getKey());
                        listCustomerBikesAll.add(bikes);
                    }

                    if (listCustomerBikesAll.size() == 1) {
                        tVBikeImgCustomShowBikesAll.setText(listCustomerBikesAll.size() + " bike available to rent");
                    } else {
                        tVBikeImgCustomShowBikesAll.setText(listCustomerBikesAll.size() + " bikes available to rent");
                    }

                    bikeAdapterBikesCustomer.notifyDataSetChanged();
                }

                else {
                    tVBikeImgCustomShowBikesAll.setText("No bikes available to rent!!");
                    bikeAdapterBikesCustomer.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikeImageCustomerShowBikesAll.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        progressDialog.dismiss();
    }

    //Action on bikes onClick
    @Override
    public void onItemClick(int position) {
        final String[] options = {"Rent this Bike", "Back Main Page"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, options);
        Bikes selected_Bike = listCustomerBikesAll.get(position);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setCancelable(false)
                .setTitle("You selected: " + selected_Bike.getBike_Model() + "\nSelect an option:")
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (which == 0) {
                            Intent intent = new Intent(BikeImageCustomerShowBikesAll.this, RentBikesCustomer.class);
                            Bikes selected_Bike = listCustomerBikesAll.get(position);
                            intent.putExtra("BCondition", selected_Bike.getBike_Condition());
                            intent.putExtra("BModel", selected_Bike.getBike_Model());
                            intent.putExtra("BManufact", selected_Bike.getBike_Manufacturer());
                            intent.putExtra("BImage", selected_Bike.getBike_Image());
                            intent.putExtra("BStoreName", selected_Bike.getBikeStoreName());
                            intent.putExtra("BPrice", String.valueOf(selected_Bike.getBike_Price()));
                            intent.putExtra("BStoreKey", selected_Bike.getBikeStoreKey());
                            intent.putExtra("BKey", selected_Bike.getBike_Key());
                            startActivity(intent);
                        }

                        if (which == 1) {
                            startActivity(new Intent(BikeImageCustomerShowBikesAll.this, CustomerPageRentBikes.class));


                        }
                    }
                })

                .setNegativeButton("CLOSE", (dialog, id) -> dialog.dismiss());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
