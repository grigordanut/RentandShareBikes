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
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BikesImageShowBikesRentedCustom extends AppCompatActivity implements BikesAdapterShowBikesRentedCustomer.OnItemClickListener {

    private DatabaseReference databaseRefRemoveBike;
    private DatabaseReference databaseRefRestoreBike;
    private FirebaseStorage bikesStorageRemoveBike;
    private FirebaseStorage bikesStorageReturnBike;

    private ValueEventListener bikesEventListener;

    private RecyclerView bikesListRecyclerView;
    private BikesAdapterShowBikesRentedCustomer bikesAdapterShowBikesRentedCustomer;

    private TextView tVCustomerRentBikes;

    private List<RentBikes> rentBikesList;

    String customerFirst_Name = "";
    String customerLast_Name = "";
    String customer_Id = "";

    private ProgressDialog progressDialog;

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
        tVCustomerRentBikes.setText("Bikes rented by: "+customerFirst_Name+" "+customerLast_Name);

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
        alertDialogStoreLocation(position);
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
                        //textViewBikesImageList.setText(bikesList.size()+" bikes available in "+bikeStore_Name+" store");
                    }
                }
                bikesAdapterShowBikesRentedCustomer = new BikesAdapterShowBikesRentedCustomer(BikesImageShowBikesRentedCustom.this, rentBikesList);
                bikesListRecyclerView.setAdapter(bikesAdapterShowBikesRentedCustomer);
                bikesAdapterShowBikesRentedCustomer.setOnItmClickListener(BikesImageShowBikesRentedCustom.this);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikesImageShowBikesRentedCustom.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void alertDialogStoreLocation(final int position) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        final String [] options = {"Delete this bike", "Return this Bike"};
        alertDialogBuilder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0){
                    alertDialogBuilder.setMessage("Are sure to delete this Bike?");
                    alertDialogBuilder.setCancelable(true);
                    alertDialogBuilder.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {



                                    RentBikes selected_BikeReturn = rentBikesList.get(position);
                                    final String selBikeReturn = selected_BikeReturn.getBike_RentKey();
                                    StorageReference imageRefReturn = bikesStorageReturnBike.getReferenceFromUrl(selected_BikeReturn.getBikeImage_RentBike());
                                    imageRefReturn.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            databaseRefRestoreBike.child("Bikes").setValue(selBikeReturn);
                                            Toast.makeText(BikesImageShowBikesRentedCustom.this, "The Bike has been deleted successfully ", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    RentBikes selected_Bike = rentBikesList.get(position);
                                    final String selectedKeyBike = selected_Bike.getBike_RentKey();
                                    StorageReference imageReference = bikesStorageRemoveBike.getReferenceFromUrl(selected_Bike.getBikeImage_RentBike());
                                    imageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            databaseRefRemoveBike.child(selectedKeyBike).removeValue();
                                            Toast.makeText(BikesImageShowBikesRentedCustom.this, "The Bike has been deleted successfully ", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                            });


                    alertDialogBuilder.setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert1 = alertDialogBuilder.create();
                    alert1.show();
                }

                if (which == 1){
                    alertDialogBuilder.setMessage("Are sure to return this Bike?");
                    alertDialogBuilder.setCancelable(true);
                    alertDialogBuilder.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    RentBikes selected_Bike = rentBikesList.get(position);
                                    Intent intent = new Intent (BikesImageShowBikesRentedCustom.this, ReturnRentedBikes.class);
                                    intent.putExtra("BStoreSame",selected_Bike.getStoreLocation_RentBikes());
                                    intent.putExtra("BKey",selected_Bike.getBike_RentKey());
                                    startActivity(intent);
                                }
                            });

                    alertDialogBuilder.setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert1 = alertDialogBuilder.create();
                    alert1.show();
                }

            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void alertDialogReturnDateEmpty(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("The returning day cannot be empty.");
        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        //selectReturnDate();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
