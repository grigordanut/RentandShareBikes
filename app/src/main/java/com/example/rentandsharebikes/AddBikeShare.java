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
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.Objects;

public class AddBikeShare extends AppCompatActivity {

    private static final String[] conditionBikeShare = new String[]{"Brand New", "Used Bike"};

    private static final int PICK_PICTURE = 100;
    private static final int TAKE_PICTURE = 101;

    private static final int CAPTURE_CAMERA = 1001;
    private static final int PERMISSION_CAMERA = 1000;

    //Save bike details into Share Bikes able
    private StorageReference storageRefShareBikes;
    private DatabaseReference databaseRefShareBikes;
    private StorageTask shareUploadTask;

    //Access Customer Database
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseRefCustomer;

    //Customer Details
    private TextInputEditText eTFNameBikeShare, eTLNameBikeShare, eTPNoBikeShare, eTEmailBikeShare;
    //Bike Details
    private TextInputEditText eTModelBikeShare, eTManufactBikeShare, eTPricePerDayBikeShare;

    private EditText eTDateAvBikeShare;
    private TextView tVShareBikes, tVDateAvBikeShare;
    private AutoCompleteTextView tVCondBikeShare;

    private ImageButton btn_TakePictureShare;
    private Button btn_SaveBikeShare;

    //Customer details variables
    private String fName_BikeShare, lName_BikeShare, pNo_BikeShare, email_BikeShare;

    //Bike details variables
    private String cond_BikeShare, model_BikeShare, manufact_BikeShare, dateAv_BikeShare;
    private double price_BikeShare;
    private ImageView ivBikeShare;
    private Uri uriBikeShare;

