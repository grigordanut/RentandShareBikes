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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BikeStoreImageShowStoresListAdminNew extends AppCompatActivity implements BikeStoreAdapterShowStoresListAdminNew.OnItemClickListener{

    private TextView textViewBikeStoresImageShowStoreListAdminNew;
    private DatabaseReference databaseReference;
    private ValueEventListener bikeStoreEventListener;

    private RecyclerView bikeStoreRecyclerView;
    private BikeStoreAdapterShowStoresListAdminNew bikeStoreAdapterShowStoresListAdminNew;

    public List<BikeStore> bikeStoreList;

    private ProgressDialog progressDialog;
    private Button buttonAddMoreStoresNew, buttonBackAdminPageStoreNew;

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_store_image_show_stores_list_admin_new);

        textViewBikeStoresImageShowStoreListAdminNew = (TextView)findViewById(R.id.tvBikeStoresImageShowStoreListAdminNew);
        textViewBikeStoresImageShowStoreListAdminNew.setText("No Bike Stores available");

        bikeStoreRecyclerView = (RecyclerView) findViewById(R.id.evRecyclerView);
        bikeStoreRecyclerView.setHasFixedSize(true);
        bikeStoreRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressDialog = new ProgressDialog(this);
        bikeStoreList = new ArrayList<BikeStore>();

        progressDialog.show();

        buttonAddMoreStoresNew = (Button) findViewById(R.id.btnAddMoreStoresNew);
        buttonAddMoreStoresNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BikeStoreImageShowStoresListAdminNew.this, CalculateCoordinates.class));
            }
        });

        buttonBackAdminPageStoreNew = (Button) findViewById(R.id.btnBackAdminPageStoreNew);
        buttonBackAdminPageStoreNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BikeStoreImageShowStoresListAdminNew.this, AdminPage.class));
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
                bikeStoreList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    BikeStore bikeStore = postSnapshot.getValue(BikeStore.class);
                    assert bikeStore != null;
                    bikeStore.setStoreKey(postSnapshot.getKey());
                    bikeStoreList.add(bikeStore);
                    textViewBikeStoresImageShowStoreListAdminNew.setText("Bike Stores available " +bikeStoreList.size());
                }
                bikeStoreAdapterShowStoresListAdminNew = new BikeStoreAdapterShowStoresListAdminNew(BikeStoreImageShowStoresListAdminNew.this, bikeStoreList);
                bikeStoreRecyclerView.setAdapter(bikeStoreAdapterShowStoresListAdminNew);
                bikeStoreAdapterShowStoresListAdminNew.setOnItmClickListener(BikeStoreImageShowStoresListAdminNew.this);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikeStoreImageShowStoresListAdminNew.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Action of the menu onClick
    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, "Press long click to show more action: ", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onShowMapStoreClick(int position) {
        startActivity(new Intent(BikeStoreImageShowStoresListAdminNew.this, MapsActivity.class));
    }

    @Override
    public void onUpdateStoreClick(int position) {

        Intent intent = new Intent(BikeStoreImageShowStoresListAdminNew.this, UpdateBikeStoreDetails.class);
        BikeStore selected_BikeStore = bikeStoreList.get(position);
        intent.putExtra("SLocation", selected_BikeStore.getBikeStore_Location());
        intent.putExtra("SAddress", selected_BikeStore.getBikeStore_Address());
        intent.putExtra("SLatitude", String.valueOf(selected_BikeStore.getBikeStore_Latitude()));
        intent.putExtra("SLongitude", String.valueOf(selected_BikeStore.getBikeStore_Longitude()));
        intent.putExtra("SNoSlots", String.valueOf(selected_BikeStore.getBikeStore_NumberSlots()));
        startActivity(intent);
    }

    //Action of the menu Delete and alert dialog
    @Override
    public void onDeleteStoreClick(final int position) {
        AlertDialog.Builder builderAlert = new AlertDialog.Builder(BikeStoreImageShowStoresListAdminNew.this);
        BikeStore selectedBikeStore = bikeStoreList.get(position);
        builderAlert.setMessage("Are sure to delete " + selectedBikeStore.getBikeStore_Location() + " Bike Store?");
        builderAlert.setCancelable(true);
        builderAlert.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        BikeStore selectedBikeStore = bikeStoreList.get(position);
                        String selectedKeyStore = selectedBikeStore.getStoreKey();
                        databaseReference.child(selectedKeyStore).removeValue();
                        Toast.makeText(BikeStoreImageShowStoresListAdminNew.this, "The Bike Store " + selectedBikeStore.getBikeStore_Location() + " has been deleted successfully", Toast.LENGTH_SHORT).show();

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
        AlertDialog.Builder builderAlert = new AlertDialog.Builder(BikeStoreImageShowStoresListAdminNew.this);
        BikeStore selectedBikeStore = bikeStoreList.get(position);
        builderAlert.setMessage("The " +selectedBikeStore.getBikeStore_Location()+ " Bike Store still has bikes and cannot be deleted \nDelete the Bikes first and after delete the Bike Store");
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