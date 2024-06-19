package com.example.rentandsharebikes;

import android.content.Context;
import android.view.LayoutInflater;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class BikeStoreAdpterAdminShowBikesRented extends RecyclerView.Adapter<BikeStoreAdpterAdminShowBikesRented.ImageViewHolder> {

    private Context bikeStoreContext;
    private List<BikeStores> bikeStoresUploads;

    private FirebaseStorage fbStorageBikesAv;
    private DatabaseReference dbReferenceBikesAv;
    private ValueEventListener evListenerBikesAv;

    private FirebaseStorage fbStorageBikesRented;
    private DatabaseReference dbReferenceBikesRented;
    private ValueEventListener evListenerBikesRented;

    private List<Bikes> listBikesAv;
    private List<RentedBikes> listBikesRented;

    private int numberBikesAvailable;
    private int numberBikesRented;

    private OnItemClickListener clickListener;

    public BikeStoreAdpterAdminShowBikesRented(Context bikeStore_context, List<BikeStores> bikeStores_uploads) {
        bikeStoreContext = bikeStore_context;
        bikeStoresUploads = bikeStores_uploads;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(bikeStoreContext).inflate(R.layout.image_bike_store_show_rented_bikes, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder holder, int position) {

        final BikeStores uploadCurrent = bikeStoresUploads.get(position);
        holder.tVStoreLocationRent.setText(uploadCurrent.getBikeStore_Location());
        holder.tVStoreAddressRent.setText(uploadCurrent.getBikeStore_Address());
        holder.tVStoreSlotsRent.setText(String.valueOf(uploadCurrent.getBikeStore_NumberSlots()));

        listBikesAv = new ArrayList<>();

        //initialize the bike storage database
        fbStorageBikesAv = FirebaseStorage.getInstance();
        dbReferenceBikesAv = FirebaseDatabase.getInstance().getReference("Bikes");

        evListenerBikesAv = dbReferenceBikesAv.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                listBikesAv.clear();

                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Bikes bikes = postSnapshot.getValue(Bikes.class);
                        assert bikes != null;
                        bikes.setBike_Key(postSnapshot.getKey());

                        if (bikes.getBikeStoreKey().equals(uploadCurrent.getBikeStore_Key())) {
                            listBikesAv.add(bikes);
                        }

                        numberBikesAvailable = listBikesAv.size();
                    }

                    holder.tVNrBikesAvailableRent.setText(String.valueOf(numberBikesAvailable));
                }

                else {
                    holder.tVNrBikesAvailableRent.setText(String.valueOf(0));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(bikeStoreContext, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        listBikesRented = new ArrayList<>();

        //initialize the bike storage database
        fbStorageBikesRented = FirebaseStorage.getInstance();
        dbReferenceBikesRented = FirebaseDatabase.getInstance().getReference("Rent Bikes");

        evListenerBikesRented = dbReferenceBikesRented.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                listBikesRented.clear();

                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        RentedBikes rented_bikes = postSnapshot.getValue(RentedBikes.class);
                        assert rented_bikes != null;
                        rented_bikes.setBikeKey_RentBikes(postSnapshot.getKey());

                        if (rented_bikes.getStoreKey_RentBikes().equals(uploadCurrent.getBikeStore_Key())) {
                            listBikesRented.add(rented_bikes);
                        }

                        numberBikesRented = listBikesRented.size();
                    }
                    holder.tVNrBikesRentedRent.setText(String.valueOf(numberBikesRented));

                }

                else {
                    holder.tVNrBikesRentedRent.setText(String.valueOf(0));
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

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tVStoreLocationRent;
        public TextView tVStoreAddressRent;
        public TextView tVStoreSlotsRent;
        public TextView tVNrBikesAvailableRent;
        public TextView tVNrBikesRentedRent;

        public ImageViewHolder(View itemView) {
            super(itemView);
            tVStoreLocationRent = itemView.findViewById(R.id.tvStoreLocationRent);
            tVStoreAddressRent = itemView.findViewById(R.id.tvStoreAddressRent);
            tVStoreSlotsRent = itemView.findViewById(R.id.tvStoreSlotsRent);
            tVNrBikesAvailableRent = itemView.findViewById(R.id.tvNrBikesAvailableRent);
            tVNrBikesRentedRent = itemView.findViewById(R.id.tvNrBikesRentedRent);

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