    String bike_KeyShare = "";
    String customId_BikeShare = "";

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bike_share);

        progressDialog = new ProgressDialog(AddBikeShare.this);

        firebaseAuth = FirebaseAuth.getInstance();

        //retrieve data from database into text views
        databaseRefCustomer = FirebaseDatabase.getInstance().getReference("Customers");

        storageRefShareBikes = FirebaseStorage.getInstance().getReference("Share Bikes");
        databaseRefShareBikes = FirebaseDatabase.getInstance().getReference("Share Bikes");

        tVShareBikes = findViewById(R.id.tvShareBikes);

        eTFNameBikeShare = findViewById(R.id.etFNameBikeShare);
        eTLNameBikeShare = findViewById(R.id.etLNameBikeShare);
        eTPNoBikeShare = findViewById(R.id.etPNoBikeShare);
        eTEmailBikeShare = findViewById(R.id.etEmailBikeShare);
        eTEmailBikeShare.setEnabled(false);

        ivBikeShare = findViewById(R.id.imgViewBikeShare);
        btn_TakePictureShare = findViewById(R.id.btnTakePictureShare);

        tVCondBikeShare = findViewById(R.id.tvCondBikeShare);

        eTModelBikeShare = findViewById(R.id.etModelBikeShare);
        eTManufactBikeShare = findViewById(R.id.etManufactBikeShare);
        eTPricePerDayBikeShare = findViewById(R.id.etPricePerDayBikeShare);

        tVDateAvBikeShare = findViewById(R.id.tvDateAvBikeShare);
        eTDateAvBikeShare = findViewById(R.id.etDateAvBikeShare);
        eTDateAvBikeShare.setEnabled(false);

        //Action button Save share Bike
        btn_SaveBikeShare = findViewById(R.id.btnSaveBikeShare);

        ivBikeShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pick_PhotoShare = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pick_PhotoShare, PICK_PICTURE);
            }
        });

        btn_TakePictureShare.setOnClickListener(new View.OnClickListener() {
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

        ArrayAdapter<String> conditionAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, conditionBikeShare);
        tVCondBikeShare.setAdapter(conditionAdapter);

        //Select the Bike available share date
        tVDateAvBikeShare.setOnClickListener(v -> selectShareAvailableDate());

        btn_SaveBikeShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //show progress Dialog
                progressDialog.show();
                if (shareUploadTask != null && shareUploadTask.isInProgress()) {
                    Toast.makeText(AddBikeShare.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadShareBikes();
                }
            }
        });
    }

    public void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
        uriBikeShare = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriBikeShare);
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
                    uriBikeShare = data.getData();
                    ivBikeShare.setImageURI(uriBikeShare);
                }
                break;
            case TAKE_PICTURE:
                if (resultCode == RESULT_OK) {
                    ivBikeShare.setImageURI(uriBikeShare);
                }
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    //Pick the share bike available date
    private void selectShareAvailableDate() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(AddBikeShare.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date_year = String.valueOf(year);
                String date_month = (month + 1) < 10 ? "0" + (month + 1) : String.valueOf(month + 1);
                String date_day = dayOfMonth < 10 ? "0" + dayOfMonth : String.valueOf(dayOfMonth);
                String picked_Date = date_day + "/" + date_month + "/" + date_year;
                eTDateAvBikeShare.setText(picked_Date);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dialog.show();
    }

    private void alertDialogAvailableDateEmpty() {
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

    //Upload a new Bicycle into the Share Bicycles table
    public void uploadShareBikes() {

        if (validateShareBikesDetail()) {

            fName_BikeShare = Objects.requireNonNull(eTFNameBikeShare.getText()).toString().trim();
            lName_BikeShare = Objects.requireNonNull(eTLNameBikeShare.getText()).toString().trim();
            pNo_BikeShare = Objects.requireNonNull(eTPNoBikeShare.getText()).toString().trim();
            email_BikeShare = Objects.requireNonNull(eTEmailBikeShare.getText()).toString().trim();

            cond_BikeShare = tVCondBikeShare.getText().toString().trim();
            model_BikeShare = Objects.requireNonNull(eTModelBikeShare.getText()).toString().trim();
            manufact_BikeShare = Objects.requireNonNull(eTManufactBikeShare.getText()).toString().trim();
            price_BikeShare = Double.parseDouble(Objects.requireNonNull(eTPricePerDayBikeShare.getText()).toString().trim());
            dateAv_BikeShare = eTDateAvBikeShare.getText().toString().trim();

            progressDialog.setTitle("The Bike is uploading");
            progressDialog.show();

            final StorageReference fileReference = storageRefShareBikes.child(System.currentTimeMillis() + "." + getFileExtension(uriBikeShare));
            shareUploadTask = fileReference.putFile(uriBikeShare)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @SuppressLint("SetTextI18n")
                                @Override
                                public void onSuccess(Uri uri) {
                                    bike_KeyShare = databaseRefShareBikes.push().getKey();

                                    BikesShare rent_Bikes = new BikesShare(fName_BikeShare, lName_BikeShare, pNo_BikeShare, email_BikeShare,
                                            uri.toString(), cond_BikeShare, model_BikeShare, manufact_BikeShare, price_BikeShare,
                                            dateAv_BikeShare, customId_BikeShare, bike_KeyShare);

                                    assert bike_KeyShare != null;
                                    databaseRefShareBikes.child(bike_KeyShare).setValue(rent_Bikes);

                                    LayoutInflater inflater = getLayoutInflater();
                                    @SuppressLint("InflateParams") View layout = inflater.inflate(R.layout.toast, null);
                                    TextView text = layout.findViewById(R.id.tvToast);
                                    ImageView imageView = layout.findViewById(R.id.imgToast);
                                    text.setText("The bike was successfully uploaded!!");
                                    imageView.setImageResource(R.drawable.baseline_directions_bike_24);
                                    Toast toast = new Toast(getApplicationContext());
                                    toast.setDuration(Toast.LENGTH_SHORT);
                                    toast.setView(layout);
                                    toast.show();

                                    Intent add_Bikes = new Intent(AddBikeShare.this, CustomerPageShareBikes.class);
                                    startActivity(add_Bikes);
                                    finish();
                                }
                            });
                            progressDialog.dismiss();
                        }
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(AddBikeShare.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        //show upload Progress
                        double progress = 100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount();
                        progressDialog.setMessage("Uploaded: " + (int) progress + "%");
                        progressDialog.setProgress((int) progress);
                    });
        }
    }

    private Boolean validateShareBikesDetail() {
        boolean result = false;
        final String etFName_BikeShareVal = Objects.requireNonNull(eTFNameBikeShare.getText()).toString().trim();
        final String etLName_BikeShareVal = Objects.requireNonNull(eTLNameBikeShare.getText()).toString().trim();
        final String etPNo_BikeShareVal = Objects.requireNonNull(eTPNoBikeShare.getText()).toString().trim();
        final String etEmail_BikeShareVal = Objects.requireNonNull(eTEmailBikeShare.getText()).toString().trim();

        final String tVCond_BikeShareVal = tVCondBikeShare.getText().toString().trim();
        final String etModel_BikeShareVal = Objects.requireNonNull(eTModelBikeShare.getText()).toString().trim();
        final String etManufact_BikeShareVal = Objects.requireNonNull(eTManufactBikeShare.getText()).toString().trim();
        final String etPrice_BikeShareVal = Objects.requireNonNull(eTPricePerDayBikeShare.getText()).toString().trim();
        final String etDateAv_BikeShareVal = eTDateAvBikeShare.getText().toString().trim();

        if (TextUtils.isEmpty(etFName_BikeShareVal)) {
            eTFNameBikeShare.setError("Please enter the First Name");
            eTFNameBikeShare.requestFocus();
        }
        else if (TextUtils.isEmpty(etLName_BikeShareVal)) {
            eTLNameBikeShare.setError("Please enter the Last Name");
            eTLNameBikeShare.requestFocus();
        }
        else if (TextUtils.isEmpty(etPNo_BikeShareVal)) {
            eTPNoBikeShare.setError("Please enter the Phone Number");
            eTPNoBikeShare.requestFocus();
        }
        else if (TextUtils.isEmpty(etEmail_BikeShareVal)) {
            eTEmailBikeShare.setError("Please enter the Email Address");
            eTEmailBikeShare.requestFocus();
        }

        else if (uriBikeShare == null) {
            alertDialogBikeSharePicture();
        }

        else if (TextUtils.isEmpty(tVCond_BikeShareVal)) {
            alertDialogBikeShareCond();
            tVCondBikeShare.requestFocus();
        }
        else if (TextUtils.isEmpty(etModel_BikeShareVal)) {
            eTModelBikeShare.setError("Please add the Model of Bicycle");
            eTModelBikeShare.requestFocus();
        }
        else if (TextUtils.isEmpty(etManufact_BikeShareVal)) {
            eTManufactBikeShare.setError("Please add the Manufacturer");
            eTManufactBikeShare.requestFocus();
        }
        else if (TextUtils.isEmpty(etPrice_BikeShareVal)) {
            eTPricePerDayBikeShare.setError("Please add the Price/Day ");
            eTPricePerDayBikeShare.requestFocus();
        }
        else if (TextUtils.isEmpty(etDateAv_BikeShareVal)) {
            alertDialogAvailableDateEmpty();
            eTDateAvBikeShare.requestFocus();
        } else {
            result = true;
        }
        return result;
    }

    public void alertDialogBikeSharePicture() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setTitle("No bike picture?")
                .setMessage("Please add a picture!!")
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, id) -> dialog.dismiss());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void alertDialogBikeShareCond() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setTitle("No bike condition?")
                .setMessage("Select the Bike condition!!")
                .setPositiveButton("OK", (dialog, id) -> dialog.dismiss());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        loadCustomerDetailsShareBikes();
    }

    public void loadCustomerDetailsShareBikes() {

        progressDialog.show();

        databaseRefCustomer.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"SetTextI18n", "NewApi"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //retrieve data from database
                for (DataSnapshot ds_User : dataSnapshot.getChildren()) {
                    FirebaseUser user_Db = firebaseAuth.getCurrentUser();

                    Customers custom_Data = ds_User.getValue(Customers.class);
                    assert user_Db != null;
                    assert custom_Data != null;
                    if (user_Db.getUid().equals(ds_User.getKey())) {
                        tVShareBikes.setText("Welcome: " + custom_Data.getfName_Customer() + " " + custom_Data.getlName_Customer());
                        eTFNameBikeShare.setText(custom_Data.getfName_Customer());
                        eTLNameBikeShare.setText(custom_Data.getlName_Customer());
                        eTPNoBikeShare.setText(custom_Data.getPhoneNumb_Customer());
                        eTEmailBikeShare.setText(custom_Data.getEmail_Customer());
                        customId_BikeShare = user_Db.getUid();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AddBikeShare.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        progressDialog.dismiss();
    }
}
