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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BikeStoreImageShowStoresListAdmin extends AppCompatActivity implements BikeStoreAdapterShowStoresListAdmin.OnItemClickListener {

    private TextView textViewBikeStoresImageShowStoreListAdmin;
    private DatabaseReference databaseReference;
    private ValueEventListener bikeStoreEventListener;

    private RecyclerView bikeStoreRecyclerView;
    private BikeStoreAdapterShowStoresListAdmin bikeStoreAdapterShowStoresListAdmin;

    public List<BikeStores> bikeStoresList;

    private ProgressDialog progressDialog;
    private Button buttonAddMoreStores, buttonBackAdminPageStore;

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_store_image_show_stores_list_admin);

        textViewBikeStoresImageShowStoreListAdmin = (TextView)findViewById(R.id.tvBikeStoresImageShowStoreListAdmin);
        textViewBikeStoresImageShowStoreListAdmin.setText("No Bike Stores available");

        bikeStoreRecyclerView = (RecyclerView) findViewById(R.id.evRecyclerView);
        bikeStoreRecyclerView.setHasFixedSize(true);
        bikeStoreRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressDialog = new ProgressDialog(this);
        bikeStoresList = new ArrayList<BikeStores>();

        progressDialog.show();

        buttonAddMoreStores = (Button) findViewById(R.id.btnAddMoreStores);
        buttonAddMoreStores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BikeStoreImageShowStoresListAdmin.this, CalculateCoordinates.class));
            }
        });

        buttonBackAdminPageStore = (Button) findViewById(R.id.btnBackAdminPageStore);
        buttonBackAdminPageStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BikeStoreImageShowStoresListAdmin.this, AdminPage.class));
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        loadBikeStoresListAdmin();
    }

    private void loadBikeStoresListAdmin() {
        //initialize the bike store database
        databaseReference = FirebaseDatabase.getInstance().getReference("Bike Stores");

        bikeStoreEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bikeStoresList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    BikeStores bikeStores = postSnapshot.getValue(BikeStores.class);
                    assert bikeStores != null;
                    bikeStores.setBikeStore_Key(postSnapshot.getKey());
                    bikeStoresList.add(bikeStores);
                    textViewBikeStoresImageShowStoreListAdmin.setText(bikeStoresList.size()+" Bike Stores available");
                }
                bikeStoreAdapterShowStoresListAdmin = new BikeStoreAdapterShowStoresListAdmin(BikeStoreImageShowStoresListAdmin.this, bikeStoresList);
                bikeStoreRecyclerView.setAdapter(bikeStoreAdapterShowStoresListAdmin);
                bikeStoreAdapterShowStoresListAdmin.setOnItmClickListener(BikeStoreImageShowStoresListAdmin.this);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikeStoreImageShowStoresListAdmin.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Action of the menu onClick
    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, "Press long click to show more actions: ", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onShowMapStoreClick(int position) {
        startActivity(new Intent(BikeStoreImageShowStoresListAdmin.this, MapsActivity.class));
    }

    @Override
    public void onUpdateStoreClick(int position) {

        Intent intent = new Intent(BikeStoreImageShowStoresListAdmin.this, UpdateBikeStoreDetails.class);
        BikeStores selected_BikeStores = bikeStoresList.get(position);
        intent.putExtra("SLocation", selected_BikeStores.getBikeStore_Location());
        intent.putExtra("SAddress", selected_BikeStores.getBikeStore_Address());
        intent.putExtra("SLatitude", String.valueOf(selected_BikeStores.getBikeStore_Latitude()));
        intent.putExtra("SLongitude", String.valueOf(selected_BikeStores.getBikeStore_Longitude()));
        intent.putExtra("SNoSlots", String.valueOf(selected_BikeStores.getBikeStore_NumberSlots()));
        intent.putExtra("SKey", selected_BikeStores.getBikeStore_Key());
        startActivity(intent);
    }

    //Action of the menu Delete and alert dialog
    @Override
    public void onDeleteStoreClick(final int position) {
        AlertDialog.Builder builderAlert = new AlertDialog.Builder(BikeStoreImageShowStoresListAdmin.this);
        BikeStores selectedBikeStores = bikeStoresList.get(position);
        builderAlert.setMessage("Are sure to delete " + selectedBikeStores.getBikeStore_Location() + " Bike Store?");
        builderAlert.setCancelable(true);
        builderAlert.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        BikeStores selectedBikeStores = bikeStoresList.get(position);
                        String selectedKeyStore = selectedBikeStores.getBikeStore_Key();
                        databaseReference.child(selectedKeyStore).removeValue();
                        Toast.makeText(BikeStoreImageShowStoresListAdmin.this, "The Bike Store " + selectedBikeStores.getBikeStore_Location() + " has been deleted successfully", Toast.LENGTH_SHORT).show();

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

    @Override
    public void alertDialogBikeStoreNotEmpty(final int position) {
        AlertDialog.Builder builderAlert = new AlertDialog.Builder(BikeStoreImageShowStoresListAdmin.this);
        BikeStores selectedBikeStores = bikeStoresList.get(position);
        builderAlert.setMessage("The " + selectedBikeStores.getBikeStore_Location()+ " Bike Store still has bikes and cannot be deleted \nDelete the Bikes first and after delete the Bike Store");
        builderAlert.setCancelable(true);
        builderAlert.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });

        AlertDialog alert1 = builderAlert.create();
        alert1.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseReference.removeEventListener(bikeStoreEventListener);
    }
}
