package com.example.rentandsharebikes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BikesImageShowOwnSharedBikes extends AppCompatActivity implements BikesAdapterShowOwnSharedBikes.OnItemClickListener {
    private DatabaseReference databaseRefShare;
    private FirebaseStorage bikesStorageShare;

    private DatabaseReference databaseRefDeleteBike;
    private FirebaseStorage bikesStorageDelete;

    private ValueEventListener shareBikesEventListener;

    private RecyclerView bikesListRecyclerView;
    private BikesAdapterShowOwnSharedBikes bikesAdapterShowOwnSharedBikes;

    private TextView tVCustomerShareBikes;

    private List<ShareBikes> shareBikesList;

    String customShareFirst_Name = "";
    String customShareLast_Name = "";
    String customShare_Id = "";

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bikes_image_show_own_shared_bikes);

        tVCustomerShareBikes = (TextView) findViewById(R.id.tvBikesImageShowOwnSharedBikes);

        getIntent().hasExtra("CFNameShare");
        customShareFirst_Name = Objects.requireNonNull(getIntent().getExtras()).getString("CFNameShare");

        getIntent().hasExtra("CLNameShare");
        customShareLast_Name = Objects.requireNonNull(getIntent().getExtras()).getString("CLNameShare");

        getIntent().hasExtra("CIdShare");
        customShare_Id = Objects.requireNonNull(getIntent().getExtras()).getString("CIdShare");

        bikesListRecyclerView = (RecyclerView) findViewById(R.id.evRecyclerView);
        bikesListRecyclerView.setHasFixedSize(true);
        bikesListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressDialog = new ProgressDialog(this);
        shareBikesList = new ArrayList<>();

        progressDialog.show();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onStart() {
        super.onStart();
        loadBikeSharedCustomer();
    }

    private void loadBikeSharedCustomer() {
        //initialize the bike storage database
        bikesStorageShare = FirebaseStorage.getInstance();
        databaseRefShare = FirebaseDatabase.getInstance().getReference("Share Bikes");

        shareBikesEventListener = databaseRefShare.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                shareBikesList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    ShareBikes share_Bikes = postSnapshot.getValue(ShareBikes.class);

                    assert share_Bikes != null;
                    if (share_Bikes.getShareBikes_CustomId().equals(customShare_Id)) {
                        share_Bikes.setShareBike_Key(postSnapshot.getKey());
                        shareBikesList.add(share_Bikes);
                        tVCustomerShareBikes.setText(shareBikesList.size() + " bikes added by " + customShareFirst_Name + " " + customShareLast_Name);
                    }
                }
                bikesAdapterShowOwnSharedBikes = new BikesAdapterShowOwnSharedBikes(BikesImageShowOwnSharedBikes.this, shareBikesList);
                bikesListRecyclerView.setAdapter(bikesAdapterShowOwnSharedBikes);
                bikesAdapterShowOwnSharedBikes.setOnItmClickListener(BikesImageShowOwnSharedBikes.this);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikesImageShowOwnSharedBikes.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Action of the menu onClick
    @Override
    public void onItemClick(final int position) {
        final String[] options = {"Update Bike", "Delete Bike"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, options);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select an option");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (which == 0) {
                    Intent intent = new Intent(BikesImageShowOwnSharedBikes.this, UpdateBikeShareDetails.class);
                    ShareBikes selected_Bike = shareBikesList.get(position);
                    intent.putExtra("BSharedKey", selected_Bike.getShareBike_Key());
                    startActivity(intent);
                }
                if(which ==1){
                    bikesStorageDelete = FirebaseStorage.getInstance();
                    databaseRefDeleteBike = FirebaseDatabase.getInstance().getReference("Share Bikes");

                    AlertDialog.Builder builderAlert = new AlertDialog.Builder(BikesImageShowOwnSharedBikes.this);
                    builderAlert.setMessage("Are sure to delete this Bike?");
                    builderAlert.setCancelable(true);
                    builderAlert.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    ShareBikes selected_Bike = shareBikesList.get(position);
                                    final String selectedKeyBike = selected_Bike.getShareBike_Key();
                                    StorageReference imageReference = bikesStorageShare.getReferenceFromUrl(selected_Bike.getShareBike_Image());
                                    imageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            databaseRefDeleteBike.child(selectedKeyBike).removeValue();
                                            Toast.makeText(BikesImageShowOwnSharedBikes.this, "The Bike has been deleted successfully ", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });

                    builderAlert.setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert1 = builderAlert.create();
                    alert1.show();

                }
            }
        });

        final AlertDialog alertDialog = builder.create();

        alertDialog.show();
    }
}
