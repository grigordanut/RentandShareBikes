package com.example.rentandsharebikes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;
import java.util.Objects;

public class UpdateBikeDetails extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private FirebaseStorage bikeStorage;
    private ValueEventListener bikesDBEventListener;

    private RecyclerView bikesListRecyclerView;
    private BikesAdapterAdmin bikesListAdapter;

    private TextView textViewBikesImageList;

    public List<Bikes> bikesList;

    String bikeModel, bikeManufact;
    int bikePrice;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_bike_details);

        getIntent().hasExtra("model");
        bikeModel = (getIntent().getExtras()).getString("model");

        EditText bike_ModelNew = (EditText)findViewById(R.id.editTextBikeModel);
        bike_ModelNew.setText(bikeModel);
    }
}
