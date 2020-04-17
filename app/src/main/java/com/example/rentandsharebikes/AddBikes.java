package com.example.rentandsharebikes;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class AddBikes extends AppCompatActivity {

    static final int REQUEST_IMAGE_GET = 2;
    private static final int IMAGE_CAPTURE_CODE = 1001;
    private static final int PERMISSION_CODE = 1000;

    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private StorageTask mUploadTask;

    private ImageView ivAddBike;
    private Uri imageUri;

    private EditText etBikeModel, etBikeManufact, etBikePrice;
    private TextView textViewWelcomeAddBikes, textViewDate, textViewBikeNumber;
    private Button buttonSaveBike;
    private ImageButton buttonTakePicture;

    private String tvBike_Date, etBike_Model, etBike_Manufact;
    private int etBike_Price;
    public int tvBike_Number;

    String bikeStore_Name = "";
    String bikeStore_Key = "";

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


        textViewWelcomeAddBikes = (TextView) findViewById(R.id.tvWelcomeAddBikes);
        textViewDate = (TextView) findViewById(R.id.tvBikeDate);
        //textViewBikeNumber = (TextView) findViewById(R.id.tvBikeNumber);
        textViewWelcomeAddBikes.setText("Add Bicycles to " + bikeStore_Name + " store");

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
        textViewDate.setText(insertDate);

        etBikeModel = (EditText) findViewById(R.id.etBikeModel);
        etBikeManufact = (EditText) findViewById(R.id.etBikeManufacturer);
        etBikePrice = (EditText) findViewById(R.id.etBikePricePerDay);

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
                //show progress Dialog
                progressDialog.show();
                if (mUploadTask != null && mUploadTask.isInProgress()) {
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

        tvBike_Date = textViewDate.getText().toString().trim();
        etBike_Model = etBikeModel.getText().toString().trim();
        etBike_Manufact = etBikeManufact.getText().toString().trim();
        etBike_Price = Integer.parseInt(etBikePrice.getText().toString().trim());

        if (imageUri == null) {
            Toast.makeText(AddBikes.this, "Please add a picture", Toast.LENGTH_SHORT).show();
        } else if (etBike_Model.isEmpty()) {
            etBikeModel.setError("Please add the Model of Bicycle");
            etBikeModel.requestFocus();
        } else if (TextUtils.isEmpty(etBike_Manufact)) {
            etBikeManufact.setError("Please add the Manufacturer");
            etBikeManufact.requestFocus();
        } else if (TextUtils.isEmpty(String.valueOf(etBike_Price))) {
            etBikePrice.setError("Please add the Price/Day ");
            etBikePrice.requestFocus();
        }

        //Add a new Bike into the Bike's table
        else {
            progressDialog.setTitle("The Bike is Uploading");
            progressDialog.show();
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            mUploadTask = fileReference.putFile(imageUri)
            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            Bikes bikes = new Bikes(tvBike_Date, tvBike_Number, etBike_Model, etBike_Manufact, etBike_Price, uri.toString(), bikeStore_Key);
                            String addBike_id = databaseReference.push().getKey();
                            assert addBike_id != null;
                            databaseReference.child(addBike_id).setValue(bikes);

                            etBikeModel.setText("");
                            etBikeManufact.setText("");
                            etBikePrice.setText("");
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
                    progressDialog.setMessage("Uploaded " + (int) progress + "%");
                    progressDialog.setProgress((int) progress);
                }
            });
        }
    }


}
