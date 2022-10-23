package com.example.rentandsharebikes;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.util.List;
import java.util.Locale;

public class ReturnRentedBikesSpinner extends AppCompatActivity {

    //Display Rented bikes details from Rent Bikes database
    private FirebaseStorage firebaseStShowRentedBikes;
    private DatabaseReference databaseRefShowRentedBikes;
    private ValueEventListener eventListenerShowRentedBikes;

    //Delete rented bikes from RentedBikes Database;
    private DatabaseReference databaseRefRemoveRentBikes;

    private DatabaseReference databaseRefBikeStores;

    //Declare Bike Store database variables (Retrieve data)
    private DatabaseReference dbRefStoreCheck;

    private BikeStores bike_Store;

    private List<String> listBikeStores;
    private ArrayAdapter<String> arrayAdapter;

    //Return bikes to Bikes database
    private StorageReference storageRefReturnBikes;
    private DatabaseReference databaseRefReturnBikes;

    //Access database Customers table
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseRefCustomer;

    private StorageTask bikesReturnTask;

    private TextInputEditText etFNameReturnBikes, etLNameReturnBikes;
    private TextView tVReturnBikes, tVStoreNameReturnBikes, tVCondReturnBikes, tVModelReturnBikes, tVManufactReturnBikes, tVPriceReturnBikes;
    private EditText eTDateOfRentBike, eTDateReturnBike, eTRentDurationBike, eTReturnTotalHours, eTReturnTotalPricePay;

    private AutoCompleteTextView tVBikeStoreReturnBike;

    //variables for data received
    private String storeName_ReturnBikes, tVCond_ReturnBikes, tVModel_ReturnBikes, tVManufact_ReturnBikes;

    private Double tVPrice_ReturnBikes, eTReturn_TotalPricePay;
    private Double totalHours = 0.00;

    private ImageView ivReturnBikes;

    //Receive Bike image
    private String img_RentedBike;

    String rentedBikes_CustomId = "";

    //Receive the Bike Key of rented bike from BikeAdapterRentedBikesCustomer
    String rentedBike_Key = "";
    String returnBike_Key = "";

    //BikeStoreKey that the bike is returning
    private String returnBikeStore_Key = "";

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_rented_bikes_spinner);

