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

public class BikeAdapterBikesCustomer extends RecyclerView.Adapter<BikeAdapterBikesCustomer.ImageViewHolder> {

    private final Context bikesContext;
    private final List<Bikes> bikesUploads;

    private OnItemClickListener clickListener;

    public BikeAdapterBikesCustomer(Context bikes_context, List<Bikes> bikes_ToRent_uploads){
        bikesContext = bikes_context;
        bikesUploads = bikes_ToRent_uploads;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(bikesContext).inflate(R.layout.image_customer_bikes,parent, false);
        return new ImageViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {

        final Bikes uploadCurrent = bikesUploads.get(position);
        holder.tVStoreNameUser.setText(uploadCurrent.getBikeStoreName());
        holder.tVBikeConditionUser.setText(uploadCurrent.getBike_Condition());
        holder.tVBikeModelUser.setText(uploadCurrent.getBike_Model());
        holder.tVBikeManufactUser.setText(uploadCurrent.getBike_Manufacturer());
        holder.tVBikePriceUser.setText(String.valueOf(uploadCurrent.getBike_Price()));

        Picasso.get()
                .load(uploadCurrent.getBike_Image())
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(holder.imageBikeUser);
    }

    @Override
    public int getItemCount() {
        return bikesUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public ImageView imageBikeUser;
        public TextView tVStoreNameUser;
        public TextView tVBikeConditionUser;
        public TextView tVBikeModelUser;
        public TextView tVBikeManufactUser;
        public TextView tVBikePriceUser;

        public ImageViewHolder(View itemView) {
            super(itemView);

            imageBikeUser = itemView.findViewById(R.id.imgBikesUser);
            tVStoreNameUser = itemView.findViewById(R.id.tvStoreNameUser);
            tVBikeConditionUser = itemView.findViewById(R.id.tvBikeCondUser);
            tVBikeModelUser = itemView.findViewById(R.id.tvBikeModelUser);
            tVBikeManufactUser = itemView.findViewById(R.id.tvBikeManufactUser);
            tVBikePriceUser = itemView.findViewById(R.id.tvBikePriceUser);

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
