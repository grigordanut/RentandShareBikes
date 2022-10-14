package com.example.rentandsharebikes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

public class ReturnRentedBikes extends AppCompatActivity {

    //Display data from Bike Stores database
    private DatabaseReference databaseRefBikeStores;

    private ArrayList<String> bikeStoreListReturnBike;
    private ArrayAdapter<String> arrayAdapter;

    //Display data from Rent BikesRent database
    private FirebaseStorage firebaseStShowRentedBikes;
    private DatabaseReference databaseRefShowRentedBikes;
    private ValueEventListener showRentedBikesEventListener;

    //Remove bikes from Rent BikesRent database
    private DatabaseReference databaseRefRemoveRentBikes;

    //Return bikes to BikesRent database
    private StorageReference storageRefReturnBikes;
    DatabaseReference databaseRefReturnBikes;

    //Access database Customers table
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseRefCustomer;

    private StorageTask bikesReturnTask;

    private TextInputEditText etFNameReturnBikes, etLNameReturnBikes;
    private TextView tVReturnBikes, tVStoreNameReturnBikes, tVCondReturnBikes, tVModelReturnBikes, tVManufactReturnBikes, tVPriceReturnBikes;
    private CheckBox cBoxRetSameStore, cBoxRetDiffStore;
    private EditText eTDateOfRentBike, eTDateReturnBike, eTRentDurationBike, eTReturnTotalHours, eTReturnTotalPricePay, etBikeStoreReturn;

    //variables for data received
    private String storeName_ReturnBikes, tVCond_ReturnBikes, tVModel_ReturnBikes, tVManufact_ReturnBikes;

    private Double tVPrice_ReturnBikes, eTReturn_TotalPricePay;
    private Double totalHours = 0.00;

    private ImageView ivReturnBikes;

    //Receive Bike image
    String img_ReturnBikes;

    //Receive the Bike Store Key of rented bike from BikesAdapterReturnBikesRented (Different than rented)
    String bike_StoreKeyRentedBikesDiff = "";

    //Receive Bike Store name of rented bike from BikesAdapterReturnBikesRented (Same store as rented)
    String bike_StoreNameRentedBikesSame = "";

    //Receive Bike Store key of rented bikes from BikesAdapterReturnBikesRented
    String bike_StoreKeyRentedBikesSame = "";

