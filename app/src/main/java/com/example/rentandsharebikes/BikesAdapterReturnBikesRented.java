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

public class BikesAdapterReturnBikesRented extends RecyclerView.Adapter<BikesAdapterReturnBikesRented.ImageViewHolder> {

    private Context bikesContext;
    private List<RentBikes> bikesUploads;

    public BikesAdapterReturnBikesRented(Context bikes_context, List<RentBikes> bikes_uploads){
        bikesContext = bikes_context;
        bikesUploads = bikes_uploads;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(bikesContext).inflate(R.layout.image_bikes_rented_customer,parent, false);
        return new ImageViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {

        final RentBikes uploadCurrent = bikesUploads.get(position);
        holder.tvRentedBikeStoreNameUser.setText(uploadCurrent.getStoreLocation_RentBikes());
        holder.tvRentedBikeConditionUser.setText(uploadCurrent.getBikeCond_RentBikes());
        holder.tvRentedBikeModelUser.setText(uploadCurrent.getBikeModel_RentBikes());
        holder.tvRentedBikeManufacturerUser.setText(uploadCurrent.getBikeManufact_RentBikes());
        holder.tvRentedBikePriceUser.setText(String.valueOf(uploadCurrent.getBikePrice_RentBikes()));
        holder.tvRentedBikeDateRentUser.setText(uploadCurrent.getDate_RentBikes());

        Picasso.get()
                .load(uploadCurrent.getBikeImage_RentBike())
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(holder.imageRentedBikeUser);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (bikesContext, ReturnRentedBikes.class);
                //Bike Store name of rented Bike
                intent.putExtra("BStoreNameSame",uploadCurrent.getStoreLocation_RentBikes());
                //Bike Store key of rented bike
                intent.putExtra("BStoreKeySame",uploadCurrent.getStoreKey_RentBikes());
                //Bike key of rented bike
                intent.putExtra("BikeRentedKey",uploadCurrent.getBike_RentKey());
                bikesContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return bikesUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageRentedBikeUser;
        public TextView tvRentedBikeStoreNameUser;
        public TextView tvRentedBikeConditionUser;
        public TextView tvRentedBikeModelUser;
        public TextView tvRentedBikeManufacturerUser;
        public TextView tvRentedBikePriceUser;
        public TextView tvRentedBikeDateRentUser;

        public ImageViewHolder(View itemView) {
            super(itemView);

            imageRentedBikeUser = itemView.findViewById(R.id.imgRentedBikesUser);
            tvRentedBikeStoreNameUser = itemView.findViewById(R.id.tvBikeStoreRentUser);
            tvRentedBikeConditionUser = itemView.findViewById(R.id.tvBikeCondRentUser);
            tvRentedBikeModelUser = itemView.findViewById(R.id.tvBikeModelRentUser);
            tvRentedBikeManufacturerUser = itemView.findViewById(R.id.tvBikeManufactRentUser);
            tvRentedBikePriceUser = itemView.findViewById(R.id.tvBikePriceRentUser);
            tvRentedBikeDateRentUser = itemView.findViewById(R.id.tvBikeDateRentUser);
        }
    }
}
