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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

public class BikesImageShowShareBikesOwn extends AppCompatActivity implements BikesAdapterShowShareBikesOwn.OnItemClickListener{

    private DatabaseReference databaseRefShare;
    private FirebaseStorage bikesStorageShare;

    private DatabaseReference databaseRefDeleteBike;
    private FirebaseStorage bikesStorageDelete;

    private ValueEventListener shareBikesEventListener;

    private RecyclerView bikesListRecyclerView;
    private BikesAdapterShowShareBikesOwn bikesAdapterShowShareBikesOwn;

    private TextView tVCustomerShareBikes;

    private List<ShareBikes> shareBikesList;

    String customShareFirst_Name = "";
    String customShareLast_Name = "";
    String customShare_Id = "";

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bikes_image_show_share_bikes_own);

        tVCustomerShareBikes = (TextView) findViewById(R.id.tvBikesImageShowBikesSharedOwn);

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

//        buttonAddMoreBikes = (Button) findViewById(R.id.btnAddMoreBikes);
//        buttonAddMoreBikes.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(BikesImageShowBikesListAdmin.this, BikeStoreImageAddBikesAdmin.class));
//            }
//        });
//
//        buttonBackAdminPageBikes = (Button) findViewById(R.id.btnBackAdminPageBikes);
//        buttonBackAdminPageBikes.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(BikesImageShowBikesListAdmin.this, AdminPage.class));
//            }
//        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onStart() {
        super.onStart();
        loadBikesListAdmin();
    }

    private void loadBikesListAdmin() {
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

                    share_Bikes.setShareBike_Key(postSnapshot.getKey());
                    shareBikesList.add(share_Bikes);
                    tVCustomerShareBikes.setText(shareBikesList.size() + " Bikes added by " + customShareFirst_Name+" "+customShareLast_Name);
                }
                bikesAdapterShowShareBikesOwn = new BikesAdapterShowShareBikesOwn(BikesImageShowShareBikesOwn.this, shareBikesList);
                bikesListRecyclerView.setAdapter(bikesAdapterShowShareBikesOwn);
                bikesAdapterShowShareBikesOwn.setOnItmClickListener(BikesImageShowShareBikesOwn.this);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikesImageShowShareBikesOwn.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

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
                    Intent intent = new Intent(BikesImageShowShareBikesOwn.this, UpdateBikeShareDetails.class);
                    ShareBikes selected_Bike = shareBikesList.get(position);
                    intent.putExtra("BCondUpdate", selected_Bike.getShareBike_Condition());
                    intent.putExtra("BModelUpdate", selected_Bike.getShareBike_Model());
                    intent.putExtra("BManufUpdate", selected_Bike.getShareBike_Manufact());
                    intent.putExtra("BPriceUpdate", String.valueOf(selected_Bike.getShareBike_Price()));
                    intent.putExtra("BImgUpdate", selected_Bike.getShareBike_Image());
                    intent.putExtra("BDateAvUpdate", selected_Bike.getShareBike_DateAv());
                    intent.putExtra("CIdUpdate", selected_Bike.getShareBikes_CustomId());
                    intent.putExtra("BKeyUpdate", selected_Bike.getShareBike_Key());
                    startActivity(intent);
                }
                if(which ==1){
                    bikesStorageDelete = FirebaseStorage.getInstance();
                    databaseRefDeleteBike = FirebaseDatabase.getInstance().getReference("Share Bikes");

                    AlertDialog.Builder builderAlert = new AlertDialog.Builder(BikesImageShowShareBikesOwn.this);
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
                                            Toast.makeText(BikesImageShowShareBikesOwn.this, "The Bike has been deleted successfully ", Toast.LENGTH_SHORT).show();
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
