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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.Objects;

public class ShareBikesCustomer extends AppCompatActivity {

    static final int REQUEST_IMAGE_GET = 2;
    private static final int IMAGE_CAPTURE_CODE = 1001;
    private static final int PERMISSION_CODE = 1000;

    //Save bike details into Share Bikes able
    private StorageReference storageRefShareBikes;
    private DatabaseReference databaseRefShareBikes;
    private StorageTask shareUploadTask;

    //Access Customer Database
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseRefCustomer;

    //Customer Details
    private EditText etFNameShareBike, etLNameShareBike, etPNoShareBike, etEmailShareBike;
    //Bike Details
    private EditText etModelShareBike, etManufactShareBike, etPriceShareBike, etDateAvShareBike;
    private TextView tVShareBikes, tVAvDateShareBike;
    private AutoCompleteTextView tVCondShareBike;
    private ImageView imgArrowCondShareBike;
    private ImageButton buttonShareTakePicture;
    private Button buttonShareBike;

    //Customer details variables
    private String etFName_ShareBike, etLName_ShareBike, etPNo_ShareBike, etEmail_ShareBike;

    //Bike details variables
    private String tVCond_ShareBike, etModel_ShareBike, etManufact_ShareBike, etDateAv_ShareBike;
    private double etPrice_ShareBike;
    private ImageView ivShareBike;
    private Uri imageShareUri;

