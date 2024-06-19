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

public class BikeAdapterShowSharedBikesToUpdate extends RecyclerView.Adapter<BikeAdapterShowSharedBikesToUpdate.ImageViewHolder>{

    private final Context bikesContext;
    private final List<BikesShare> bikesUploads;

    public BikeAdapterShowSharedBikesToUpdate(Context bikes_context, List<BikesShare> bikes_uploads){
        bikesContext = bikes_context;
        bikesUploads = bikes_uploads;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(bikesContext).inflate(R.layout.image_customer_bikes_shared,parent, false);
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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(bikesContext, UpdateBikeSharedDetails.class);
                //Customer Details
                intent.putExtra("BFNameUpdate",uploadCurrent.getShareCus_FirstName());
                intent.putExtra("BLNameUpdate",uploadCurrent.getShareCus_LastName());
                intent.putExtra("BPhoneUpdate",uploadCurrent.getShareCus_PhoneNo());
                intent.putExtra("BEmailUpdate",uploadCurrent.getShareCus_Email());

                //Shared Bike Details
                intent.putExtra("BCondUpdate",uploadCurrent.getShareBike_Condition());
                intent.putExtra("BModelUpdate",uploadCurrent.getShareBike_Model());
                intent.putExtra("BManufUpdate",uploadCurrent.getShareBike_Manufact());
                intent.putExtra("BPriceUpdate",String.valueOf(uploadCurrent.getShareBike_Price()));
                intent.putExtra("BImgUpdate",uploadCurrent.getShareBike_Image());
                intent.putExtra("BDateAvUpdate",uploadCurrent.getShareBike_DateAv());
                intent.putExtra("CIdUpdate", uploadCurrent.getShareBikes_CustomId());
                intent.putExtra("BKeyUpdate", uploadCurrent.getShareBike_Key());
                bikesContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bikesUploads.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder{

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
        }
    }
}
