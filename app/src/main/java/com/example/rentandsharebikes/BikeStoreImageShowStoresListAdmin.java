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

public class BikeStoreImageShowStoresListAdmin extends AppCompatActivity implements BikeStoreAdapterShowStoreListAdmin.OnItemClickListener {

    private DatabaseReference databaseReference;
    private ValueEventListener bikeStoreDBEventListener;

    private RecyclerView bikeStoreRecyclerView;
    private BikeStoreAdapterShowStoreListAdmin bikeStoreAdapterShowStoreListAdmin;

    public List<BikeStore> bikeStoreList;

    private TextView tvImageAdmin;

    private ProgressDialog progressDialog;
    private Button buttonAddMoreStores, buttonBackAdminPageStore;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_store_image_show_stores_list_admin);

        tvImageAdmin = (TextView) findViewById(R.id.tvBikeStoresImageShowStoreList);

        bikeStoreRecyclerView = (RecyclerView) findViewById(R.id.evRecyclerView);
        bikeStoreRecyclerView.setHasFixedSize(true);
        bikeStoreRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressDialog = new ProgressDialog(this);
        bikeStoreList = new ArrayList<BikeStore>();

        progressDialog.show();

        buttonAddMoreStores = (Button) findViewById(R.id.btnAddMoreStores);
        buttonAddMoreStores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BikeStoreImageShowStoresListAdmin.this, AddBikeStore.class));
            }
        });

        buttonBackAdminPageStore = (Button) findViewById(R.id.btnBackAdminPageStore);
        buttonBackAdminPageStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BikeStoreImageShowStoresListAdmin.this, AdminPage.class));
            }
        });

        //check if the bikes store list is empty and add a new bike store
        if(databaseReference == null){
            databaseReference = FirebaseDatabase.getInstance().getReference("Bike Stores");
        }

        bikeStoreDBEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bikeStoreList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    BikeStore bikeStore = postSnapshot.getValue(BikeStore.class);
                    assert bikeStore != null;
                    bikeStore.setStoreKey(postSnapshot.getKey());
                    bikeStoreList.add(bikeStore);
                }

                bikeStoreAdapterShowStoreListAdmin = new BikeStoreAdapterShowStoreListAdmin(BikeStoreImageShowStoresListAdmin.this, bikeStoreList);
                bikeStoreRecyclerView.setAdapter(bikeStoreAdapterShowStoreListAdmin);
                bikeStoreAdapterShowStoreListAdmin.setOnItmClickListener(BikeStoreImageShowStoresListAdmin.this);
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
        Toast.makeText(this, "Press long click to show more action: ", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onShowMapStoreClick(int position) {
        startActivity(new Intent(BikeStoreImageShowStoresListAdmin.this, MapsActivity.class));
    }

    @Override
    public void onUpdateStoreClick(int position) {

        Intent intent = new Intent(BikeStoreImageShowStoresListAdmin.this, UpdateBikeStoreDetails.class);
        BikeStore selected_BikeStore = bikeStoreList.get(position);
        intent.putExtra("SNumber", selected_BikeStore.getBikeStore_Number());
        intent.putExtra("SLocation", selected_BikeStore.getBikeStore_Location());
        intent.putExtra("SAddress", selected_BikeStore.getBikeStore_Address());
        intent.putExtra("SNrSlots", selected_BikeStore.getBikeStore_NumberSlots());
        startActivity(intent);
    }

    //Action of the menu Delete and alert dialog
    @Override
    public void onDeleteStoreClick(final int position) {
        AlertDialog.Builder builderAlert = new AlertDialog.Builder(BikeStoreImageShowStoresListAdmin.this);
        BikeStore selectedBikeStore = bikeStoreList.get(position);
        builderAlert.setMessage("Are sure to delete "+selectedBikeStore.getBikeStore_Location()+" Bike Store?");
        builderAlert.setCancelable(true);
        builderAlert.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        BikeStore selectedBikeStore = bikeStoreList.get(position);
                        String selectedKeyStore = selectedBikeStore.getStoreKey();
                        databaseReference.child(selectedKeyStore).removeValue();
                        Toast.makeText(BikeStoreImageShowStoresListAdmin.this, "The Bike Store "+selectedBikeStore.getBikeStore_Location()+" has been deleted successfully", Toast.LENGTH_SHORT).show();

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
    protected void onDestroy() {
        super.onDestroy();
        databaseReference.removeEventListener(bikeStoreDBEventListener);
    }
}
