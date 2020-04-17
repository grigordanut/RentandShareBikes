package com.example.rentandsharebikes;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
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

import static android.icu.text.DateFormat.NONE;

public class BikeStoreAdapterShowStoresListCustomer extends RecyclerView.Adapter<BikeStoreAdapterShowStoresListCustomer.ImageViewHolder> {

    private Context bikeStoreContext;
    private List<BikeStore> bikeStoreUploads;

    private OnItemClickListener clickListener;

    private FirebaseStorage bikeStorage;
    private DatabaseReference databaseReference;
    private ValueEventListener bikesEventListener;

    private List<Bikes> bikesList;

    private int numberBikesAvailable;

    public BikeStoreAdapterShowStoresListCustomer(Context bikeStore_context, List<BikeStore> bikeStore_uploads) {
        bikeStoreContext = bikeStore_context;
        bikeStoreUploads = bikeStore_uploads;
    }

    @NonNull
    @Override
    public BikeStoreAdapterShowStoresListCustomer.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(bikeStoreContext).inflate(R.layout.image_bikestore, parent, false);
        return new BikeStoreAdapterShowStoresListCustomer.ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final BikeStoreAdapterShowStoresListCustomer.ImageViewHolder holder, int position) {
        final BikeStore uploadCurrent = bikeStoreUploads.get(position);
        //holder.tvStoreBikeNumber.setText(String.valueOf(uploadCurrent.getBikeStore_Number()));
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
    }

    @Override
    public int getItemCount() {
        return bikeStoreUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
        public TextView tvStoreBikeLocation;
        public TextView tvStoreBikeAddress;
        public TextView tvStoreBikeSlots;
        public TextView tvStoreBikesAvailable;

        public ImageViewHolder(View itemView) {
            super(itemView);
            tvStoreBikeLocation = itemView.findViewById(R.id.tvStorePlace);
            tvStoreBikeAddress = itemView.findViewById(R.id.tvStoreAddress);
            tvStoreBikeSlots = itemView.findViewById(R.id.tvStoreSlots);
            tvStoreBikesAvailable = itemView.findViewById(R.id.tvNrAvailable);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
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

        //create onItem click menu
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select an Action");
            MenuItem doShowMapStore = menu.add(NONE, 1, 1, "Show Map Store");
            MenuItem doUpdateStore = menu.add(NONE, 2, 2, "Update Bike Store");

            doShowMapStore.setOnMenuItemClickListener(this);
            doUpdateStore.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (clickListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    switch (item.getItemId()) {
                        case 1:
                            clickListener.onShowMapStoreClick(position);
                            return true;

                        case 2:
                            clickListener.onUpdateStoreClick(position);
                            return true;
                    }
                }
            }
            return false;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onShowMapStoreClick(int position);

        void onUpdateStoreClick(int position);
    }

    public void setOnItmClickListener(OnItemClickListener listener) {
        clickListener = listener;
    }
}
