package com.example.rentandsharebikes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Scanner extends AppCompatActivity {

//    private static final int PERMISSION_CODE = 1000;
//    CodeScanner codeScanner;
//    CodeScannerView scannerView;
//    TextView resultData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

//        scannerView = findViewById(R.id.scannerView);
//        codeScanner = new CodeScanner(this,scannerView);
//        resultData = findViewById(R.id.resultsOfQr);
//        codeScanner.setDecodeCallback(new DecodeCallback() {
//            @Override
//            public void onDecoded(@NonNull final Result result) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        //resultData.setText(result.getText());
//                        //Log.d("tag","secret "+result.getText());
//                        Intent intent = new Intent(getApplicationContext(),LockActivity.class);
//                        startActivity(intent);
//                    }
//                });
//            }
//        });
//
//        scannerView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openCamera();
//            }
//        });
    }

//    @SuppressLint("ObsoleteSdkInt")
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (checkSelfPermission(Manifest.permission.CAMERA) ==
//                    PackageManager.PERMISSION_DENIED ||
//                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
//                            PackageManager.PERMISSION_DENIED) {
//                String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
//                requestPermissions(permission, PERMISSION_CODE);
//            } else {
//                openCamera();
//            }
//        } else {
//            openCamera();
//        }
//    }
//    public void openCamera() {
//        codeScanner.startPreview();
//    }
}
