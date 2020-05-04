package com.example.rentandsharebikes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.util.Calendar;
import java.util.Objects;

import static com.google.firebase.storage.FirebaseStorage.getInstance;


public class UpdateBikeSharedDetails extends AppCompatActivity {

    private static final int REQUEST_IMAGE_GET = 2;
    private static final int IMAGE_CAPTURE_CODE = 1001;
    private static final int PERMISSION_CODE = 1000;

    //Save bike details into Share Bikes able
    private StorageReference storageRefUpShare;
    private DatabaseReference databaseRefUpShare;
    private StorageTask shareUpTask;

    //Access Customer Database
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseRefCustomUpdateShare;

    //Customer Details
    private EditText etUpFNameShareBike, etUpLNameShareBike, etUpPNoShareBike, etUpEmailShareBike;
    //Bike Details
    private EditText etUpModelShareBike, etUpManufactShareBike, etUpPriceShareBike, etUpDateAvShareBike;
    private TextView tVUpShareBikes, tVUpAvDateShareBike;
    private AutoCompleteTextView tVUpCondShareBike;
    private ImageView imgArrCondUpShareBike;
    private ImageButton buttonUpShareTPicture;
    private Button buttonUpShareBike;

    //Customer details variables
    private String etUpFName_ShareBike, etUpLName_ShareBike, etUpPNo_ShareBike, etUpEmail_ShareBike;

    //Bike details variables
    private String tVUpCond_ShareBike, etUpModel_ShareBike, etUpManufact_ShareBike, etUpDateAv_ShareBike;
    private double etUpPrice_ShareBike;
    private ImageView ivUpShareBike;
    private Uri imageUpShareUri;

