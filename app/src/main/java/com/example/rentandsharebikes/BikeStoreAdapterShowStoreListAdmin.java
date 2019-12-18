package com.example.rentandsharebikes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BikeStoreAdapterShowStoreListAdmin extends RecyclerView.Adapter<BikeStoreAdapterShowStoreListAdmin.ImageViewHolder> {

    private Context bikeStoreContext;
    private List<BikeStore> bikeStoreUploads;

    public BikeStoreAdapterShowStoreListAdmin(Context bikeStore_context, List<BikeStore> bikeStore_uploads){
        bikeStoreContext = bikeStore_context;
        bikeStoreUploads = bikeStore_uploads;
    }


    @NonNull
    @Override
    public BikeStoreAdapterShowStoreListAdmin.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(bikeStoreContext).inflate(R.layout.image_bikestore,parent, false);
        return new BikeStoreAdapterShowStoreListAdmin.ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BikeStoreAdapterShowStoreListAdmin.ImageViewHolder holder, int position) {

        final BikeStore uploadCurrent = bikeStoreUploads.get(position);
        holder.tvStoreBikeLocation.setText(uploadCurrent.getLocationBike_Store());
        holder.tvStoreBikeAddress.setText(uploadCurrent.getAddressBike_Store());
        holder.tvStoreBikeSlots.setText(uploadCurrent.getNumber_Slots());
    }

    @Override
    public int getItemCount() {
        return bikeStoreUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        public TextView tvStoreBikeLocation;
        public TextView tvStoreBikeAddress;
        public TextView tvStoreBikeSlots;

        public ImageViewHolder(View itemView) {
            super(itemView);

            tvStoreBikeLocation = itemView.findViewById(R.id.tvStorePlace);
            tvStoreBikeAddress = itemView.findViewById(R.id.tvStoreAddress);
            tvStoreBikeSlots = itemView.findViewById(R.id.tvStoreSlots);
        }
    }
}
