package com.example.rentandsharebikes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

import static android.icu.text.DateFormat.NONE;

public class BikeStoreAdapterAdminShowStores extends RecyclerView.Adapter<BikeStoreAdapterAdminShowStores.ImageViewHolder> {

    private Context bikeStoreContext;
    private List<BikeStores> bikeStoresUploads;

    private OnItemClickListener clickListener;

    private FirebaseStorage bikeStorageTest;
    private DatabaseReference databaseReferenceTest;
    private ValueEventListener bikesEventListener;

    private List<Bikes> bikesList;

    private int numberBikesAvailable;

    public BikeStoreAdapterAdminShowStores(Context bikeStore_context, List<BikeStores> bikeStores_uploads) {
        bikeStoreContext = bikeStore_context;
        bikeStoresUploads = bikeStores_uploads;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(bikeStoreContext).inflate(R.layout.image_bike_store, parent, false);
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
        bikeStorageTest = FirebaseStorage.getInstance();
        databaseReferenceTest = FirebaseDatabase.getInstance().getReference("Bikes");

        bikesEventListener = databaseReferenceTest.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                bikesList.clear();

                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Bikes bikes = postSnapshot.getValue(Bikes.class);
                        assert bikes != null;
                        bikes.setBike_Key(postSnapshot.getKey());

                        if (bikes.getBikeStoreKey().equals(uploadCurrent.getBikeStore_Key())) {
                            bikesList.add(bikes);
                        }

                        numberBikesAvailable = bikesList.size();
                        holder.tvStoreBikesAvailable.setText(String.valueOf(numberBikesAvailable));
                    }

                    if (numberBikesAvailable == 0) {
                        holder.tvStoreBikesAvailable.setText(String.valueOf(0));
                    }
                }

                else {
                    holder.tvStoreBikesAvailable.setText(String.valueOf(0));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(bikeStoreContext, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return bikeStoresUploads.size();
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
            MenuItem doShowMapStore = menu.add(NONE, 1, 1, "Show Stores on Map");
            MenuItem doUpdateStore = menu.add(NONE, 2, 2, "Update Bike Store");
            MenuItem doDeleteStore = menu.add(NONE, 3, 3, "Delete Bike Store");

            doShowMapStore.setOnMenuItemClickListener(this);
            doUpdateStore.setOnMenuItemClickListener(this);
            doDeleteStore.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(@NonNull MenuItem item) {
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
                        case 3:
                            if (tvStoreBikesAvailable.getText().toString().equals(String.valueOf(0))) {
                                clickListener.onDeleteStoreClick(position);
                            } else {
                                clickListener.alertDialogBikeStoreNotEmpty(position);
                            }
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

        void onDeleteStoreClick(int position);

        void alertDialogBikeStoreNotEmpty(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        clickListener = listener;
    }
}
