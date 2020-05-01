package com.example.rentandsharebikes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Objects;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

public class UpdateBikeShareDetails extends AppCompatActivity {

    static final int REQUEST_IMAGE_GET = 2;
    private static final int IMAGE_CAPTURE_CODE = 1001;
    private static final int PERMISSION_CODE = 1000;

    private DatabaseReference databaseRefCustomUpdateShare;

    //    //Save bike details into Share Bikes able
//    private StorageReference storageRefUpShare;
//    private DatabaseReference databaseRefUpShare;
    private StorageTask shareUpTask;

    //Access Customer Database
    private FirebaseAuth firebaseAuth;
//    private DatabaseReference databaseRefCustomUpdateShare;

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
    private String etUpUpFName_ShareBike, etUpLName_ShareBike, etUpPNo_ShareBike, etUpEmail_ShareBike;

    //Bike details variables
    private String tVUpCond_ShareBike, etUpModel_ShareBike, etUpManufact_ShareBike, etUpDateAv_ShareBike;
    private double etUpPrice_ShareBike;
    private ImageView ivUpShareBike;
    private Uri imageUphareUri;

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
        setContentView(R.layout.activity_update_bike_share_details);

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(UpdateBikeShareDetails.this);

        //storageRefUpShare = FirebaseStorage.getInstance().getReference("Share Bikes");
        //databaseRefUpShare = getInstance().getReference("Share Bikes");

        tVUpShareBikes = (TextView)findViewById(R.id. tvUpShareBikes);
        etUpFNameShareBike = (EditText)findViewById(R.id.etUpShareBikeFName);
        etUpLNameShareBike = (EditText)findViewById(R.id.etUpShareBikeLName);
        etUpPNoShareBike = (EditText)findViewById(R.id.etUpShareBikePNumber);
        etUpEmailShareBike = (EditText)findViewById(R.id.etUpShareBikeEmail);
        ivUpShareBike = (ImageView)findViewById(R.id.imgViewUpShareBikes);
        tVUpCondShareBike = (AutoCompleteTextView) findViewById(R.id.tvUpShareBikeCond);

        imgArrCondUpShareBike = (ImageView)findViewById(R.id.imgArrowUpShareBikeCond);

