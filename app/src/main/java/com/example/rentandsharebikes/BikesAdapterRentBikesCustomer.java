package com.example.rentandsharebikes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class BikesAdapterRentBikesCustomer extends RecyclerView.Adapter<BikesAdapterRentBikesCustomer.ImageViewHolder> {

    private final Context bikesContext;
    private final List<BikesRent> bikesRentUploads;

    public BikesAdapterRentBikesCustomer(Context bikes_context, List<BikesRent> bikes_ToRent_uploads){
        bikesContext = bikes_context;
        bikesRentUploads = bikes_ToRent_uploads;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(bikesContext).inflate(R.layout.image_bikes_admin,parent, false);
        return new ImageViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {

        final BikesRent uploadCurrent = bikesRentUploads.get(position);
        holder.tvBikeCondition.setText(uploadCurrent.getBike_Condition());
        holder.tvBikeModel.setText(uploadCurrent.getBike_Model());
        holder.tvBikeManufacturer.setText(uploadCurrent.getBike_Manufacturer());
        holder.tvBikePrice.setText(String.valueOf( +uploadCurrent.getBike_Price()));

        Picasso.get()
                .load(uploadCurrent.getBike_Image())
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(holder.imageBike);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(bikesContext, RentBikesCustomer.class);
                intent.putExtra("BCondition",uploadCurrent.getBike_Condition());
                intent.putExtra("BModel",uploadCurrent.getBike_Model());
                intent.putExtra("BManufact",uploadCurrent.getBike_Manufacturer());
                intent.putExtra("BImage",uploadCurrent.getBike_Image());
                intent.putExtra("BStoreName",uploadCurrent.getBikeStoreName());
                intent.putExtra("BStoreKey",uploadCurrent.getBikeStoreKey());
                intent.putExtra("BPrice",String.valueOf(uploadCurrent.getBike_Price()));
                intent.putExtra("BKey",uploadCurrent.getBike_Key());
                bikesContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bikesRentUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageBike;
        public TextView tvBikeCondition;
        public TextView tvBikeModel;
        public TextView tvBikeManufacturer;
        public TextView tvBikePrice;

        public ImageViewHolder(View itemView) {
            super(itemView);

            imageBike = itemView.findViewById(R.id.imgShowBike);
            tvBikeCondition = itemView.findViewById(R.id.tvBikeCondition);
            tvBikeModel = itemView.findViewById(R.id.tvBikeModel);
            tvBikeManufacturer = itemView.findViewById(R.id.tvBikeManufact);
            tvBikePrice = itemView.findViewById(R.id.tvBikePrice);
        }
    }
}
