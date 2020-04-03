package com.example.rentandsharebikes;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class BikeStoreAdapterShowBikesListAdmin extends RecyclerView.Adapter<BikeStoreAdapterShowBikesListAdmin.ImageViewHolder>{

    private Context bikeStoreContext;
    private List<BikeStore> bikeStoreUploads;

    private FirebaseStorage bikeStorage;
    private DatabaseReference databaseReference;
    private ValueEventListener bikesEventListener;

    private List<Bikes> bikesList;

    private int numberBikesAvailable;

    public BikeStoreAdapterShowBikesListAdmin(Context bikeStore_context, List<BikeStore> bikeStore_uploads){
        bikeStoreContext = bikeStore_context;
        bikeStoreUploads = bikeStore_uploads;
    }

    @NonNull
    @Override
    public BikeStoreAdapterShowBikesListAdmin.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(bikeStoreContext).inflate(R.layout.image_bikestore,parent, false);
        return new BikeStoreAdapterShowBikesListAdmin.ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final BikeStoreAdapterShowBikesListAdmin.ImageViewHolder holder, int position) {

        final BikeStore uploadCurrent = bikeStoreUploads.get(position);
        holder.tvStoreBikeNumber.setText(String.valueOf(uploadCurrent.getBikeStore_Number()));
        holder.tvStoreBikeLocation.setText(uploadCurrent.getBikeStore_Location());
        holder.tvStoreBikeAddress.setText(uploadCurrent.getBikeStore_Address());
        holder.tvStoreBikeSlots.setText(String.valueOf(uploadCurrent.getBikeStore_NumberSlots()));

        bikesList = new ArrayList<>();

        //initialize the bike storage database
        bikeStorage = FirebaseStorage.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Bikes");

        bikesEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bikesList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Bikes bikes = postSnapshot.getValue(Bikes.class);
                    assert bikes != null;
                    if (bikes.getBikeStoreKey().equals(uploadCurrent.getStoreKey())) {
                        bikes.setBikesKey(postSnapshot.getKey());
                        bikesList.add(bikes);
                        numberBikesAvailable = bikesList.size();
                        holder.tvStoreBikesAvailable.setText(String.valueOf(numberBikesAvailable));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Toast.makeText(BikesImageAdmin.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(bikeStoreContext, BikesImageAdmin.class);
                intent.putExtra("SName",uploadCurrent.getBikeStore_Location());
                intent.putExtra("SKey",uploadCurrent.getStoreKey());
                bikeStoreContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bikeStoreUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        public TextView tvStoreBikeNumber;
        public TextView tvStoreBikeLocation;
        public TextView tvStoreBikeAddress;
        public TextView tvStoreBikeSlots;
        public TextView tvStoreBikesAvailable;

        public ImageViewHolder(View itemView) {
            super(itemView);
            tvStoreBikeNumber = itemView.findViewById(R.id.tvStoreNumber);
            tvStoreBikeLocation = itemView.findViewById(R.id.tvStorePlace);
            tvStoreBikeAddress = itemView.findViewById(R.id.tvStoreAddress);
            tvStoreBikeSlots = itemView.findViewById(R.id.tvStoreSlots);
            tvStoreBikesAvailable =  itemView.findViewById(R.id.tvNrAvailable);
        }
    }
}
