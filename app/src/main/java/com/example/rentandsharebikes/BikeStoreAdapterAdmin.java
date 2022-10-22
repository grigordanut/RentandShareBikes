package com.example.rentandsharebikes;

import android.content.Context;
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

public class BikeStoreAdapterAdmin extends RecyclerView.Adapter<BikeStoreAdapterAdmin.ImageViewHolder> {
    private Context bikeStoreContext;
    private List<BikeStores> bikeStoresUploads;

    private FirebaseStorage bikeStorage;
    private DatabaseReference databaseReference;
    private ValueEventListener bikesEventListener;

    private List<Bikes> bikesList;

    private int numberBikesAvailable;

    private OnItemClickListener clickListener;

    public BikeStoreAdapterAdmin(Context bikeStore_context, List<BikeStores> bikeStores_uploads){
        bikeStoreContext = bikeStore_context;
        bikeStoresUploads = bikeStores_uploads;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(bikeStoreContext).inflate(R.layout.image_bikestore,parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder holder, int position) {

        final BikeStores uploadCurrent = bikeStoresUploads.get(position);
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
                    if (bikes.getBikeStoreKey().equals(uploadCurrent.getBikeStore_Key())) {
                        bikes.setBike_Key(postSnapshot.getKey());
                        bikesList.add(bikes);
                        numberBikesAvailable = bikesList.size();
                        holder.tvStoreBikesAvailable.setText(String.valueOf(numberBikesAvailable));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Toast.makeText(BikeStoreAdapterAdmin.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return bikeStoresUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tvStoreBikeLocation;
        public TextView tvStoreBikeAddress;
        public TextView tvStoreBikeSlots;
        public TextView tvStoreBikesAvailable;

        public ImageViewHolder(View itemView) {
            super(itemView);
            tvStoreBikeLocation = itemView.findViewById(R.id.tvStorePlace);
            tvStoreBikeAddress = itemView.findViewById(R.id.tvStoreAddress);
            tvStoreBikeSlots = itemView.findViewById(R.id.tvStoreSlots);
            tvStoreBikesAvailable =  itemView.findViewById(R.id.tvNrAvailable);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    clickListener.onItemClick(position);
                }
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItmClickListener(OnItemClickListener listener) {
        clickListener = listener;
    }
}
