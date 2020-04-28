package com.example.rentandsharebikes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BikeStoreAdapterReturnBikeDifferentStore extends RecyclerView.Adapter<BikeStoreAdapterReturnBikeDifferentStore.ImageViewHolder> {

    private Context bikeStoreContext;
    private List<BikeStore> bikeStoreUploads;

    private OnItemClickListener clickListener;

    public BikeStoreAdapterReturnBikeDifferentStore(Context bikeStore_context, List<BikeStore> bikeStore_uploads) {
        bikeStoreContext = bikeStore_context;
        bikeStoreUploads = bikeStore_uploads;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(bikeStoreContext).inflate(R.layout.image_bikestore_return_bike, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder holder, int position) {
        final BikeStore uploadCurrent = bikeStoreUploads.get(position);
        holder.tvStoreBikeLocReturn.setText(uploadCurrent.getBikeStore_Location());
        holder.tvStoreBikeAddReturn.setText(uploadCurrent.getBikeStore_Address());
    }

    @Override
    public int getItemCount() {
        return bikeStoreUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tvStoreBikeLocReturn;
        public TextView tvStoreBikeAddReturn;

        public ImageViewHolder(View itemView) {
            super(itemView);
            tvStoreBikeLocReturn = itemView.findViewById(R.id.tvStorePlaceReturn);
            tvStoreBikeAddReturn = itemView.findViewById(R.id.tvStoreAddressReturn);

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
