package com.example.rentandsharebikes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class BikeAdapterSharedBikesOwner extends RecyclerView.Adapter<BikeAdapterSharedBikesOwner.ImageViewHolder>{

    private final Context bikesContext;
    private final List<BikesShare> bikesUploads;

    private OnItemClickListener clickListener;

    public BikeAdapterSharedBikesOwner(Context bikes_context, List<BikesShare> bikes_uploads){
        bikesContext = bikes_context;
        bikesUploads = bikes_uploads;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(bikesContext).inflate(R.layout.image_bikes_shared_customer,parent, false);
        return new ImageViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {

        final BikesShare uploadCurrent = bikesUploads.get(position);
        holder.tvSharedBikeCondUser.setText(uploadCurrent.getShareBike_Condition());
        holder.tvSharedBikeModelUser.setText(uploadCurrent.getShareBike_Model());
        holder.tvSharedBikeManufactUser.setText(uploadCurrent.getShareBike_Manufact());
        holder.tvSharedBikePriceUser.setText(String.valueOf(uploadCurrent.getShareBike_Price()));
        holder.tvSharedBikeDateAvUser.setText(uploadCurrent.getShareBike_DateAv());

        Picasso.get()
                .load(uploadCurrent.getShareBike_Image())
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(holder.imageSharedBikesUser);
    }

    @Override
    public int getItemCount() {
        return bikesUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public ImageView imageSharedBikesUser;
        public TextView tvSharedBikeCondUser;
        public TextView tvSharedBikeModelUser;
        public TextView tvSharedBikeManufactUser;
        public TextView tvSharedBikePriceUser;
        public TextView tvSharedBikeDateAvUser;

        public ImageViewHolder(View itemView) {
            super(itemView);

            imageSharedBikesUser = itemView.findViewById(R.id.imgSharedBikesCustom);
            tvSharedBikeCondUser = itemView.findViewById(R.id.tvBikeConditionShare);
            tvSharedBikeModelUser = itemView.findViewById(R.id.tvBikeModelShare);
            tvSharedBikeManufactUser = itemView.findViewById(R.id.tvBikeManufactShare);
            tvSharedBikePriceUser = itemView.findViewById(R.id.tvBikePriceShare);
            tvSharedBikeDateAvUser = itemView.findViewById(R.id.tvDateAvShare);

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