//        progressDialog = new ProgressDialog(ReturnRentedBikesSpinner.this);
//
//        listBikeStores = new ArrayList<>();
//
//        firebaseAuth = FirebaseAuth.getInstance();
//
//        //Retrieve data from RentedBikes database (Bikes rented by Customer)
//        firebaseStShowRentedBikes = getInstance();
//        databaseRefShowRentedBikes = FirebaseDatabase.getInstance().getReference("Rent Bikes");
//
//        //Retrieve data from Customers database (Customer that rented bikes)
//        databaseRefCustomer = FirebaseDatabase.getInstance().getReference("Customers");
//
//        //Retrieve data from BikesSores database
//        databaseRefBikeStores = FirebaseDatabase.getInstance().getReference("Bike Stores");
//
//        //Retrieve Bike Store data from Bike Stores table
//        dbRefStoreCheck = FirebaseDatabase.getInstance().getReference("Bike Stores");
//
//        //initialise variables
//        tVReturnBikes = findViewById(R.id.tvReturnBikes);
//
//        //Date of Rent
//        eTDateOfRentBike = findViewById(R.id.etDateOfRentBike);
//        eTDateOfRentBike.setEnabled(false);
//
//        //Date of Return
//        eTDateReturnBike = findViewById(R.id.etDateReturnBike);
//        eTDateReturnBike.setEnabled(false);
//
//        //Duration Time
//        eTRentDurationBike = findViewById(R.id.etRentDurationBike);
//        eTRentDurationBike.setEnabled(false);
//
//        //Total Hours
//        eTReturnTotalHours = findViewById(R.id.etReturnTotalHours);
//        eTReturnTotalHours.setEnabled(false);
//
//        eTReturnTotalPricePay = findViewById(R.id.etReturnTotalPricePay);
//        eTReturnTotalHours.setEnabled(false);
//
//        etFNameReturnBikes = findViewById(R.id.etFirstNameReturnBikes);
//        etFNameReturnBikes.setEnabled(false);
//        etLNameReturnBikes = (TextInputEditText) findViewById(R.id.etLastNameReturnBikes);
//        etLNameReturnBikes.setEnabled(false);
//
//        ivReturnBikes = findViewById(R.id.imgShowReturnBikes);
//        tVStoreNameReturnBikes = findViewById(R.id.tvReturnBikesStoreName);
//        tVCondReturnBikes = findViewById(R.id.tvReturnBikesCond);
//        tVModelReturnBikes = findViewById(R.id.tvReturnBikesModel);
//        tVManufactReturnBikes = findViewById(R.id.tvReturnBikesManufact);
//        tVPriceReturnBikes = findViewById(R.id.tvReturnBikesPrice);
//
//        Bundle bundle = getIntent().getExtras();
//        if (bundle != null) {
//
//            //Display the Bike key received of rented bike
//            rentedBike_Key = bundle.getString("BikeRentedKey");
//        }
//
//        tVBikeStoreReturnBike = findViewById(R.id.tvBikeStoreReturnBike);
//
//        databaseRefBikeStores.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                listBikeStores.clear();
//                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                    bike_Store = postSnapshot.getValue(BikeStores.class);
//                    assert bike_Store != null;
//                    listBikeStores.add(bike_Store.getBikeStore_Location());
//                }
//
//                arrayAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Toast.makeText(ReturnRentedBikesSpinner.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        //arrayAdapter = new ArrayAdapter<String>(ReturnRentedBikesSpinner.this, R.layout.image_bike_store_return_rented_bike, R.id.rbReturnBikeStore, listBikeStores);
//        arrayAdapter = new ArrayAdapter<String>(ReturnRentedBikesSpinner.this, R.layout.image_bike_store_return_rented_bike, R.id.tvReturnBikeStore, listBikeStores);
//        //arrayAdapter = new ArrayAdapter<String>(ReturnRentedBikesSpinner.this, android.R.layout.simple_list_item_single_choice, listBikeStores);
//
//        tVBikeStoreReturnBike.setAdapter(arrayAdapter);
//
//        tVBikeStoreReturnBike.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//                //String store_Name = adapterView.getItemAtPosition(i).toString();
//
//                //tVBikeStoreReturnBike.setText(store_Name);
//
//                storeName_ReturnBikes = tVBikeStoreReturnBike.getText().toString().trim();
//
//                Query query = dbRefStoreCheck.orderByChild("bikeStore_Location").equalTo(storeName_ReturnBikes);
//                query.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                            postSnapshot.getKey();
//                            returnBikeStore_Key = postSnapshot.getKey();
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//                        Toast.makeText(ReturnRentedBikesSpinner.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        });
//
//        Button buttonReturnBikes = findViewById(R.id.btnReturnBike);
//        buttonReturnBikes.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (bikesReturnTask != null && bikesReturnTask.isInProgress()) {
//                    Toast.makeText(ReturnRentedBikesSpinner.this, "Return Bikes in progress", Toast.LENGTH_SHORT).show();
//                } else {
//                    returnRentedBike();
//                }
//            }
//        });
//    }
//
//    public void returnRentedBike() {
//        progressDialog.dismiss();
//
//        if (TextUtils.isEmpty(storeName_ReturnBikes)) {
//            alertReturnBikeStore();
//        } else {
//            storeName_ReturnBikes = tVBikeStoreReturnBike.getText().toString().trim();
//            tVCond_ReturnBikes = tVCondReturnBikes.getText().toString().trim();
//            tVModel_ReturnBikes = tVModelReturnBikes.getText().toString().trim();
//            tVManufact_ReturnBikes = tVManufactReturnBikes.getText().toString().trim();
//            tVPrice_ReturnBikes = Double.parseDouble(tVPriceReturnBikes.getText().toString().trim());
//
//            storageRefReturnBikes = FirebaseStorage.getInstance().getReference("Bikes");
//            databaseRefReturnBikes = FirebaseDatabase.getInstance().getReference("Bikes");
//
//            progressDialog.setTitle("The bike is returning to: " + storeName_ReturnBikes);
//            progressDialog.show();
//
//            // Get the data from an ImageView as bytes
//            ivReturnBikes.setDrawingCacheEnabled(true);
//            ivReturnBikes.buildDrawingCache();
//            Bitmap bitmap = ivReturnBikes.getDrawingCache();
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//            byte[] data = baos.toByteArray();
//
//            bikesReturnTask = storageRefReturnBikes.putBytes(data)
//                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            storageRefReturnBikes.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                @Override
//                                public void onSuccess(Uri uri) {
//                                    returnBike_Key = databaseRefReturnBikes.push().getKey();
//                                    Bikes return_Bikes = new Bikes(tVCond_ReturnBikes, tVModel_ReturnBikes, tVManufact_ReturnBikes,
//                                            tVPrice_ReturnBikes, uri.toString(), storeName_ReturnBikes, returnBikeStore_Key,
//                                            returnBike_Key);
//
//                                    databaseRefReturnBikes.child(returnBike_Key).setValue(return_Bikes);
//                                    startActivity(new Intent(ReturnRentedBikesSpinner.this, CustomerPageRentBikes.class));
//                                    deleteRentedBikes();
//                                    Toast.makeText(ReturnRentedBikesSpinner.this, "Return Bike successfully", Toast.LENGTH_SHORT).show();
//                                    finish();
//                                }
//                            });
//                            progressDialog.dismiss();
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            progressDialog.dismiss();
//                            Toast.makeText(ReturnRentedBikesSpinner.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
//                            //show upload progress
//                            double progress = 100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount();
//                            progressDialog.setMessage("Returned: " + (int) progress + "%");
//                            progressDialog.setProgress((int) progress);
//                        }
//                    });
//        }
//    }
//
//    private void deleteRentedBikes() {
//
//        StorageReference firebaseStRemoveRentBikes = getInstance().getReferenceFromUrl(img_RentedBike);
//        firebaseStRemoveRentBikes.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(@NonNull Void aVoid) {
//
//                databaseRefRemoveRentBikes = FirebaseDatabase.getInstance().getReference("Rent Bikes");
//                Query query = databaseRefRemoveRentBikes.orderByChild("bike_RentKey").equalTo(rentedBike_Key);
//                query.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                            ds.getRef().removeValue();
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//                        Toast.makeText(ReturnRentedBikesSpinner.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(ReturnRentedBikesSpinner.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    public void alertReturnBikeStore() {
//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
//        alertDialogBuilder.setMessage("The return Bike Store can not be empty.");
//        alertDialogBuilder.setPositiveButton("OK",
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface arg0, int arg1) {
//                    }
//                });
//
//        AlertDialog alertDialog = alertDialogBuilder.create();
//        alertDialog.show();
//    }
//
//    public void alertBikeStoreDeleted() {
//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
//        alertDialogBuilder.setMessage("The return Bike Store has been removed.\n Please select a different Bike Store.");
//        alertDialogBuilder.setPositiveButton("OK",
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface arg0, int arg1) {
//                    }
//                });
//
//        AlertDialog alertDialog = alertDialogBuilder.create();
//        alertDialog.show();
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        loadCustomerDetailsRentBikes();
//        loadBikesListReturn();
//    }
//
//    //Display customer details
//    public void loadCustomerDetailsRentBikes() {
//
//        databaseRefCustomer.addValueEventListener(new ValueEventListener() {
//            @SuppressLint({"SetTextI18n", "NewApi"})
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                //retrieve data from database
//                for (DataSnapshot ds_User : dataSnapshot.getChildren()) {
//                    FirebaseUser user_Db = firebaseAuth.getCurrentUser();
//
//                    Customers custom_Data = ds_User.getValue(Customers.class);
//
//                    assert user_Db != null;
//                    assert custom_Data != null;
//                    if (user_Db.getUid().equals(ds_User.getKey())) {
//                        tVReturnBikes.setText("Welcome: " + custom_Data.getfName_Customer() + " " + custom_Data.getlName_Customer());
//                        etFNameReturnBikes.setText(custom_Data.getfName_Customer());
//                        etLNameReturnBikes.setText(custom_Data.getlName_Customer());
//                        rentedBikes_CustomId = user_Db.getUid();
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Toast.makeText(ReturnRentedBikesSpinner.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    //Display bike details
//    private void loadBikesListReturn() {
//
//        eventListenerShowRentedBikes = databaseRefShowRentedBikes.addValueEventListener(new ValueEventListener() {
//            @SuppressLint("SetTextI18n")
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                    RentedBikes rented_Bikes = postSnapshot.getValue(RentedBikes.class);
//                    assert rented_Bikes != null;
//                    rented_Bikes.setBike_RentKey(postSnapshot.getKey());
//                    if (rented_Bikes.getBike_RentKey().equals(rentedBike_Key)) {
//                        eTDateOfRentBike.setText(rented_Bikes.getDate_RentBikes());
//                        tVStoreNameReturnBikes.setText(rented_Bikes.getStoreLocation_RentBikes());
//                        tVCondReturnBikes.setText(rented_Bikes.getBikeCond_RentBikes());
//                        tVModelReturnBikes.setText(rented_Bikes.getBikeModel_RentBikes());
//                        tVManufactReturnBikes.setText(rented_Bikes.getBikeManufact_RentBikes());
//                        tVPriceReturnBikes.setText(String.valueOf(rented_Bikes.getBikePrice_RentBikes()));
//
//                        img_RentedBike = rented_Bikes.getBikeImage_RentBike();
//
//                        calculateRentDurationPrice();
//
//                        Picasso.get()
//                                .load(rented_Bikes.getBikeImage_RentBike())
//                                .placeholder(R.mipmap.ic_launcher)
//                                .fit()
//                                .centerCrop()
//                                .into(ivReturnBikes);
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Toast.makeText(ReturnRentedBikesSpinner.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    @SuppressLint("SetTextI18n")
//    public void calculateRentDurationPrice() {
//
//        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
//        String currentDateAndTime = sdf.format(new Date());
//        eTDateReturnBike.setText(currentDateAndTime);
//
//        String date1 = eTDateOfRentBike.getText().toString().trim();
//        String date2 = eTDateReturnBike.getText().toString().trim();
//
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
//        Duration diff = Duration.between(LocalDateTime.parse(date1, formatter),
//                LocalDateTime.parse(date2, formatter));
//
//        if (diff.isZero()) {
//            Toast.makeText(ReturnRentedBikesSpinner.this, "The was rented 0m", Toast.LENGTH_SHORT).show();
//        } else {
//            long days = diff.toDays();
//            if (days != 0) {
//                diff = diff.minusDays(days);
//            }
//            long hours = diff.toHours();
//            if (hours != 0) {
//                diff = diff.minusHours(hours);
//            }
//            long minutes = diff.toMinutes();
//            if (minutes != 0) {
//                diff = diff.minusMinutes(minutes);
//            }
////                            long seconds = diff.getSeconds();
////                            if (seconds != 0) {
////                                System.out.print("" + seconds + "s ");
////                            }
//
//            eTRentDurationBike.setText(days + " d " + " " + hours + " h " + "" + minutes + " m");
//            totalHours = (double) ((days * 24) + hours + (minutes / 60));
//            eTReturnTotalHours.setText(String.valueOf(totalHours));
//            tVPrice_ReturnBikes = Double.parseDouble(tVPriceReturnBikes.getText().toString().trim());
//            eTReturn_TotalPricePay = totalHours * tVPrice_ReturnBikes;
//            eTReturnTotalPricePay.setText("€ " + eTReturn_TotalPricePay);
//        }
    }
}