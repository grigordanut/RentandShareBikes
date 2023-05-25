package com.example.rentandsharebikes;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.util.Objects;

public class AddBikeRent extends AppCompatActivity {

    private static final String[] bikeCondition = new String[]{"Brand New", "Used Bike"};

    private static final int PICK_PICTURE = 100;
    private static final int TAKE_PICTURE = 101;

    private static final int CAPTURE_CAMERA = 1001;
    private static final int PERMISSION_CAMERA = 1000;

    //Declare to Bike database variables (Upload data)
    private StorageReference stRefBikeUpload;
    private DatabaseReference dbRefBikeUpload;
    private StorageTask stTaskBikeUpload;

    private ImageView ivAddBike;
    private Uri imageUri;

    private EditText eTBikeModel, eTBikeManufact, eTBikePrice;
    private TextView tViewWelcomeAddBikes;
    private AutoCompleteTextView tVBikeCondition;
    private Button buttonSaveBike;
    private ImageButton buttonTakePicture;

    private String bike_Condition, bike_Model, bike_Manufact;
    private double bike_Price;

    private String store_Name = "";
    private String store_Key = "";

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bike_rent);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Add bikes to Bike Store");

        //Create to Bikes table into database
        stRefBikeUpload = FirebaseStorage.getInstance().getReference("Bikes");
        dbRefBikeUpload = FirebaseDatabase.getInstance().getReference("Bikes");

        progressDialog = new ProgressDialog(AddBikeRent.this);

        getIntent().hasExtra("SName");
        store_Name = Objects.requireNonNull(getIntent().getExtras()).getString("SName");

        getIntent().hasExtra("SKey");
        store_Key = Objects.requireNonNull(getIntent().getExtras()).getString("SKey");

        tViewWelcomeAddBikes = findViewById(R.id.tvWelcomeAddBikes);
        tViewWelcomeAddBikes.setText("Add Bicycles to " + store_Name + " store");

        tVBikeCondition = findViewById(R.id.tvBikeCondition);

        ArrayAdapter<String> conditionAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, bikeCondition);
        tVBikeCondition.setAdapter(conditionAdapter);

        eTBikeModel = findViewById(R.id.etBikeModel);
        eTBikeManufact = findViewById(R.id.etBikeManufacturer);
        eTBikePrice = findViewById(R.id.etBikePricePerDay);

        buttonTakePicture = findViewById(R.id.btnTakePicture);
        ivAddBike = findViewById(R.id.imgViewAddBikes);

        ivAddBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pick_Photo = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pick_Photo, PICK_PICTURE);
            }
        });

        buttonTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.CAMERA) ==
                        PackageManager.PERMISSION_DENIED ||
                        checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                                PackageManager.PERMISSION_DENIED) {
                    String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    requestPermissions(permission, TAKE_PICTURE);
                } else {
                    openCamera();
                }
            }
        });

        //Action button Save Bike
        buttonSaveBike = findViewById(R.id.btnSaveBike);
        buttonSaveBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (stTaskBikeUpload != null && stTaskBikeUpload.isInProgress()) {
                    Toast.makeText(AddBikeRent.this, "Upload in progress", Toast.LENGTH_SHORT).show();
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
        startActivityForResult(cameraIntent, CAPTURE_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CAMERA) {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PICK_PICTURE:
                if (resultCode == RESULT_OK) {
                    imageUri = data.getData();
                    ivAddBike.setImageURI(imageUri);
                }
                break;
            case TAKE_PICTURE:
                if (resultCode == RESULT_OK) {
                    ivAddBike.setImageURI(imageUri);
                }
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    //Upload bike data into the Bikes table
    public void uploadBikesDetails() {

        if (validateBikeDetails()) {

            //Read entered Bike data
            bike_Condition = tVBikeCondition.getText().toString().trim();
            bike_Model = eTBikeModel.getText().toString().trim();
            bike_Manufact = eTBikeManufact.getText().toString().trim();
            bike_Price = Double.parseDouble(eTBikePrice.getText().toString().trim());

            progressDialog.setTitle("The Bike is uploading!");
            progressDialog.show();
            final StorageReference fileReference = stRefBikeUpload.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            stTaskBikeUpload = fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            String bike_Key = dbRefBikeUpload.push().getKey();

                            Bikes bikes = new Bikes(bike_Condition, bike_Model, bike_Manufact, bike_Price,
                                    uri.toString(), store_Name, store_Key);

                            assert bike_Key != null;
                            dbRefBikeUpload.child(bike_Key).setValue(bikes);

                            eTBikeModel.setText("");
                            eTBikeManufact.setText("");
                            eTBikePrice.setText("");
                            ivAddBike.setImageResource(R.drawable.add_bikes_picture);

                            Intent add_Bikes = new Intent(AddBikeRent.this, AdminPage.class);
                            startActivity(add_Bikes);

                            Toast.makeText(AddBikeRent.this, "Upload Bicycle successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        });
                        progressDialog.dismiss();
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(AddBikeRent.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        //show upload progress
                        double progress = 100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount();
                        progressDialog.setMessage("Uploaded: " + (int) progress + "%");
                        progressDialog.setProgress((int) progress);
                    });
        }
    }

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
        alertDialogBuilder
                .setMessage("Select the Bike condition")
                .setPositiveButton("OK", (dialog, id) -> dialog.dismiss());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    //Notify Bike picture missing
    public void alertDialogBikePicture() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setTitle("No Bike picture")
                .setMessage("Please add a picture!")
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, id) -> dialog.dismiss());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
