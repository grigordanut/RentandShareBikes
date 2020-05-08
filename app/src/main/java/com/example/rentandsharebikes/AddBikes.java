package com.example.rentandsharebikes;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
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

    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private StorageTask bikesUploadTask;

    private ImageView ivAddBike;
    private Uri imageUri;

    private EditText eTextBikeModel, eTextBikeManufact, eTextBikePrice;
    private TextView tViewWelcomeAddBikes;
    private AutoCompleteTextView tViewBikeCondition;
    private ImageView imgArrowBikeCondition;
    private Button buttonSaveBike;
    private ImageButton buttonTakePicture;

    private String eTextBike_Condition, eTextBike_Model, eTextBike_Manufact;
    private double eTextBike_Price;

    String bikeStore_Name = "";
    String bikeStore_Key = "";
    String bike_Key = "";

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bikes);

        getIntent().hasExtra("SName");
        bikeStore_Name = Objects.requireNonNull(getIntent().getExtras()).getString("SName");

        getIntent().hasExtra("SKey");
        bikeStore_Key = Objects.requireNonNull(getIntent().getExtras()).getString("SKey");

        tViewWelcomeAddBikes = (TextView) findViewById(R.id.tvWelcomeAddBikes);
        tViewWelcomeAddBikes.setText("Add Bicycles to " + bikeStore_Name + " store");

        tViewBikeCondition = (AutoCompleteTextView)findViewById(R.id.tvBikeCondition);
        imgArrowBikeCondition = (ImageView)findViewById(R.id.imgBikeCondition);

        ArrayAdapter<String> conditionAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,bikeCondition);
        tViewBikeCondition.setAdapter(conditionAdapter);

        imgArrowBikeCondition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tViewBikeCondition.showDropDown();
            }
        });

        eTextBikeModel = (EditText) findViewById(R.id.etBikeModel);
        eTextBikeManufact = (EditText) findViewById(R.id.etBikeManufacturer);
        eTextBikePrice = (EditText) findViewById(R.id.etBikePricePerDay);

        storageReference = FirebaseStorage.getInstance().getReference("Bikes");
        databaseReference = FirebaseDatabase.getInstance().getReference("Bikes");

        progressDialog = new ProgressDialog(AddBikes.this);

        ivAddBike = (ImageView) findViewById(R.id.imgViewAddBikes);
        ivAddBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        buttonTakePicture = (ImageButton) findViewById(R.id.btnTakePicture);
        buttonTakePicture.setOnClickListener(new View.OnClickListener() {
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
                progressDialog.show();
                if (bikesUploadTask != null && bikesUploadTask.isInProgress()) {
                    Toast.makeText(AddBikes.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadBikes();
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
            ivAddBike.setImageURI(imageUri);
            Toast.makeText(getApplicationContext(), "Image captured by Camera", Toast.LENGTH_SHORT).show();
        }

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
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    //Upload a new Bicycle into the Bicycles table
    public void uploadBikes() {
        progressDialog.dismiss();

        final String tv_BikeConditionValidation = tViewBikeCondition.getText().toString().trim();
        final String etBike_ModelValidation = eTextBikeModel.getText().toString().trim();
        final String etBike_ManufactValidation = eTextBikeManufact.getText().toString().trim();
        final String etBike_PriceValidation = eTextBikePrice.getText().toString().trim();

        if (imageUri == null) {
            alertDialogBikePicture();
        }
        else if(TextUtils.isEmpty(tv_BikeConditionValidation)){
            alertDialogBikeCond();
            tViewBikeCondition.requestFocus();
        }
        else if (TextUtils.isEmpty(etBike_ModelValidation)) {
            eTextBikeModel.setError("Please add the Model of Bicycle");
            eTextBikeModel.requestFocus();
        }
        else if (TextUtils.isEmpty(etBike_ManufactValidation)) {
            eTextBikeManufact.setError("Please add the Manufacturer");
            eTextBikeManufact.requestFocus();
        }
        else if (TextUtils.isEmpty(etBike_PriceValidation)) {
            eTextBikePrice.setError("Please add the Price/Hour");
            eTextBikePrice.requestFocus();
        }

        //Add a new Bike into the Bike's table
        else {

            eTextBike_Condition = tViewBikeCondition.getText().toString().trim();
            eTextBike_Model = eTextBikeModel.getText().toString().trim();
            eTextBike_Manufact = eTextBikeManufact.getText().toString().trim();
            eTextBike_Price = Double.parseDouble(eTextBikePrice.getText().toString().trim());

            progressDialog.setTitle("The Bike is Uploading");
            progressDialog.show();
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            bikesUploadTask = fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String addBike_id = databaseReference.push().getKey();
                                    bike_Key = addBike_id;

                                    Bikes bikes = new Bikes(eTextBike_Condition, eTextBike_Model, eTextBike_Manufact, eTextBike_Price, uri.toString(), bikeStore_Name, bikeStore_Key,  bike_Key);

                                    assert addBike_id != null;
                                    databaseReference.child(addBike_id).setValue(bikes);

                                    eTextBikeModel.setText("");
                                    eTextBikeManufact.setText("");
                                    eTextBikePrice.setText("");
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
                            //show upload Progress
                            double progress = 100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage("Uploaded: " + (int) progress + "%");
                            progressDialog.setProgress((int) progress);
                        }
                    });
        }
    }

    private static final String[] bikeCondition = new String[]{"Brand New", "Used Bike"};

    public void alertDialogBikeCond(){
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

    public void alertDialogBikePicture(){
        androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Please add a picture");
        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });

        androidx.appcompat.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
