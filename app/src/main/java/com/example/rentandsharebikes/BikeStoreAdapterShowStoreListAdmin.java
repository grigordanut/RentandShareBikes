package com.example.rentandsharebikes;

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

import java.util.List;

import static android.icu.text.DateFormat.NONE;

public class BikeStoreAdapterShowStoreListAdmin extends RecyclerView.Adapter<BikeStoreAdapterShowStoreListAdmin.ImageViewHolder> {

    private Context bikeStoreContext;
    private List<BikeStore> bikeStoreUploads;

    private OnItemClickListener clickListener;
    private OnItemClickListener mapListener;
    public int numberAvailable;
    private DatabaseReference databaseReference;
    private FirebaseStorage bikeStorage;

    public BikeStoreAdapterShowStoreListAdmin(Context bikeStore_context, List<BikeStore> bikeStore_uploads) {
        bikeStoreContext = bikeStore_context;
        bikeStoreUploads = bikeStore_uploads;
    }

    @NonNull
    @Override
    public BikeStoreAdapterShowStoreListAdmin.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(bikeStoreContext).inflate(R.layout.image_bikestore, parent, false);
        return new BikeStoreAdapterShowStoreListAdmin.ImageViewHolder(view);
    }

    AddBikes addBikes = new AddBikes();
    Bikes bikes = new Bikes();
    BikesImageAdmin bikesImageAdmin = new BikesImageAdmin();

    @Override
    public void onBindViewHolder(BikeStoreAdapterShowStoreListAdmin.ImageViewHolder holder, int position) {
        final BikeStore uploadCurrent = bikeStoreUploads.get(position);
        holder.tvStoreBikeNumber.setText(String.valueOf(uploadCurrent.getBikeStore_Number()));
        holder.tvStoreBikeLocation.setText(uploadCurrent.getBikeStore_Location());
        holder.tvStoreBikeAddress.setText(uploadCurrent.getBikeStore_Address());
        holder.tvStoreBikeSlots.setText(String.valueOf(uploadCurrent.getBikeStore_NumberSlots()));
        //holder.tvStoreAvailable.setText(String.valueOf(bikesImageAdmin.calculateNumberAvailable()));
        //holder.tvStoreAvailable.setText(String.valueOf(numberAvailable));
        //holder.tvStoreAvailable.setText(String.valueOf(calculateNumberAvailableOld()));
    }

    @Override
    public int getItemCount() {
        return bikeStoreUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

        public TextView tvStoreBikeNumber;
        public TextView tvStoreBikeLocation;
        public TextView tvStoreBikeAddress;
        public TextView tvStoreBikeSlots;
        public TextView tvStoreAvailable;

        public ImageViewHolder(View itemView) {
            super(itemView);
            tvStoreBikeNumber = itemView.findViewById(R.id.tvStoreNumber);
            tvStoreBikeLocation = itemView.findViewById(R.id.tvStorePlace);
            tvStoreBikeAddress = itemView.findViewById(R.id.tvStoreAddress);
            tvStoreBikeSlots = itemView.findViewById(R.id.tvStoreSlots);
            tvStoreAvailable = itemView.findViewById(R.id.tvNrAvailable);

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
            MenuItem doDeleteStore = menu.add(NONE, 3, 3, "Delete Bike Store");
            //MenuItem doDeleteStore = menu.add(NONE, 3, 3, "Delete Bike Store");

            doShowMapStore.setOnMenuItemClickListener(this);
            doUpdateStore.setOnMenuItemClickListener(this);
            doDeleteStore.setOnMenuItemClickListener(this);
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

                        case 3:
                            clickListener.onDeleteStoreClick(position);
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
    }

    public void setOnItmClickListener(OnItemClickListener listener) {
        clickListener = listener;
    }

    public int calculateNumberAvailableOld() {
        if (databaseReference == null) {
            bikeStorage = FirebaseStorage.getInstance();
            databaseReference = FirebaseDatabase.getInstance().getReference("Bikes");
        }
        //databaseReference = FirebaseDatabase.getInstance().getReference("Bikes");
        //Query query = databaseReference.child("Bikes").orderByChild("bikeStoreName");
        Query query = databaseReference.orderByChild("Bikes").equalTo("bikeStoreName");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    //ds.getChildrenCount();
                    //numberAvailable = (int)ds.getChildrenCount();
                    numberAvailable = 5;//(int)dataSnapshot.getChildrenCount();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
//        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                //if (dataSnapshot.exists())
//                numberAvailable = 5;//(int)dataSnapshot.getChildrenCount();
//                //numberAvailable = (int)dataSnapshot.getChildrenCount();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                //Toast.makeText(BikeStoreAdapterShowStoreListAdmin.this,databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
        return numberAvailable;
    }
}
