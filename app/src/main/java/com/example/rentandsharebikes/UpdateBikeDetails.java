package com.example.rentandsharebikes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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

import static com.google.firebase.storage.FirebaseStorage.getInstance;

public class UpdateBikeDetails extends AppCompatActivity {

    private static final String[] updateBikeCondition = new String[]{"Brand New", "Used Bike"};

    private static final int REQUEST_IMAGE_GET = 2;
    private static final int IMAGE_CAPTURE_CODE = 1001;
    private static final int PERMISSION_CODE = 1000;

    private StorageReference storageRefUpdate;
    private DatabaseReference databaseRefUpdate;
    private StorageTask updateBikeTaskUp;

    private ArrayAdapter<String> updateArrayAdapter;

    private ImageView ivUpdateBike;
    private Uri imageUriUp;

    private EditText etUpBikeModel, etUpBikeManufact, etUpBikePrice;
    private TextView tViewUpBikes;
    private AutoCompleteTextView tViewUpBikeCond;

    private Button btn_SaveBikeUpdated;
    private ImageButton btn_UpTakePicture;

    private String etUpBike_Cond, etUpBike_Model, etUpBike_Manufact;
    private double etUpBike_Price;

    String bike_updateCond = "";
    String bike_updateModel = "";
    String bike_updateManufact = "";
    String bike_updatePrice = "";
    String bike_updateImage = "";
    String bike_KeyUp = "";

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_bike_details);


        storageRefUpdate = FirebaseStorage.getInstance().getReference("Bikes");
        databaseRefUpdate = FirebaseDatabase.getInstance().getReference("Bikes");

        progressDialog = new ProgressDialog(UpdateBikeDetails.this);

        //initialise variables
        tViewUpBikes = findViewById(R.id.tvUpBikes);
        tViewUpBikeCond = findViewById(R.id.tvUpBikeCond);

        updateArrayAdapter = new ArrayAdapter<>(UpdateBikeDetails.this, android.R.layout.select_dialog_item, updateBikeCondition);
        tViewUpBikeCond.setAdapter(updateArrayAdapter);

        etUpBikeModel = findViewById(R.id.etBikeModelUp);
        etUpBikeManufact = findViewById(R.id.etBikeManufacturerUp);
        etUpBikePrice = findViewById(R.id.etBikePricePerDayUp);
        ivUpdateBike = findViewById(R.id.imgViewUpBikes);

        btn_UpTakePicture = findViewById(R.id.btnTakePictureUpBikes);
        btn_SaveBikeUpdated = findViewById(R.id.btnSaveBikeUp);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            bike_updateCond = bundle.getString("BCondition");
            bike_updateModel = bundle.getString("BModel");
            bike_updateManufact = bundle.getString("BManufact");
            bike_updatePrice = bundle.getString("BPrice");
            bike_updateImage = bundle.getString("BImage");
            bike_KeyUp = bundle.getString("BKey");
        }

        //receive data from the other activity
        Picasso.get()
                .load(bike_updateImage)
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(ivUpdateBike);

        tViewUpBikes.setText("Update " + bike_updateModel + " bike");
        etUpBikeModel.setText(bike_updateModel);
        etUpBikeManufact.setText(bike_updateManufact);
        etUpBikePrice.setText(String.valueOf(bike_updatePrice));

        ivUpdateBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        btn_UpTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.CAMERA) ==
                        PackageManager.PERMISSION_DENIED ||
                        checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                                PackageManager.PERMISSION_DENIED) {
                    String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    requestPermissions(permission, PERMISSION_CODE);
                } else {
                    openCamera();
                }
            }
        });

        btn_SaveBikeUpdated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (updateBikeTaskUp != null && updateBikeTaskUp.isInProgress()) {
                    Toast.makeText(UpdateBikeDetails.this, "Update bike in progress", Toast.LENGTH_SHORT).show();
                } else {
                    if (imageUriUp == null) {
                        alertDialogBikePictureNew();
                    } else {
                        updateBikesWithNewPicture();
                    }
                }
            }
        });
    }

    public void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
        imageUriUp = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUriUp);
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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
        if (resultCode == RESULT_OK) {
            ivUpdateBike.setImageURI(imageUriUp);
            Toast.makeText(getApplicationContext(), "Image captured by Camera", Toast.LENGTH_SHORT).show();
        }

        if (requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK) {
            try {
                imageUriUp = data.getData();
                Bitmap thumbnail = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUriUp);
                ivUpdateBike.setImageBitmap(thumbnail);
                Toast.makeText(UpdateBikeDetails.this, "Image picked from Gallery", Toast.LENGTH_SHORT).show();
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

    private void deleteOldBikePicture() {

        StorageReference storageRefDelete = getInstance().getReferenceFromUrl(bike_updateImage);
        storageRefDelete.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(UpdateBikeDetails.this, "Previous image deleted", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateBikeDetails.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Upload the updated Bike into the Bike table
    public void updateBikesWithNewPicture() {
        progressDialog.dismiss();

        if (validateUpdateBikeDetails()) {

            etUpBike_Cond = tViewUpBikeCond.getText().toString().trim();
            etUpBike_Model = etUpBikeModel.getText().toString().trim();
            etUpBike_Manufact = etUpBikeManufact.getText().toString().trim();
            etUpBike_Price = Double.parseDouble(etUpBikePrice.getText().toString().trim());

            progressDialog.setTitle("The Bike is updating");
            progressDialog.show();

            final StorageReference fileReference = storageRefUpdate.child(System.currentTimeMillis() + "." + getFileExtension(imageUriUp));
            updateBikeTaskUp = fileReference.putFile(imageUriUp)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(final Uri uri) {

                                    Query query = databaseRefUpdate.orderByChild("bike_Key").equalTo(bike_KeyUp);
                                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                ds.getRef().child("bike_Condition").setValue(etUpBike_Cond);
                                                ds.getRef().child("bike_Model").setValue(etUpBike_Model);
                                                ds.getRef().child("bike_Manufacturer").setValue(etUpBike_Manufact);
                                                ds.getRef().child("bike_Price").setValue(etUpBike_Price);
                                                ds.getRef().child("bike_Image").setValue(uri.toString());
                                            }

                                            deleteOldBikePicture();
                                            progressDialog.dismiss();
                                            Toast.makeText(UpdateBikeDetails.this, "The Bike will be updated", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(UpdateBikeDetails.this, AdminPage.class));
                                            finish();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            Toast.makeText(UpdateBikeDetails.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(UpdateBikeDetails.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            //show upload Progress
                            double progress = 100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage("Updated: " + (int) progress + "%");
                            progressDialog.setProgress((int) progress);
                        }
                    });
        }
    }

    private void uploadBikesWithOldPicture() {
        progressDialog.dismiss();

        if (validateUpdateBikeDetails()) {

            //Add a new Bike into the Bike's table
            etUpBike_Cond = tViewUpBikeCond.getText().toString().trim();
            etUpBike_Model = etUpBikeModel.getText().toString().trim();
            etUpBike_Manufact = etUpBikeManufact.getText().toString().trim();
            etUpBike_Price = Double.parseDouble(etUpBikePrice.getText().toString().trim());

            progressDialog.setMessage("The Bike is updating");
            progressDialog.show();

            Query query = databaseRefUpdate.orderByChild("bike_Key").equalTo(bike_KeyUp);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        ds.getRef().child("bike_Condition").setValue(etUpBike_Cond);
                        ds.getRef().child("bike_Model").setValue(etUpBike_Model);
                        ds.getRef().child("bike_Manufacturer").setValue(etUpBike_Manufact);
                        ds.getRef().child("bike_Price").setValue(etUpBike_Price);
                    }
                    progressDialog.dismiss();
                    Toast.makeText(UpdateBikeDetails.this, "The Bike will be updated", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(UpdateBikeDetails.this, AdminPage.class));
                    finish();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(UpdateBikeDetails.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            progressDialog.dismiss();
        }
    }

    public boolean validateUpdateBikeDetails() {

        boolean result = false;
        final String upBike_ConditionVal = tViewUpBikeCond.getText().toString().trim();
        final String upBike_ModelVal = etUpBikeModel.getText().toString().trim();
        final String upBike_ManufactVal = etUpBikeManufact.getText().toString().trim();
        final String upBike_PriceVal = etUpBikePrice.getText().toString().trim();

        if (TextUtils.isEmpty(upBike_ConditionVal)) {
            alertDialogBikeCond();
            tViewUpBikeCond.requestFocus();
        } else if (TextUtils.isEmpty(upBike_ModelVal)) {
            etUpBikeModel.setError("Please add the Model of Bicycle");
            etUpBikeModel.requestFocus();
        } else if (TextUtils.isEmpty(upBike_ManufactVal)) {
            etUpBikeManufact.setError("Please add the Manufacturer");
            etUpBikeManufact.requestFocus();
        } else if (TextUtils.isEmpty(upBike_PriceVal)) {
            etUpBikePrice.setError("Please add the Price/Day ");
            etUpBikePrice.requestFocus();
        } else {
            result = true;
        }

        return result;
    }

    public void alertDialogBikeCond() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setMessage("Select the Bike Condition")
                .setPositiveButton("OK",
                (arg0, arg1) -> {
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void alertDialogBikePictureNew() {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder
                .setTitle("No Bike picture changed.")
                .setMessage("Update the Bike with old picture.")
                .setPositiveButton("YES",
                        (arg0, arg1) -> uploadBikesWithOldPicture())
                .setNegativeButton("CANCEL", (dialog, i) -> dialog.dismiss());

        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