    String bike_CusIdRentedBikes = "";
    String bikeRented_Key = "";
    String bikeKey_ReturnBike = "";
    String bikeStoreKey_ReturnBike = "";

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_rented_bikes);

        progressDialog = new ProgressDialog(ReturnRentedBikes.this);

        firebaseAuth = FirebaseAuth.getInstance();

        //initialise variables
        tVReturnBikes = findViewById(R.id.tvReturnBikes);

        //Date of Rent
        eTDateOfRentBike = findViewById(R.id.etDateOfRentBike);
        eTDateOfRentBike.setEnabled(false);

        //Date of Return
        eTDateReturnBike = findViewById(R.id.etDateReturnBike);
        eTDateReturnBike.setEnabled(false);

        //Duration Time
        eTRentDurationBike = findViewById(R.id.etRentDurationBike);
        eTRentDurationBike.setEnabled(false);

        //Total Hours
        eTReturnTotalHours = (EditText) findViewById(R.id.etReturnTotalHours);
        eTReturnTotalHours.setEnabled(false);

        eTReturnTotalPricePay = (EditText) findViewById(R.id.etReturnTotalPricePay);
        eTReturnTotalHours.setEnabled(false);

        etFNameReturnBikes = (TextInputEditText) findViewById(R.id.etFirstNameReturnBikes);
        etFNameReturnBikes.setEnabled(false);
        etLNameReturnBikes = (TextInputEditText) findViewById(R.id.etLastNameReturnBikes);
        etLNameReturnBikes.setEnabled(false);

        etBikeStoreReturn = (EditText) findViewById(R.id.etBikeReturnStore);
        etBikeStoreReturn.setEnabled(false);

        ivReturnBikes = (ImageView) findViewById(R.id.imgShowReturnBikes);
        tVStoreNameReturnBikes = (TextView) findViewById(R.id.tvReturnBikesStoreName);
        tVCondReturnBikes = (TextView) findViewById(R.id.tvReturnBikesCond);
        tVModelReturnBikes = (TextView) findViewById(R.id.tvReturnBikesModel);
        tVManufactReturnBikes = (TextView) findViewById(R.id.tvReturnBikesManufact);
        tVPriceReturnBikes = (TextView) findViewById(R.id.tvReturnBikesPrice);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            //Display the Bike key received of rented bike
            bikeRented_Key = bundle.getString("BikeRentedKey");
        }

        cBoxRetSameStore = (CheckBox) findViewById(R.id.cbReturnBikeSameStore);
        cBoxRetSameStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cBoxRetSameStore.isChecked()) {
                    cBoxRetDiffStore.setChecked(false);
                    etBikeStoreReturn.setText(bike_StoreNameRentedBikesSame);
                    bikeStoreKey_ReturnBike = bike_StoreKeyRentedBikesSame;
                } else {
                    etBikeStoreReturn.setText("");
                }
            }
        });

        cBoxRetDiffStore = findViewById(R.id.cbReturnBikeDiffStore);
        cBoxRetDiffStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cBoxRetDiffStore.isChecked()) {
                    cBoxRetSameStore.setChecked(false);

                    //Send data to BikeStoreListReturnBikeDifferentStore
                    Context context = ReturnRentedBikes.this;
                    LayoutInflater li = LayoutInflater.from(context);
                    View promptsView = li.inflate(R.layout.activity_bike_store_list_return_rented_bike, null);

                    androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(context);

                    //set prompts.xml to alert dialog builder
                    alertDialogBuilder.setView(promptsView);

                    final ListView bikeStoreListViewReturnBike = promptsView.findViewById(R.id.lVBikeStoreReturnRentedBike);

                    databaseRefBikeStores = FirebaseDatabase.getInstance().getReference("Bike Stores");
                    bikeStoreListReturnBike = new ArrayList<>();

                    arrayAdapter = new ArrayAdapter<>(ReturnRentedBikes.this, R.layout.image_bikestore_return_bike, R.id.tvStorePlaceReturn, bikeStoreListReturnBike);
                    databaseRefBikeStores.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            bikeStoreListReturnBike.clear();
                            for (DataSnapshot dsBikeStores : dataSnapshot.getChildren()) {
                                BikeStores bike_Store = dsBikeStores.getValue(BikeStores.class);
                                assert bike_Store != null;
                                if (!bike_Store.getBikeStore_Key().equals(bike_StoreKeyRentedBikesSame)) {
                                    bikeStoreListReturnBike.add(bike_Store.getBikeStore_Location());
                                    bike_StoreKeyRentedBikesDiff = bike_Store.getBikeStore_Key();
                                }
                            }

                            bikeStoreListViewReturnBike.setAdapter(arrayAdapter);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(ReturnRentedBikes.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    bikeStoreListViewReturnBike.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String bikeStore_Name = bikeStoreListReturnBike.get(position);

                            etBikeStoreReturn.setText(bikeStore_Name);

                            bikeStoreKey_ReturnBike = bike_StoreKeyRentedBikesDiff;
                        }
                    });

                    //set dialog message
                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                        }
                                    })
                            
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            etBikeStoreReturn.setText("");
                                            cBoxRetDiffStore.setChecked(false);
                                        }
                                    });

                    // create alert dialog
                    androidx.appcompat.app.AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();
                } else {
                    etBikeStoreReturn.setText("");
                }
            }
        });

        Button buttonReturnBikes = (Button) findViewById(R.id.btnReturnBike);
        buttonReturnBikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bikesReturnTask != null && bikesReturnTask.isInProgress()) {
                    Toast.makeText(ReturnRentedBikes.this, "Return Bikes in progress", Toast.LENGTH_SHORT).show();
                } else {
                    returnRentedBike();
                }
            }
        });
    }

    public void returnRentedBike() {
        progressDialog.dismiss();

        final String etStoreName_ReturnBikesVal = Objects.requireNonNull(etBikeStoreReturn.getText()).toString().trim();

        if (TextUtils.isEmpty(etStoreName_ReturnBikesVal)) {
            alertReturnBikeStore();
        } else {
            storeName_ReturnBikes = etBikeStoreReturn.getText().toString().trim();
            tVCond_ReturnBikes = tVCondReturnBikes.getText().toString().trim();
            tVModel_ReturnBikes = tVModelReturnBikes.getText().toString().trim();
            tVManufact_ReturnBikes = tVManufactReturnBikes.getText().toString().trim();
            tVPrice_ReturnBikes = Double.parseDouble(tVPriceReturnBikes.getText().toString().trim());

            storageRefReturnBikes = FirebaseStorage.getInstance().getReference("Bikes");
            databaseRefReturnBikes = FirebaseDatabase.getInstance().getReference("Bikes");

            progressDialog.setTitle("The bike is returning");
            progressDialog.show();

            // Get the data from an ImageView as bytes
            ivReturnBikes.setDrawingCacheEnabled(true);
            ivReturnBikes.buildDrawingCache();
            Bitmap bitmap = ivReturnBikes.getDrawingCache();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            bikesReturnTask = storageRefReturnBikes.putBytes(data)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageRefReturnBikes.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    bikeKey_ReturnBike = databaseRefReturnBikes.push().getKey();
                                    BikesRent return_BikesRent = new BikesRent(tVCond_ReturnBikes, tVModel_ReturnBikes, tVManufact_ReturnBikes,
                                            tVPrice_ReturnBikes, uri.toString(), storeName_ReturnBikes, bikeStoreKey_ReturnBike,
                                            bikeKey_ReturnBike);

                                    databaseRefReturnBikes.child(bikeKey_ReturnBike).setValue(return_BikesRent);
                                    startActivity(new Intent(ReturnRentedBikes.this, CustomerPageRentBikes.class));
                                    deleteRentedBikes();
                                    Toast.makeText(ReturnRentedBikes.this, "Return Bike successfully", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            });
                            progressDialog.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(ReturnRentedBikes.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            //show upload progress
                            double progress = 100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage("Returned: " + (int) progress + "%");
                            progressDialog.setProgress((int) progress);
                        }
                    });
        }
    }

    private void deleteRentedBikes() {
        StorageReference firebaseStRemoveRentBikes = getInstance().getReferenceFromUrl(img_ReturnBikes);
        firebaseStRemoveRentBikes.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                databaseRefRemoveRentBikes = FirebaseDatabase.getInstance().getReference("Rent Bikes");
                Query query = databaseRefRemoveRentBikes.orderByChild("bike_RentKey").equalTo(bikeRented_Key);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            ds.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(ReturnRentedBikes.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ReturnRentedBikes.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void alertReturnBikeStore() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("The return Bike Store can not be empty.");
        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        loadCustomerDetailsRentBikes();
        loadBikesListReturn();
    }

    //Display customer details
    public void loadCustomerDetailsRentBikes() {
        //retrieve data from database into text views
        databaseRefCustomer = FirebaseDatabase.getInstance().getReference("Customers");
        databaseRefCustomer.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"SetTextI18n", "NewApi"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //retrieve data from database
                for (DataSnapshot ds_User : dataSnapshot.getChildren()) {
                    FirebaseUser user_Db = firebaseAuth.getCurrentUser();

                    Customers custom_Data = ds_User.getValue(Customers.class);

                    assert user_Db != null;
                    assert custom_Data != null;
                    if (user_Db.getUid().equals(ds_User.getKey())) {
                        tVReturnBikes.setText("Welcome: " + custom_Data.getfName_Customer() + " " + custom_Data.getlName_Customer());
                        etFNameReturnBikes.setText(custom_Data.getfName_Customer());
                        etLNameReturnBikes.setText(custom_Data.getlName_Customer());
                        bike_CusIdRentedBikes = user_Db.getUid();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ReturnRentedBikes.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Display bike details
    private void loadBikesListReturn() {
        //initialize the bike storage database
        firebaseStShowRentedBikes = getInstance();
        databaseRefShowRentedBikes = FirebaseDatabase.getInstance().getReference("Rent Bikes");

        showRentedBikesEventListener = databaseRefShowRentedBikes.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    RentBikes rent_Bikes = postSnapshot.getValue(RentBikes.class);
                    assert rent_Bikes != null;
                    rent_Bikes.setBike_RentKey(postSnapshot.getKey());
                    if (rent_Bikes.getBike_RentKey().equals(bikeRented_Key)) {
                        eTDateOfRentBike.setText(rent_Bikes.getDate_RentBikes());
                        tVStoreNameReturnBikes.setText(rent_Bikes.getStoreLocation_RentBikes());
                        tVCondReturnBikes.setText(rent_Bikes.getBikeCond_RentBikes());
                        tVModelReturnBikes.setText(rent_Bikes.getBikeModel_RentBikes());
                        tVManufactReturnBikes.setText(rent_Bikes.getBikeManufact_RentBikes());
                        tVPriceReturnBikes.setText(String.valueOf(rent_Bikes.getBikePrice_RentBikes()));

                        bike_StoreNameRentedBikesSame = rent_Bikes.getStoreLocation_RentBikes();
                        bike_StoreKeyRentedBikesSame = rent_Bikes.getStoreKey_RentBikes();

                        img_ReturnBikes = rent_Bikes.getBikeImage_RentBike();

                        calculateRentDurationPrice();

                        //receive data from the other activity
                        Picasso.get()
                                .load(rent_Bikes.getBikeImage_RentBike())
                                .placeholder(R.mipmap.ic_launcher)
                                .fit()
                                .centerCrop()
                                .into(ivReturnBikes);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ReturnRentedBikes.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void calculateRentDurationPrice() {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        String currentDateAndTime = sdf.format(new Date());
        eTDateReturnBike.setText(currentDateAndTime);

        String date1 = eTDateOfRentBike.getText().toString().trim();
        String date2 = eTDateReturnBike.getText().toString().trim();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        Duration diff = Duration.between(LocalDateTime.parse(date1, formatter),
                LocalDateTime.parse(date2, formatter));

        if (diff.isZero()) {
            Toast.makeText(ReturnRentedBikes.this, "The was rented 0m", Toast.LENGTH_SHORT).show();
        } else {
            long days = diff.toDays();
            if (days != 0) {
                diff = diff.minusDays(days);
            }
            long hours = diff.toHours();
            if (hours != 0) {
                diff = diff.minusHours(hours);
            }
            long minutes = diff.toMinutes();
            if (minutes != 0) {
                diff = diff.minusMinutes(minutes);
            }
//                            long seconds = diff.getSeconds();
//                            if (seconds != 0) {
//                                System.out.print("" + seconds + "s ");
//                            }
            System.out.println();

            eTRentDurationBike.setText(days + " d " + " " + hours + " h " + "" + minutes + " m");
            totalHours = (double) ((days * 24) + hours + (minutes / 60));
            eTReturnTotalHours.setText(String.valueOf(totalHours));
            tVPrice_ReturnBikes = Double.parseDouble(tVPriceReturnBikes.getText().toString().trim());
            eTReturn_TotalPricePay = totalHours * tVPrice_ReturnBikes;
            eTReturnTotalPricePay.setText(String.valueOf("€ " + eTReturn_TotalPricePay));
        }
    }
}
