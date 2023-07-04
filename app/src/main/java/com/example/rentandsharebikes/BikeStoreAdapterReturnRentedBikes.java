package com.example.rentandsharebikes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BikeStoreAdapterReturnRentedBikes extends RecyclerView.Adapter<BikeStoreAdapterReturnRentedBikes.ImageViewHolder> {

    private final Context bikeStoreContext;
    private final List<BikeStores> bikeStoresList;

    private OnItemClickListener clickListener;

    public BikeStoreAdapterReturnRentedBikes(Context bikeStore_context, List<BikeStores> bikeStores_uploads) {
        bikeStoreContext = bikeStore_context;
        bikeStoresList = bikeStores_uploads;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(bikeStoreContext).inflate(R.layout.image_bike_store_return_rented_bike, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        final BikeStores uploadCurrent = bikeStoresList.get(position);
        holder.tVBikeStoreName.setText(uploadCurrent.getBikeStore_Location());
        holder.tVBikeStoreKey.setText(uploadCurrent.getBikeStore_Key());
    }

    @Override
    public int getItemCount() {
        return bikeStoresList.size();
    }


    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tVBikeStoreName;
        public TextView tVBikeStoreKey;

        public ImageViewHolder(View itemView) {
            super(itemView);
            tVBikeStoreName = itemView.findViewById(R.id.tvBikeStoreNameSpin);

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

    public void setOnItemClickListener(OnItemClickListener listener) {
        clickListener = listener;
    }
}