    String bike_shareUpCond = "";
    String bike_shareUpModel = "";
    String bike_shareUpManufact = "";
    String bike_shareUpPrice = "";
    String bike_shareUpImage = "";
    String bike_shareUpDateAv = "";
    String bike_KeyUpShare = "";
    String customId_UpShareBikes = "";

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_bike_shared_details);

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(UpdateBikeSharedDetails.this);

        storageRefUpShare = getInstance().getReference("Share Bikes");
        databaseRefUpShare = FirebaseDatabase.getInstance().getReference("Share Bikes");

        tVUpShareBikes = (TextView) findViewById(R.id.tvUpShareBikes);
        etUpFNameShareBike = (EditText) findViewById(R.id.etUpShareBikeFName);
        etUpLNameShareBike = (EditText) findViewById(R.id.etUpShareBikeLName);
        etUpPNoShareBike = (EditText) findViewById(R.id.etUpShareBikePNumber);
        etUpEmailShareBike = (EditText) findViewById(R.id.etUpShareBikeEmail);
        ivUpShareBike = (ImageView) findViewById(R.id.imgViewUpShareBikes);
        tVUpCondShareBike = (AutoCompleteTextView) findViewById(R.id.tvUpShareBikeCond);

        imgArrCondUpShareBike = (ImageView) findViewById(R.id.imgArrowUpShareBikeCond);

        ArrayAdapter<String> conditionAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, bikeUpdateCondition);
        tVUpCondShareBike.setAdapter(conditionAdapter);

        imgArrCondUpShareBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tVUpCondShareBike.showDropDown();
            }
        });

        etUpModelShareBike = (EditText) findViewById(R.id.etUpShareBikeModel);
        etUpManufactShareBike = (EditText) findViewById(R.id.etUpShareBikeManufact);
        etUpPriceShareBike = (EditText) findViewById(R.id.etUpShareBikePriceDay);
        tVUpAvDateShareBike = (TextView) findViewById(R.id.tvUpShareBikeAvDate);
        etUpDateAvShareBike = (EditText) findViewById(R.id.etUpShareBikeAvDate);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            bike_shareUpCond = bundle.getString("BCondUpdate");
            bike_shareUpModel = bundle.getString("BModelUpdate");
            bike_shareUpManufact = bundle.getString("BManufUpdate");
            bike_shareUpPrice = bundle.getString("BPriceUpdate");
            bike_shareUpImage = bundle.getString("BImgUpdate");
            bike_shareUpDateAv = bundle.getString("BDateAvUpdate");
            customId_UpShareBikes = bundle.getString("CIdUpdate");
            bike_KeyUpShare = bundle.getString("BKeyUpdate");
        }

        //tVUpCondShareBike.setText(bike_shareUpCond);
        etUpModelShareBike.setText(bike_shareUpModel);
        etUpManufactShareBike.setText(bike_shareUpManufact);
        etUpPriceShareBike.setText(String.valueOf(bike_shareUpPrice));
        etUpDateAvShareBike.setText(bike_shareUpDateAv);

        //receive data from the other activity
        Picasso.get()
                .load(bike_shareUpImage)
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(ivUpShareBike);

        tVUpAvDateShareBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectShareUpAvDate();
            }
        });

        ivUpShareBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteOldShareBikePicture();
                openGallery();
            }
        });

        buttonUpShareTPicture = (ImageButton) findViewById(R.id.btnUpShareTakePicture);
        buttonUpShareTPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteOldShareBikePicture();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) ==
                            PackageManager.PERMISSION_DENIED ||
                            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                                    PackageManager.PERMISSION_DENIED) {
                        String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permission, PERMISSION_CODE);
                    } else {
                        openCamera();
                    }
                } else {
                    openCamera();
                }
            }
        });

        //Action button Save Bike
        buttonUpShareBike = (Button) findViewById(R.id.btnShareUpBike);
        buttonUpShareBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //show progress Dialog
                progressDialog.show();
                if (shareUpTask != null && shareUpTask.isInProgress()) {
                    Toast.makeText(UpdateBikeSharedDetails.this, "Update share bike in progress", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (imageUpShareUri == null){
                        upBikesSharedWithOldPicture();
                    }
                    else{
                        upBikesSharedWithNewPicture();
                    }
                }
            }
        });
    }

    private void selectShareUpAvDate() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(UpdateBikeSharedDetails.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date_year = String.valueOf(year);
                String date_month = (month + 1) < 10 ? "0" + (month + 1) : String.valueOf(month + 1);
                String date_day = dayOfMonth < 10 ? "0" + dayOfMonth : String.valueOf(dayOfMonth);
                String picked_Date = date_day + "/" + date_month + "/" + date_year;
                etUpDateAvShareBike.setText(picked_Date);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dialog.show();
    }

    public void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
        imageUpShareUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUpShareUri);
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_GET);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    openCamera();
                } else {
                    Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                    // permission deniedDisable the
                    // functionality that depends on this permission.
                }
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            ivUpShareBike.setImageURI(imageUpShareUri);
            Toast.makeText(getApplicationContext(), "Image captured by Camera", Toast.LENGTH_SHORT).show();
        }

        if (requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK) {
            try {
                imageUpShareUri = data.getData();
                Bitmap thumbnail = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUpShareUri);
                ivUpShareBike.setImageBitmap(thumbnail);
                Toast.makeText(UpdateBikeSharedDetails.this, "Image picked from Gallery", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public void deleteOldShareBikePicture(){
        progressDialog.show();

        StorageReference storageReferShareUp = getInstance().getReferenceFromUrl(bike_shareUpImage);
        storageReferShareUp.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(UpdateBikeSharedDetails.this, "Previous image was deleted", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateBikeSharedDetails.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }
    //Upload a new Bicycle into the Bicycles table
    public void upBikesSharedWithNewPicture() {
        progressDialog.dismiss();

        final String etFName_ShareBikeVal = etUpFNameShareBike.getText().toString().trim();
        final String etLName_ShareBikeVal = etUpLNameShareBike.getText().toString().trim();
        final String etPNo_ShareBikeVal = etUpPNoShareBike.getText().toString().trim();
        final String etEmail_ShareBikeVal = etUpEmailShareBike.getText().toString().trim();

        final String tVCond_ShareBikeVal = tVUpCondShareBike.getText().toString().trim();
        final String etModel_ShareBikeVal = etUpModelShareBike.getText().toString().trim();
        final String etManufact_ShareBikeVal = etUpManufactShareBike.getText().toString().trim();
        final String etPrice_ShareBikeVal = etUpPriceShareBike.getText().toString().trim();
        final String etDateAv_ShareBikeVal = etUpDateAvShareBike.getText().toString().trim();

        if (imageUpShareUri == null) {
            Toast.makeText(UpdateBikeSharedDetails.this, "Please add a picture", Toast.LENGTH_SHORT).show();
        }

        else if (TextUtils.isEmpty(etFName_ShareBikeVal)) {
            etUpFNameShareBike.setError("Please enter the First Name");
            etUpFNameShareBike.requestFocus();
        }
        else if (TextUtils.isEmpty(etLName_ShareBikeVal)) {
            etUpLNameShareBike.setError("Please enter the Last Name");
            etUpLNameShareBike.requestFocus();
        }
        else if (TextUtils.isEmpty(etPNo_ShareBikeVal)) {
            etUpPNoShareBike.setError("Please enter the Phone Number");
            etUpPNoShareBike.requestFocus();
        }
        else if (TextUtils.isEmpty(etEmail_ShareBikeVal)) {
            etUpEmailShareBike.setError("Please enter the Email Address");
            etUpEmailShareBike.requestFocus();
        } else if (TextUtils.isEmpty(tVCond_ShareBikeVal)) {
            alertDialogBikeShareUpCond();
            tVUpCondShareBike.requestFocus();
        } else if (TextUtils.isEmpty(etModel_ShareBikeVal)) {
            etUpModelShareBike.setError("Please add the Model of Bicycle");
            etUpModelShareBike.requestFocus();
        } else if (TextUtils.isEmpty(etManufact_ShareBikeVal)) {
            etUpManufactShareBike.setError("Please add the Manufacturer");
            etUpManufactShareBike.requestFocus();
        } else if (TextUtils.isEmpty(etPrice_ShareBikeVal)) {
            etUpPriceShareBike.setError("Please add the Price/Day ");
            etUpPriceShareBike.requestFocus();
        } else if (TextUtils.isEmpty(etDateAv_ShareBikeVal)) {
            alertDialogAvailableDateEmpty();
        }
        //Add a new Bike into the Bike's table
        else {
            etUpFName_ShareBike = etUpFNameShareBike.getText().toString().trim();
            etUpLName_ShareBike = etUpLNameShareBike.getText().toString().trim();
            etUpPNo_ShareBike = etUpPNoShareBike.getText().toString().trim();
            etUpEmail_ShareBike = etUpEmailShareBike.getText().toString().trim();

            tVUpCond_ShareBike = tVUpCondShareBike.getText().toString().trim();
            etUpModel_ShareBike = etUpModelShareBike.getText().toString().trim();
            etUpManufact_ShareBike = etUpManufactShareBike.getText().toString().trim();
            etUpPrice_ShareBike = Double.parseDouble(etUpPriceShareBike.getText().toString().trim());
            etUpDateAv_ShareBike = etUpDateAvShareBike.getText().toString().trim();

            progressDialog.setTitle("The Bike is Updating");
            progressDialog.show();
            final StorageReference fileReference = storageRefUpShare.child(System.currentTimeMillis() + "." + getFileExtension(imageUpShareUri));
            shareUpTask = fileReference.putFile(imageUpShareUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(final Uri uri) {
                                    databaseRefUpShare = FirebaseDatabase.getInstance().getReference().child("Share Bikes");

                                    Query query = databaseRefUpShare.orderByChild("shareBike_Key").equalTo(bike_KeyUpShare);
                                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                ds.getRef().child("shareCus_FirstName").setValue(etUpFName_ShareBike);
                                                ds.getRef().child("shareCus_LastName").setValue(etUpLName_ShareBike);
                                                ds.getRef().child("shareCus_PhoneNo").setValue(etUpPNo_ShareBike);
                                                ds.getRef().child("shareCus_EmailAdd").setValue(etUpEmail_ShareBike);

                                                ds.getRef().child("shareBike_Condition").setValue(tVUpCond_ShareBike);
                                                ds.getRef().child("shareBike_Model").setValue(etUpModel_ShareBike);
                                                ds.getRef().child("shareBike_Manufact").setValue(etUpManufact_ShareBike);
                                                ds.getRef().child("shareBike_Price").setValue(String.valueOf(etUpPrice_ShareBike));
                                                ds.getRef().child("shareBike_DateAv").setValue(etUpDateAv_ShareBike);
                                                ds.getRef().child("shareBike_Image").setValue(uri.toString());
                                            }
                                            progressDialog.dismiss();
                                            Toast.makeText(UpdateBikeSharedDetails.this, "The Shared Bike will be updated", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(UpdateBikeSharedDetails.this, CustomerPageShareBikes.class));
                                            finish();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            Toast.makeText(UpdateBikeSharedDetails.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                            progressDialog.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(UpdateBikeSharedDetails.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            //show upload Progress
                            double progress = 100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage("Uploaded: " + (int) progress + "%");
                            progressDialog.setProgress((int) progress);
                        }
                    });
        }
    }

    public void upBikesSharedWithOldPicture(){
        progressDialog.dismiss();

        if (validateShareBikesDetails()){

            etUpFName_ShareBike = etUpFNameShareBike.getText().toString().trim();
            etUpLName_ShareBike = etUpLNameShareBike.getText().toString().trim();
            etUpPNo_ShareBike = etUpPNoShareBike.getText().toString().trim();
            etUpEmail_ShareBike = etUpEmailShareBike.getText().toString().trim();

            tVUpCond_ShareBike = tVUpCondShareBike.getText().toString().trim();
            etUpModel_ShareBike = etUpModelShareBike.getText().toString().trim();
            etUpManufact_ShareBike = etUpManufactShareBike.getText().toString().trim();
            etUpPrice_ShareBike = Double.parseDouble(etUpPriceShareBike.getText().toString().trim());
            etUpDateAv_ShareBike = etUpDateAvShareBike.getText().toString().trim();

            progressDialog.setTitle("The Share Bike is Updating");
            progressDialog.show();

            databaseRefUpShare = FirebaseDatabase.getInstance().getReference().child("Share Bikes");

            Query query = databaseRefUpShare.orderByChild("shareBike_Key").equalTo(bike_KeyUpShare);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        ds.getRef().child("shareCus_FirstName").setValue(etUpFName_ShareBike);
                        ds.getRef().child("shareCus_LastName").setValue(etUpLName_ShareBike);
                        ds.getRef().child("shareCus_PhoneNo").setValue(etUpPNo_ShareBike);
                        ds.getRef().child("shareCus_EmailAdd").setValue(etUpEmail_ShareBike);

                        ds.getRef().child("shareBike_Condition").setValue(tVUpCond_ShareBike);
                        ds.getRef().child("shareBike_Model").setValue(etUpModel_ShareBike);
                        ds.getRef().child("shareBike_Manufact").setValue(etUpManufact_ShareBike);
                        ds.getRef().child("shareBike_Price").setValue(String.valueOf(etUpPrice_ShareBike));
                        ds.getRef().child("shareBike_DateAv").setValue(etUpDateAv_ShareBike);
                    }
                    progressDialog.dismiss();
                    Toast.makeText(UpdateBikeSharedDetails.this, "The Shared Bike will be updated", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(UpdateBikeSharedDetails.this, CustomerPageShareBikes.class));
                    finish();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(UpdateBikeSharedDetails.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            progressDialog.dismiss();
        }
    }

    public boolean validateShareBikesDetails() {
        boolean result = false;
        final String etFName_ShareBikeVal = etUpFNameShareBike.getText().toString().trim();
        final String etLName_ShareBikeVal = etUpLNameShareBike.getText().toString().trim();
        final String etPNo_ShareBikeVal = etUpPNoShareBike.getText().toString().trim();
        final String etEmail_ShareBikeVal = etUpEmailShareBike.getText().toString().trim();

        final String tVCond_ShareBikeVal = tVUpCondShareBike.getText().toString().trim();
        final String etModel_ShareBikeVal = etUpModelShareBike.getText().toString().trim();
        final String etManufact_ShareBikeVal = etUpManufactShareBike.getText().toString().trim();
        final String etPrice_ShareBikeVal = etUpPriceShareBike.getText().toString().trim();
        final String etDateAv_ShareBikeVal = etUpDateAvShareBike.getText().toString().trim();

        if (TextUtils.isEmpty(etFName_ShareBikeVal)) {
            etUpFNameShareBike.setError("Please enter the First Name");
            etUpFNameShareBike.requestFocus();
        }
        if (TextUtils.isEmpty(etLName_ShareBikeVal)) {
            etUpLNameShareBike.setError("Please enter the Last Name");
            etUpLNameShareBike.requestFocus();
        }
        if (TextUtils.isEmpty(etPNo_ShareBikeVal)) {
            etUpPNoShareBike.setError("Please enter the Phone Number");
            etUpPNoShareBike.requestFocus();
        }
        if (TextUtils.isEmpty(etEmail_ShareBikeVal)) {
            etUpEmailShareBike.setError("Please enter the Email Address");
            etUpEmailShareBike.requestFocus();
        } else if (TextUtils.isEmpty(tVCond_ShareBikeVal)) {
            alertDialogBikeShareUpCond();
            tVUpCondShareBike.requestFocus();
        } else if (TextUtils.isEmpty(etModel_ShareBikeVal)) {
            etUpModelShareBike.setError("Please add the Model of Bicycle");
            etUpModelShareBike.requestFocus();
        } else if (TextUtils.isEmpty(etManufact_ShareBikeVal)) {
            etUpManufactShareBike.setError("Please add the Manufacturer");
            etUpManufactShareBike.requestFocus();
        } else if (TextUtils.isEmpty(etPrice_ShareBikeVal)) {
            etUpPriceShareBike.setError("Please add the Price/Day ");
            etUpPriceShareBike.requestFocus();
        } else if (TextUtils.isEmpty(etDateAv_ShareBikeVal)) {
            alertDialogAvailableDateEmpty();
        } else {
            result = true;
        }

        return result;
    }

    public void alertDialogBikeShareUpCond(){
        androidx.appcompat.app.AlertDialog.Builder
        alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Select the Bike Condition");
        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });

        androidx.appcompat.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private static final String[] bikeUpdateCondition = new String[]{"Brand New", "Used Bike"};


    //Pick the share bike available date
    private void selectShareAvailableDate() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(UpdateBikeSharedDetails.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date_year = String.valueOf(year);
                String date_month = (month + 1) < 10 ? "0" + (month + 1) : String.valueOf(month + 1);
                String date_day = dayOfMonth < 10 ? "0" + dayOfMonth : String.valueOf(dayOfMonth);
                String picked_Date = date_day + "/" + date_month + "/" + date_year;
                etUpDateAvShareBike.setText(picked_Date);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dialog.show();
    }

    private void alertDialogAvailableDateEmpty() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("The available day cannot be empty.");
        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        selectShareAvailableDate();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    @Override
    public void onStart() {
        super.onStart();
        loadCustomDetailsUpShareBikes();
    }

    public void loadCustomDetailsUpShareBikes() {
        //retrieve data from database into text views
        databaseRefCustomUpdateShare = FirebaseDatabase.getInstance().getReference("Customers");
        databaseRefCustomUpdateShare.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"SetTextI18n", "NewApi"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //retrieve data from database
                for (DataSnapshot dsUser : dataSnapshot.getChildren()) {
                    final FirebaseUser custom_Details = firebaseAuth.getCurrentUser();

                    final Customers custom_data = dsUser.getValue(Customers.class);
                    assert custom_Details != null;
                    assert custom_data != null;
                    if (Objects.requireNonNull(custom_Details.getEmail()).equalsIgnoreCase(custom_data.getEmail_Customer())) {
                        tVUpShareBikes.setText("Welcome: " + custom_data.getfName_Customer() + " " + custom_data.getlName_Customer());
                        etUpFNameShareBike.setText(custom_data.getfName_Customer());
                        etUpLNameShareBike.setText(custom_data.getlName_Customer());
                        etUpPNoShareBike.setText(custom_data.getPhoneNumb_Customer());
                        etUpEmailShareBike.setText(custom_data.getEmail_Customer());
                        customId_UpShareBikes = custom_Details.getUid();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UpdateBikeSharedDetails.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
