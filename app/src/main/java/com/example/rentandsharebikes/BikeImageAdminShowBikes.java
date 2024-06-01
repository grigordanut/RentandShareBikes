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

public class BikeImageAdminShowBikes extends AppCompatActivity implements BikeAdapterAdminBikes.OnItemClickListener {

    //Display the Bikes available in the selected Bike Store
    private FirebaseStorage fbStShowBikes;
    private DatabaseReference dbRefShowBikes;

    private ValueEventListener evListenerShowBikes;

    private RecyclerView rvBikeImgAdmin_ShowBikes;
    private BikeAdapterAdminBikes bikeAdapterAdminBikes;

    private TextView tVBikeImgAdminShowBikes;

    private List<Bikes> listShowBikes;

    private String bikeStore_Name = "";
    private String bikeStore_Key = "";

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_image_admin_show_bikes);

        progressDialog = new ProgressDialog(this);

        //initialize the bike storage database
        fbStShowBikes = FirebaseStorage.getInstance();
        dbRefShowBikes = FirebaseDatabase.getInstance().getReference("Bikes");

        getIntent().hasExtra("SName");
        bikeStore_Name = Objects.requireNonNull(getIntent().getExtras()).getString("SName");

        getIntent().hasExtra("SKey");
        bikeStore_Key = Objects.requireNonNull(getIntent().getExtras()).getString("SKey");

        Objects.requireNonNull(getSupportActionBar()).setTitle(bikeStore_Name + " Bike Store");

        tVBikeImgAdminShowBikes = findViewById(R.id.tvBikeImgAdminShowBikes);

        rvBikeImgAdmin_ShowBikes = findViewById(R.id.rvBikeImgAdminShowBikes);
        rvBikeImgAdmin_ShowBikes.setHasFixedSize(true);
        rvBikeImgAdmin_ShowBikes.setLayoutManager(new LinearLayoutManager(this));

        listShowBikes = new ArrayList<>();

        bikeAdapterAdminBikes = new BikeAdapterAdminBikes(BikeImageAdminShowBikes.this, listShowBikes);
        rvBikeImgAdmin_ShowBikes.setAdapter(bikeAdapterAdminBikes);
        bikeAdapterAdminBikes.setOnItmClickListener(BikeImageAdminShowBikes.this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onStart() {
        super.onStart();
        loadBikesListAdmin();
    }

    private void loadBikesListAdmin() {

        progressDialog.show();

        evListenerShowBikes = dbRefShowBikes.addValueEventListener(new ValueEventListener() {

            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                listShowBikes.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Bikes bikes = postSnapshot.getValue(Bikes.class);
                    assert bikes != null;
                    bikes.setBike_Key(postSnapshot.getKey());
                    String storeKey = bikes.getBikeStoreKey();

                    if (storeKey.equals(bikeStore_Key)) {
                        listShowBikes.add(bikes);
                    }
                }

                if (listShowBikes.size() == 1) {
                    tVBikeImgAdminShowBikes.setText(listShowBikes.size() + " bike available in " + bikeStore_Name + " store");
                }
                else if (listShowBikes.size() > 1) {
                    tVBikeImgAdminShowBikes.setText(listShowBikes.size() + " bikes available in " + bikeStore_Name + " store");
                }
                else {
                    tVBikeImgAdminShowBikes.setText("No bikes available in " + bikeStore_Name + " store");
                }

                bikeAdapterAdminBikes.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikeImageAdminShowBikes.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        progressDialog.dismiss();
    }

    //Action on bikes onClick
    @Override
    public void onItemClick(final int position) {
        showOptionMenu(position);
    }

    public void showOptionMenu(final int position) {
        final String[] options = {"Update this Bike", "Delete this Bike"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, options);
        Bikes selected_Bike = listShowBikes.get(position);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setCancelable(false)
                .setTitle("You selected: " + selected_Bike.getBike_Model() + "\nSelect an option:")
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (which == 0) {
                            updateBikes(position);
                        }
                        if (which == 1) {
                            confirmDeletion(position);
                        }
                    }
                })
                .setNegativeButton("CLOSE",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void updateBikes(final int position) {
        Intent intent = new Intent(BikeImageAdminShowBikes.this, UpdateBikeDetails.class);
        Bikes selected_Bike = listShowBikes.get(position);
        intent.putExtra("BCondition", selected_Bike.getBike_Condition());
        intent.putExtra("BModel", selected_Bike.getBike_Model());
        intent.putExtra("BManufact", selected_Bike.getBike_Manufacturer());
        intent.putExtra("BPrice", String.valueOf(selected_Bike.getBike_Price()));
        intent.putExtra("BImage", selected_Bike.getBike_Image());
        intent.putExtra("BKey", selected_Bike.getBike_Key());
        startActivity(intent);
    }

    public void confirmDeletion(final int position) {
        Bikes selected_Bike = listShowBikes.get(position);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BikeImageAdminShowBikes.this);
        alertDialogBuilder
                .setTitle("Delete bike from Bike Store!!")
                .setMessage("Are sure to delete the " + selected_Bike.getBike_Model() + " Bike?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, which) -> {
                    final String selectedKeyBike = selected_Bike.getBike_Key();
                    StorageReference imageReference = fbStShowBikes.getReferenceFromUrl(selected_Bike.getBike_Image());
                    imageReference.delete().addOnSuccessListener(aVoid -> {
                        dbRefShowBikes.child(selectedKeyBike).removeValue();
                        Toast.makeText(BikeImageAdminShowBikes.this, "The Bike has been deleted successfully ", Toast.LENGTH_SHORT).show();
                    });
                })

                .setNegativeButton("CANCEL", (dialog, which) -> dialog.cancel());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbRefShowBikes.removeEventListener(evListenerShowBikes);
    }

    public void alertDialogNoBikesAvailable() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setTitle("There are not Bikes available!!")
                .setMessage("Would you like to add bikes?")
                .setPositiveButton("YES", (dialog, id) -> {
                    finish();
                    Intent intent = new Intent(BikeImageAdminShowBikes.this, BikeStoreImageAdminAddBikes.class);
                    startActivity(intent);
                })

                .setNegativeButton("NO", (dialog, id) -> {
                    Intent intent = new Intent(BikeImageAdminShowBikes.this, AdminPage.class);
                    startActivity(intent);
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
