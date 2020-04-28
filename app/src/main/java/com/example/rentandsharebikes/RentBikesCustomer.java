package com.example.rentandsharebikes;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Objects;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

public class RentBikesCustomer extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    private StorageReference storageRefRentBikes;
    private DatabaseReference databaseRefRentBikes;
    private DatabaseReference databaseRefRentBikesRemove;
    private DatabaseReference databaseRefCustomer;

    private DatabaseReference databaseRefBikes;
    private StorageTask bikesRentTask;

    private TextInputEditText etFNameRentBikes, etLNameRentBikes, etPhoneNoRentBikes, etEmailRentBikes;
    private TextView tVRentBikes,tVDateRentBike, tVStoreNameRentBikes, tVCondRentBikes, tVModelRentBikes, tVManufactRentBikes, tVPriceRentBikes;

    private EditText etCollectionDate, etReturnDate;
    private TextView tvAddCollectionDate, tvAddReturnDate;

    //variables for data received
    private String etFName_RentBikes, etLName_RentBikes, etPhoneNo_RentBikes, etEmail_RentBikes;
    private String tVDate_RentBikes, tVStoreName_RentBikes, tVCond_RentBikes, tVModel_RentBikes, tVManufact_RentBikes;
    private String etCollectBike_Date, etReturnBike_Date;

    private double tVPrice_rentBikes;

    private ImageView ivRentBikes;

    String bike_StoreNameRentBikes = "";
    String bike_CondRentBikes = "";
    String bike_ModelRentBikes = "";
    String bike_ManufactRentBikes = "";
    String bike_PriceRentBikes = "";
    String bike_ImageRentBikes = "";
    String bike_CusIdRentBikes = "";
    String bikeKey_RentBike = "";
    String bikeKey_RentedBike = "";

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_bikes_customer);

        progressDialog = new ProgressDialog(RentBikesCustomer.this);

        firebaseAuth = FirebaseAuth.getInstance();


        //storageReference = FirebaseStorage.getInstance().getReference("Bikes");
        //databaseReference = FirebaseDatabase.getInstance().getReference("Bikes");
        storageRefRentBikes = FirebaseStorage.getInstance().getReference("Rent Bikes");
        databaseRefRentBikes = FirebaseDatabase.getInstance().getReference("Rent Bikes");

        //initialise variables
        tVRentBikes = (TextView) findViewById(R.id.tvRentBikes);

        LocalDate localDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            localDate = LocalDate.now();
        }
        DateTimeFormatter formatter = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("dd/MM/YYYY");
        }
        String insertDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            insertDate = localDate.format(formatter);
        }
        tVDateRentBike =(TextView)findViewById(R.id.tvDateRentBike);
        tVDateRentBike.setText(insertDate);

        etFNameRentBikes = (TextInputEditText) findViewById(R.id.etFirstNameRentBikes);
        etLNameRentBikes = (TextInputEditText)findViewById(R.id.etLastNameRentBikes);
        etPhoneNoRentBikes = (TextInputEditText)findViewById(R.id.etPhoneNoRentBikes);
        etEmailRentBikes = (TextInputEditText)findViewById(R.id.etEmailRentBikes);

        ivRentBikes = (ImageView) findViewById(R.id.imgShowRentBikes);
        tVStoreNameRentBikes = (TextView)findViewById(R.id.tvRentBikesStoreName);
        tVCondRentBikes = (TextView)findViewById(R.id.tvRentBikesCond);
        tVModelRentBikes = (TextView)findViewById(R.id.tvRentBikesModel);
        tVManufactRentBikes = (TextView)findViewById(R.id.tvRentBikesManufact);
        tVPriceRentBikes = (TextView)findViewById(R.id.tvRentBikesPrice);

        tvAddCollectionDate = (TextView)findViewById(R.id.tvAddCollectDate);
        tvAddReturnDate = (TextView)findViewById(R.id.tvAddRetDate);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            bike_CondRentBikes = bundle.getString("BCondition");
            bike_ModelRentBikes = bundle.getString("BModel");
            bike_ManufactRentBikes = bundle.getString("BManufact");
            bike_PriceRentBikes = bundle.getString("BPrice");
            bike_ImageRentBikes = bundle.getString("BImage");
            bike_StoreNameRentBikes = bundle.getString("BStore");
            bikeKey_RentBike = bundle.getString("BKey");
        }

        //receive data from the other activity
        Picasso.get()
                .load(bike_ImageRentBikes)
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(ivRentBikes);

        tVStoreNameRentBikes.setText(bike_StoreNameRentBikes);
        tVCondRentBikes.setText(bike_CondRentBikes);
        tVModelRentBikes.setText(bike_ModelRentBikes);
        tVManufactRentBikes.setText(bike_ManufactRentBikes);
        tVPriceRentBikes.setText(String.valueOf(bike_PriceRentBikes));

        etCollectionDate = (EditText)findViewById(R.id.etCollectDate);
        etCollectionDate.setEnabled(false);
        etReturnDate = (EditText) findViewById(R.id.etRetDate);
        etReturnDate.setEnabled(false);

        tvAddCollectionDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectCollectionDate();
            }
        });

        tvAddReturnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectReturnDate();
            }
        });

        Button buttonRentBike = findViewById(R.id.btnRentBike);
        buttonRentBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadRentBikeData();
                deleteData();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        loadCustomerDetailsRentBikes();
    }

    public void loadCustomerDetailsRentBikes(){
        //retrieve data from database into text views
        databaseRefCustomer = FirebaseDatabase.getInstance().getReference("Customers");
        databaseRefCustomer.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"SetTextI18n", "NewApi"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //retrieve data from database
                for (DataSnapshot dsUser : dataSnapshot.getChildren()) {
                    final FirebaseUser custom_Details = firebaseAuth.getCurrentUser();

                    final Customers custom_data = dsUser.getValue(Customers.class);

                    assert custom_Details != null;
                    assert custom_data != null;
                    if (Objects.requireNonNull(custom_Details.getEmail()).equalsIgnoreCase(custom_data.getEmail_Customer())){
                        tVRentBikes.setText("Welcome: "+custom_data.getfName_Customer()+" "+custom_data.getlName_Customer());
                        etFNameRentBikes.setText(custom_data.getfName_Customer());
                        etLNameRentBikes.setText(custom_data.getlName_Customer());
                        etPhoneNoRentBikes.setText(custom_data.getPhoneNumb_Customer());
                        etEmailRentBikes.setText(custom_data.getEmail_Customer());
                        bike_CusIdRentBikes = custom_Details.getUid();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RentBikesCustomer.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void selectCollectionDate(){
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(RentBikesCustomer.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date_year = String.valueOf(year);
                String date_month = (month+1) < 10 ? "0" + (month+1) : String.valueOf(month+1);
                String date_day = dayOfMonth < 10 ? "0" + dayOfMonth : String.valueOf(dayOfMonth);
                String picked_Date = date_day+"/"+date_month+"/"+date_year;
                etCollectionDate.setText(picked_Date);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dialog.show();
    }

    private void selectReturnDate(){
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(RentBikesCustomer.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date_year = String.valueOf(year);
                String date_month = (month+1) < 10 ? "0" + (month+1) : String.valueOf(month+1);
                String date_day = dayOfMonth < 10 ? "0" + dayOfMonth : String.valueOf(dayOfMonth);
                String picked_Date = date_day+"/"+date_month+"/"+date_year;
                etReturnDate.setText(picked_Date);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dialog.show();
    }

    private void alertDialogCollectDateEmpty(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("The collection day cannot be empty.");
        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        selectCollectionDate();
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
                        selectReturnDate();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void alertDialogDateLower(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("The return date cannot be before the collection date.");
        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void alertDialogDateEqual(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("The return date is the same as the collection date.\nThe minimum duration of rent is one day.");
        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void uploadRentBikeData(){

        final String etFName_RentBikesVal = Objects.requireNonNull(etFNameRentBikes.getText()).toString().trim();
        final String etLName_RentBikesVal = Objects.requireNonNull(etLNameRentBikes.getText()).toString().trim();
        final String etPhoneNo_RentBikesVal = Objects.requireNonNull(etPhoneNoRentBikes.getText()).toString().trim();
        final String etEmail_RentBikesVal = Objects.requireNonNull(etEmailRentBikes.getText()).toString().trim();
        final String etCollectBike_DateVal = etCollectionDate.getText().toString().trim();
        final String etReturnBike_DateVal = etReturnDate.getText().toString().trim();

        if(TextUtils.isEmpty(etFName_RentBikesVal)) {
            etFNameRentBikes.setError("First name cannot be empty");
            etFNameRentBikes.requestFocus();
        }
        else if(TextUtils.isEmpty(etLName_RentBikesVal)) {
            etLNameRentBikes.setError("Last name cannot be empty");
            etLNameRentBikes.requestFocus();
        }

        else if(TextUtils.isEmpty(etPhoneNo_RentBikesVal)) {
            etPhoneNoRentBikes.setError("Last name cannot be empty");
            etPhoneNoRentBikes.requestFocus();
        }

        else if(TextUtils.isEmpty(etEmail_RentBikesVal)) {
            etEmailRentBikes.setError("Last name cannot be empty");
            etEmailRentBikes.requestFocus();
        }

        else if(TextUtils.isEmpty(etCollectBike_DateVal)){
            alertDialogCollectDateEmpty();
        }

        else if(TextUtils.isEmpty(etReturnBike_DateVal)){
            alertDialogReturnDateEmpty();
        }

        // -1 comes when date1 is lower then date2
        else if (etReturnBike_DateVal.compareTo(etCollectBike_DateVal) < 0){
            alertDialogDateLower();
        }

        //  0 comes when two date are same,
        else if(etReturnBike_DateVal.compareTo(etCollectBike_DateVal) == 0){
            alertDialogDateEqual();
        }

        else{
            tVDate_RentBikes = tVDateRentBike.getText().toString().trim();
            //Customer details
            etFName_RentBikes = etFNameRentBikes.getText().toString().trim();
            etLName_RentBikes = etLNameRentBikes.getText().toString().trim();
            etPhoneNo_RentBikes = etPhoneNoRentBikes.getText().toString().trim();
            etEmail_RentBikes = etEmailRentBikes.getText().toString().trim();
            tVStoreName_RentBikes = tVStoreNameRentBikes.getText().toString().trim();
            tVCond_RentBikes = tVCondRentBikes.getText().toString().trim();
            tVModel_RentBikes = tVModelRentBikes.getText().toString().trim();
            tVManufact_RentBikes = tVManufactRentBikes.getText().toString().trim();
            tVPrice_rentBikes = Double.parseDouble(tVPriceRentBikes.getText().toString().trim());

            //Date of collection and return
            etCollectBike_Date = etCollectionDate.getText().toString().trim();
            etReturnBike_Date = etReturnDate.getText().toString().trim();

            progressDialog.setTitle("The bike is rented");
            progressDialog.show();
            String rent_BikesId = databaseRefRentBikes.push().getKey();
            bikeKey_RentedBike = rent_BikesId;

            RentBikes rent_Bikes = new RentBikes(tVDate_RentBikes, etFName_RentBikes,
                    etLName_RentBikes, etPhoneNo_RentBikes, etEmail_RentBikes, tVStoreName_RentBikes, tVCond_RentBikes,
                    tVModel_RentBikes, tVManufact_RentBikes, tVPrice_rentBikes, bike_ImageRentBikes, etCollectBike_Date, etReturnBike_Date, bike_CusIdRentBikes, bikeKey_RentedBike);

            assert rent_BikesId != null;
            databaseRefRentBikes.child(rent_BikesId).setValue(rent_Bikes).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        startActivity(new Intent(RentBikesCustomer.this, CustomerPageRentBikes.class));
                        Toast.makeText(RentBikesCustomer.this, "Rent Bike successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(RentBikesCustomer.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void deleteData() {
        databaseRefRentBikesRemove = FirebaseDatabase.getInstance().getReference().child("Bikes");
        Query query = databaseRefRentBikesRemove.orderByChild("bike_Key").equalTo(bikeKey_RentBike);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ds.getRef().removeValue();
                }
                progressDialog.dismiss();
                Toast.makeText(RentBikesCustomer.this, "Bike removed", Toast.LENGTH_SHORT).show();
                //startActivity(new Intent(UpdateEve.this, UserPage.class));
                //finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RentBikesCustomer.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void deleteEventPicture(){
        progressDialog.show();
//        eventsStorage = getInstance();
//        databaseReference = FirebaseDatabase.getInstance().getReference("Events");
        StorageReference storageReference = getInstance().getReferenceFromUrl(bike_ImageRentBikes);
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                Toast.makeText(RentBikesCustomer.this, "Bike Deleted", Toast.LENGTH_SHORT).show();

                databaseRefBikes = FirebaseDatabase.getInstance().getReference().child("Bikes");

                Query query = databaseRefBikes.orderByChild("bike_key").equalTo(tVStoreName_RentBikes);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            ds.getRef().removeValue();
                        }
                        progressDialog.dismiss();
                        Toast.makeText(RentBikesCustomer.this, "Bike removed", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RentBikesCustomer.this, CustomerPageRentBikes.class));
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(RentBikesCustomer.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RentBikesCustomer.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }
}