        ArrayAdapter<String> conditionAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,bikeUpdateCondition);
        tVUpCondShareBike.setAdapter(conditionAdapter);

        imgArrCondUpShareBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tVUpCondShareBike.showDropDown();
            }
        });

        etUpModelShareBike = (EditText)findViewById(R.id.etUpShareBikeModel);
        etUpManufactShareBike = (EditText)findViewById(R.id.etUpShareBikeManufact);
        etUpPriceShareBike = (EditText)findViewById(R.id.etUpShareBikePriceDay);
        tVUpAvDateShareBike = (TextView)findViewById(R.id.tvUpShareBikeAvDate);
        etUpDateAvShareBike = (EditText)findViewById(R.id.etUpShareBikeAvDate);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            bike_shareUpCond = bundle.getString("BCondUpdate");
            bike_shareUpModel = bundle.getString("BModelUpdate");
            bike_shareUpManufact = bundle.getString("BManufUpdate");
            bike_shareUpPrice = bundle.getString("BPriceUpdate");
            bike_shareUpImage = bundle.getString("BImgUpdate");
            bike_shareUpDateAv = bundle.getString("BDateAvUpdate");
            customId_UpShareBikes = bundle.getString("CIdUpdate");
            bike_KeyUpShare = bundle.getString("BKeyUpdate");
        }

        tVUpCondShareBike.setText(bike_shareUpCond);
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

        //storageRefUpShare = FirebaseStorage.getInstance().getReference("Share Bikes");
        //databaseRefUpShare = getInstance().getReference("Share Bikes");

        tVUpAvDateShareBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectShareUpAvDate();
            }
        });

        ivUpShareBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteOldEventPicture();
                openGallery();
            }
        });

        buttonUpShareTPicture = (ImageButton) findViewById(R.id.btnUpShareTakePicture);
        buttonUpShareTPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteOldEventPicture();
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
                    Toast.makeText(UpdateBikeShareDetails.this, "Update bike in progress", Toast.LENGTH_SHORT).show();
                } else {
                    //uploadUpShareBikes();
                }
            }
        });
    }

    public void deleteOldEventPicture(){
        progressDialog.show();

        StorageReference stRefDeleteShareBike = getInstance().getReferenceFromUrl(bike_shareUpImage);
        stRefDeleteShareBike.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(UpdateBikeShareDetails.this, "Previous image deleted", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateBikeShareDetails.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    private void selectShareUpAvDate(){
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(UpdateBikeShareDetails.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date_year = String.valueOf(year);
                String date_month = (month+1) < 10 ? "0" + (month+1) : String.valueOf(month+1);
                String date_day = dayOfMonth < 10 ? "0" + dayOfMonth : String.valueOf(dayOfMonth);
                String picked_Date = date_day+"/"+date_month+"/"+date_year;
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
        imageUphareUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUphareUri);
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
            ivUpShareBike.setImageURI(imageUphareUri);
            Toast.makeText(getApplicationContext(), "Image captured by Camera", Toast.LENGTH_SHORT).show();
        }

        if (requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK) {
            try {
                imageUphareUri = data.getData();
                Bitmap thumbnail = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUphareUri);
                ivUpShareBike.setImageBitmap(thumbnail);
                Toast.makeText(UpdateBikeShareDetails.this, "Image picked from Gallery", Toast.LENGTH_SHORT).show();
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
//        progressDialog.dismiss();
//
//        final String etFName_ShareBikeVal = etFNameShareBike.getText().toString().trim();
//        final String etLName_ShareBikeVal = etLNameShareBike.getText().toString().trim();
//        final String etPNo_ShareBikeVal = etPNoShareBike.getText().toString().trim();
//        final String etEmail_ShareBikeVal = etEmailShareBike.getText().toString().trim();
//
//        final String tVCond_ShareBikeVal = tVCondShareBike.getText().toString().trim();
//        final String etModel_ShareBikeVal = etModelShareBike.getText().toString().trim();
//        final String etManufact_ShareBikeVal = etManufactShareBike.getText().toString().trim();
//        final String etPrice_ShareBikeVal = etPriceShareBike.getText().toString().trim();
//        final String etDateAv_ShareBikeVal = etDateAvShareBike.getText().toString().trim();
//
//        if(TextUtils.isEmpty(etFName_ShareBikeVal)){
//            etFNameShareBike.setError("Please enter the First Name");
//            etFNameShareBike.requestFocus();
//        }
//        if(TextUtils.isEmpty(etLName_ShareBikeVal)){
//            etLNameShareBike.setError("Please enter the Last Name");
//            etLNameShareBike.requestFocus();
//        }
//        if(TextUtils.isEmpty(etPNo_ShareBikeVal)){
//            etPNoShareBike.setError("Please enter the Phone Number");
//            etPNoShareBike.requestFocus();
//        }
//        if(TextUtils.isEmpty(etEmail_ShareBikeVal)){
//            etEmailShareBike.setError("Please enter the Email Address");
//            etEmailShareBike.requestFocus();
//        }
//        else if (imageShareUri == null) {
//            Toast.makeText(ShareBikesCustomer.this, "Please add a picture", Toast.LENGTH_SHORT).show();
//        }
//        else if(TextUtils.isEmpty(tVCond_ShareBikeVal)){
//            tVCondShareBike.setError("Please select Bike Condition");
//            tVCondShareBike.requestFocus();
//        }
//        else if (TextUtils.isEmpty(etModel_ShareBikeVal)) {
//            etModelShareBike.setError("Please add the Model of Bicycle");
//            etModelShareBike.requestFocus();
//        }
//        else if (TextUtils.isEmpty(etManufact_ShareBikeVal)) {
//            etManufactShareBike.setError("Please add the Manufacturer");
//            etManufactShareBike.requestFocus();
//        }
//        else if (TextUtils.isEmpty(etPrice_ShareBikeVal)) {
//            etPriceShareBike.setError("Please add the Price/Day ");
//            etPriceShareBike.requestFocus();
//        }
//
//        else if(TextUtils.isEmpty(etDateAv_ShareBikeVal)){
//            alertDialogAvailableDateEmpty();
//        }
//
//        //Add a new Bike into the Bike's table
//        else {
//            etFName_ShareBike = etFNameShareBike.getText().toString().trim();
//            etLName_ShareBike = etLNameShareBike.getText().toString().trim();
//            etPNo_ShareBike = etPNoShareBike.getText().toString().trim();
//            etEmail_ShareBike = etEmailShareBike.getText().toString().trim();
//
//            tVCond_ShareBike = tVCondShareBike.getText().toString().trim();
//            etModel_ShareBike = etModelShareBike.getText().toString().trim();
//            etManufact_ShareBike = etManufactShareBike.getText().toString().trim();
//            etPrice_ShareBike = Double.parseDouble(etPriceShareBike.getText().toString().trim());
//            etDateAv_ShareBike = etDateAvShareBike.getText().toString().trim();
//
//            progressDialog.setTitle("The Bike is Uploading");
//            progressDialog.show();
//            final StorageReference fileReference = storageRefShareBikes.child(System.currentTimeMillis() + "." + getFileExtension(imageShareUri));
//            shareUploadTask = fileReference.putFile(imageShareUri)
//                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                @Override
//                                public void onSuccess(Uri uri) {
//                                    bike_KeyShare = databaseRefShareBikes.push().getKey();
//
//                                    ShareBikes rent_Bikes = new ShareBikes(etFName_ShareBike, etLName_ShareBike, etPNo_ShareBike, etEmail_ShareBike,
//                                            tVCond_ShareBike, etModel_ShareBike, etManufact_ShareBike, etPrice_ShareBike, etDateAv_ShareBike, uri.toString(), customId_ShareBikes, bike_KeyShare);
//
//                                    assert bike_KeyShare != null;
//                                    databaseRefShareBikes.child(bike_KeyShare).setValue(rent_Bikes);
//
//                                    etFNameShareBike.setText("");
//                                    etLNameShareBike.setText("");
//                                    etPNoShareBike.setText("");
//                                    etEmailShareBike.setText("");
//                                    tVCondShareBike.setText("");
//                                    etModelShareBike.setText("");
//                                    etManufactShareBike.setText("");
//                                    etPriceShareBike.setText("");
//                                    etDateAvShareBike.setText("");
//                                    ivShareBike.setImageResource(R.drawable.add_bikes_picture);
//
//                                    Intent add_Bikes = new Intent(UpdateBikeSharedDetails.this, CustomerPageRentBikes.class);
//                                    startActivity(add_Bikes);
//
//                                    Toast.makeText(UpdateBikeSharedDetails.this, "Upload Bicycle successfully", Toast.LENGTH_SHORT).show();
//                                    finish();
//                                }
//                            });
//                            progressDialog.dismiss();
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            progressDialog.dismiss();
//                            Toast.makeText(UpdateBikeSharedDetails.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    })
//                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
//                            //show upload Progress
//                            double progress = 100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount();
//                            progressDialog.setMessage("Uploaded: " + (int) progress + "%");
//                            progressDialog.setProgress((int) progress);
//                        }
//                    });
//        }
    }


    private static final String[] bikeUpdateCondition = new String[]{"Brand New", "Used Bike"};

    @Override
    public void onStart() {
        super.onStart();
        loadCustomDetailsUpShareBikes();
    }

    public void loadCustomDetailsUpShareBikes(){
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
                    if (Objects.requireNonNull(custom_Details.getEmail()).equalsIgnoreCase(custom_data.getEmail_Customer())){
                        tVUpShareBikes.setText("Welcome: "+custom_data.getfName_Customer()+" "+custom_data.getlName_Customer());
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
                Toast.makeText(UpdateBikeShareDetails.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
