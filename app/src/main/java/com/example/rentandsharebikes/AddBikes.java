package com.example.rentandsharebikes;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

public class AddBikes extends AppCompatActivity {

    private static final int REQUEST_IMAGE_GET = 2;
    private static final int IMAGE_CAPTURE_CODE = 1001;
    private static final int PERMISSION_CODE = 1000;

    //Declare to Bike database variables (Upload data)
    private StorageReference stRefBikeUpload;
    private DatabaseReference dbRefBikeUpload;
    private StorageTask stTaskBikeUpload;

    private ImageView ivAddBike;
    private Uri imageUri;

    private EditText eTBikeModel, eTBikeManufact, eTBikePrice;
    private TextView tViewWelcomeAddBikes;
    private AutoCompleteTextView tVBikeCondition;
    private ImageView imgArrowBikeCondition;
    private Button buttonSaveBike;
    private ImageButton buttonTakePicture;

    private String bike_Condition, bike_Model, bike_Manufact;
    private double bike_Price;

    private String store_Name = "";
    private String store_Key = "";
    private String bike_Key = "";

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bikes);

        //Create to Bikes table into database
        stRefBikeUpload = FirebaseStorage.getInstance().getReference("Bikes");
        dbRefBikeUpload = FirebaseDatabase.getInstance().getReference("Bikes");

        progressDialog = new ProgressDialog(AddBikes.this);

        getIntent().hasExtra("SName");
        store_Name = Objects.requireNonNull(getIntent().getExtras()).getString("SName");

        getIntent().hasExtra("SKey");
        store_Key = Objects.requireNonNull(getIntent().getExtras()).getString("SKey");

        tViewWelcomeAddBikes = (TextView) findViewById(R.id.tvWelcomeAddBikes);
        tViewWelcomeAddBikes.setText("Add Bicycles to " + store_Name + " store");

        tVBikeCondition = (AutoCompleteTextView) findViewById(R.id.tvBikeCondition);
        imgArrowBikeCondition = (ImageView) findViewById(R.id.imgBikeCondition);

        ArrayAdapter<String> conditionAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, bikeCondition);
        tVBikeCondition.setAdapter(conditionAdapter);

        imgArrowBikeCondition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tVBikeCondition.showDropDown();
            }
        });

        eTBikeModel = (EditText) findViewById(R.id.etBikeModel);
        eTBikeManufact = (EditText) findViewById(R.id.etBikeManufacturer);
        eTBikePrice = (EditText) findViewById(R.id.etBikePricePerDay);

        ivAddBike = (ImageView) findViewById(R.id.imgViewAddBikes);
        ivAddBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        buttonTakePicture = (ImageButton) findViewById(R.id.btnTakePicture);
        buttonTakePicture.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ObsoleteSdkInt")
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

        //Action button Save Bike
        buttonSaveBike = (Button) findViewById(R.id.btnSaveBike);
        buttonSaveBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (stTaskBikeUpload != null && stTaskBikeUpload.isInProgress()) {
                    Toast.makeText(AddBikes.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadBikesDetails();
                }
            }
        });
    }

    public void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
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
        if (requestCode == PERMISSION_CODE) {// If request is cancelled, the result arrays are empty.
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

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK) {
            try {
                imageUri = data.getData();
                Bitmap thumbnail = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                ivAddBike.setImageBitmap(thumbnail);
                Toast.makeText(AddBikes.this, "Image picked from Gallery", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        else if (resultCode == RESULT_OK) {
            ivAddBike.setImageURI(imageUri);
            Toast.makeText(getApplicationContext(), "Image captured by Camera", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    //Upload bike data into the Bikes table
    public void uploadBikesDetails() {
        progressDialog.dismiss();

        if (validateBikeDetails()) {

            //Read entered Bike data
            bike_Condition = tVBikeCondition.getText().toString().trim();
            bike_Model = eTBikeModel.getText().toString().trim();
            bike_Manufact = eTBikeManufact.getText().toString().trim();
            bike_Price = Double.parseDouble(eTBikePrice.getText().toString().trim());

            progressDialog.setTitle("The Bike is uploading");
            progressDialog.show();
            final StorageReference fileReference = stRefBikeUpload.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            stTaskBikeUpload = fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String addBike_id = dbRefBikeUpload.push().getKey();
                                    bike_Key = addBike_id;

                                    Bikes bikes = new Bikes(bike_Condition, bike_Model, bike_Manufact, bike_Price,
                                            uri.toString(), store_Name, store_Key, bike_Key);

                                    assert addBike_id != null;
                                    dbRefBikeUpload.child(addBike_id).setValue(bikes);

                                    eTBikeModel.setText("");
                                    eTBikeManufact.setText("");
                                    eTBikePrice.setText("");
                                    ivAddBike.setImageResource(R.drawable.add_bikes_picture);

                                    Intent add_Bikes = new Intent(AddBikes.this, AdminPage.class);
                                    startActivity(add_Bikes);

                                    Toast.makeText(AddBikes.this, "Upload Bicycle successfully", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(AddBikes.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            //show upload progress
                            double progress = 100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage("Uploaded: " + (int) progress + "%");
                            progressDialog.setProgress((int) progress);
                        }
                    });
        }
    }

    private static final String[] bikeCondition = new String[]{"Brand New", "Used Bike"};

    //Validate Bike data
    private Boolean validateBikeDetails() {
        boolean result = false;

        //Read entered Bike data
        final String bike_ConditionVal = tVBikeCondition.getText().toString().trim();
        final String bike_ModelVal = eTBikeModel.getText().toString().trim();
        final String bike_ManufactVal = eTBikeManufact.getText().toString().trim();
        final String bike_PriceVal = eTBikePrice.getText().toString().trim();

        //Validate Bike details
        if (imageUri == null) {
            alertDialogBikePicture();
        } else if (TextUtils.isEmpty(bike_ConditionVal)) {
            alertDialogBikeCond();
            tVBikeCondition.requestFocus();
        } else if (TextUtils.isEmpty(bike_ModelVal)) {
            eTBikeModel.setError("Please add the Model of Bicycle");
            eTBikeModel.requestFocus();
        } else if (TextUtils.isEmpty(bike_ManufactVal)) {
            eTBikeManufact.setError("Please add the Manufacturer");
            eTBikeManufact.requestFocus();
        } else if (TextUtils.isEmpty(bike_PriceVal)) {
            eTBikePrice.setError("Please add the Price/Hour");
            eTBikePrice.requestFocus();
        } else {
            result = true;
        }
        return result;
    }

    //Notify Bike condition missing
    public void alertDialogBikeCond() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Select the Bike condition");
        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    //Notify Bike picture missing
    public void alertDialogBikePicture() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Please add a picture");
        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