    String bike_KeyShare = "";
    String customId_ShareBikes = "";

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_bikes_customer);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(ShareBikesCustomer.this);

        storageRefShareBikes = FirebaseStorage.getInstance().getReference("Share Bikes");
        databaseRefShareBikes = FirebaseDatabase.getInstance().getReference("Share Bikes");

        tVCondShareBike = (AutoCompleteTextView)findViewById(R.id.tvShareBikeCond);
        imgArrowCondShareBike = (ImageView)findViewById(R.id.imgArrowShareBikeCond);

        ArrayAdapter<String> conditionAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,bikeShareCondition);
        tVCondShareBike.setAdapter(conditionAdapter);

        imgArrowCondShareBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tVCondShareBike.showDropDown();
            }
        });

        tVShareBikes = (TextView)findViewById(R.id.tvShareBikes);
        etFNameShareBike = (EditText)findViewById(R.id.etShareBikeFName);
        etLNameShareBike = (EditText)findViewById(R.id.etShareBikeLName);
        etPNoShareBike = (EditText)findViewById(R.id.etShareBikePNumber);
        etEmailShareBike = (EditText)findViewById(R.id.etShareBikeEmail);
        etModelShareBike = (EditText) findViewById(R.id.etShareBikeModel);
        etManufactShareBike = (EditText) findViewById(R.id.etShareBikeManufact);
        etPriceShareBike = (EditText) findViewById(R.id.etShareBikePriceDay);
        tVAvDateShareBike = (TextView) findViewById(R.id.tvShareBikeAvDate);
        etDateAvShareBike = (EditText)findViewById(R.id.etShareBikeAvDate);
        etDateAvShareBike.setEnabled(false);

        ivShareBike = (ImageView) findViewById(R.id.imgViewShareBikes);
        ivShareBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        buttonShareTakePicture = (ImageButton) findViewById(R.id.btnShareTakePicture);
        buttonShareTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        //Select the Bike available share date
        tVAvDateShareBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectShareAvailableDate();
            }
        });

        //Action button Save share Bike
        buttonShareBike = (Button) findViewById(R.id.btnShareBike);
        buttonShareBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //show progress Dialog
                progressDialog.show();
                if (shareUploadTask != null && shareUploadTask.isInProgress()) {
                    Toast.makeText(ShareBikesCustomer.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadShareBikes();
                }
            }
        });
    }

    //Pick the share bike available date
    private void selectShareAvailableDate(){
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(ShareBikesCustomer.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date_year = String.valueOf(year);
                String date_month = (month+1) < 10 ? "0" + (month+1) : String.valueOf(month+1);
                String date_day = dayOfMonth < 10 ? "0" + dayOfMonth : String.valueOf(dayOfMonth);
                String picked_Date = date_day+"/"+date_month+"/"+date_year;
                etDateAvShareBike.setText(picked_Date);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dialog.show();
    }

    private void alertDialogAvailableDateEmpty(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("The available  day cannot be empty.");
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

    public void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
        imageShareUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageShareUri);
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
            ivShareBike.setImageURI(imageShareUri);
            Toast.makeText(getApplicationContext(), "Image captured by Camera", Toast.LENGTH_SHORT).show();
        }

        if (requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK) {
            try {
                imageShareUri = data.getData();
                Bitmap thumbnail = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageShareUri);
                ivShareBike.setImageBitmap(thumbnail);
                Toast.makeText(ShareBikesCustomer.this, "Image picked from Gallery", Toast.LENGTH_SHORT).show();
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

    //Upload a new Bicycle into the Share Bicycles table
    public void uploadShareBikes() {
        progressDialog.dismiss();

        final String etFName_ShareBikeVal = etFNameShareBike.getText().toString().trim();
        final String etLName_ShareBikeVal = etLNameShareBike.getText().toString().trim();
        final String etPNo_ShareBikeVal = etPNoShareBike.getText().toString().trim();
        final String etEmail_ShareBikeVal = etEmailShareBike.getText().toString().trim();

        final String tVCond_ShareBikeVal = tVCondShareBike.getText().toString().trim();
        final String etModel_ShareBikeVal = etModelShareBike.getText().toString().trim();
        final String etManufact_ShareBikeVal = etManufactShareBike.getText().toString().trim();
        final String etPrice_ShareBikeVal = etPriceShareBike.getText().toString().trim();
        final String etDateAv_ShareBikeVal = etDateAvShareBike.getText().toString().trim();

        if(TextUtils.isEmpty(etFName_ShareBikeVal)){
            etFNameShareBike.setError("Please enter the First Name");
            etFNameShareBike.requestFocus();
        }
        if(TextUtils.isEmpty(etLName_ShareBikeVal)){
            etLNameShareBike.setError("Please enter the Last Name");
            etLNameShareBike.requestFocus();
        }
        if(TextUtils.isEmpty(etPNo_ShareBikeVal)){
            etPNoShareBike.setError("Please enter the Phone Number");
            etPNoShareBike.requestFocus();
        }
        if(TextUtils.isEmpty(etEmail_ShareBikeVal)){
            etEmailShareBike.setError("Please enter the Email Address");
            etEmailShareBike.requestFocus();
        }
        else if (imageShareUri == null) {
            Toast.makeText(ShareBikesCustomer.this, "Please add a picture", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(tVCond_ShareBikeVal)){
            alertDialogBikeShareCond();
            tVCondShareBike.requestFocus();
        }
        else if (TextUtils.isEmpty(etModel_ShareBikeVal)) {
            etModelShareBike.setError("Please add the Model of Bicycle");
            etModelShareBike.requestFocus();
        }
        else if (TextUtils.isEmpty(etManufact_ShareBikeVal)) {
            etManufactShareBike.setError("Please add the Manufacturer");
            etManufactShareBike.requestFocus();
        }
        else if (TextUtils.isEmpty(etPrice_ShareBikeVal)) {
            etPriceShareBike.setError("Please add the Price/Day ");
            etPriceShareBike.requestFocus();
        }

        else if(TextUtils.isEmpty(etDateAv_ShareBikeVal)){
            alertDialogAvailableDateEmpty();
        }

        //Add a new Bike into the Bike's table
        else {
            etFName_ShareBike = etFNameShareBike.getText().toString().trim();
            etLName_ShareBike = etLNameShareBike.getText().toString().trim();
            etPNo_ShareBike = etPNoShareBike.getText().toString().trim();
            etEmail_ShareBike = etEmailShareBike.getText().toString().trim();

            tVCond_ShareBike = tVCondShareBike.getText().toString().trim();
            etModel_ShareBike = etModelShareBike.getText().toString().trim();
            etManufact_ShareBike = etManufactShareBike.getText().toString().trim();
            etPrice_ShareBike = Double.parseDouble(etPriceShareBike.getText().toString().trim());
            etDateAv_ShareBike = etDateAvShareBike.getText().toString().trim();

            progressDialog.setTitle("The Bike is Uploading");
            progressDialog.show();
            final StorageReference fileReference = storageRefShareBikes.child(System.currentTimeMillis() + "." + getFileExtension(imageShareUri));
            shareUploadTask = fileReference.putFile(imageShareUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    bike_KeyShare = databaseRefShareBikes.push().getKey();

                                    ShareBikes rent_Bikes = new ShareBikes(etFName_ShareBike, etLName_ShareBike, etPNo_ShareBike, etEmail_ShareBike,
                                            tVCond_ShareBike, etModel_ShareBike, etManufact_ShareBike, etPrice_ShareBike, etDateAv_ShareBike, uri.toString(), customId_ShareBikes, bike_KeyShare);

                                    assert bike_KeyShare != null;
                                    databaseRefShareBikes.child(bike_KeyShare).setValue(rent_Bikes);

                                    etFNameShareBike.setText("");
                                    etLNameShareBike.setText("");
                                    etPNoShareBike.setText("");
                                    etEmailShareBike.setText("");
                                    tVCondShareBike.setText("");
                                    etModelShareBike.setText("");
                                    etManufactShareBike.setText("");
                                    etPriceShareBike.setText("");
                                    etDateAvShareBike.setText("");
                                    ivShareBike.setImageResource(R.drawable.add_bikes_picture);

                                    Intent add_Bikes = new Intent(ShareBikesCustomer.this, CustomerPageShareBikes.class);
                                    startActivity(add_Bikes);

                                    Toast.makeText(ShareBikesCustomer.this, "Upload Bicycle successfully", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            });
                            progressDialog.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(ShareBikesCustomer.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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

    public void alertDialogBikeShareCond(){
        androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(this);
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

    @Override
    public void onStart() {
        super.onStart();
        loadCustomerDetailsShareBikes();
    }

    public void loadCustomerDetailsShareBikes(){
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
                        tVShareBikes.setText("Welcome: "+custom_data.getfName_Customer()+" "+custom_data.getlName_Customer());
                        etFNameShareBike.setText(custom_data.getfName_Customer());
                        etLNameShareBike.setText(custom_data.getlName_Customer());
                        etPNoShareBike.setText(custom_data.getPhoneNumb_Customer());
                        etEmailShareBike.setText(custom_data.getEmail_Customer());
                        customId_ShareBikes = custom_Details.getUid();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ShareBikesCustomer.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private static final String[] bikeShareCondition = new String[]{"Brand New", "Used Bike"};
}
