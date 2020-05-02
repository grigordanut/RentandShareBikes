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

public class BikesAdapterShowBikesRentedCustom extends RecyclerView.Adapter<BikesAdapterShowBikesRentedCustom.ImageViewHolder> {

    private Context bikesContext;
    private List<RentBikes> bikesUploads;
    private OnItemClickListener clickListener;

    public BikesAdapterShowBikesRentedCustom(Context bikes_context, List<RentBikes> bikes_uploads){
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

    }

    @Override
    public int getItemCount() {
        return bikesUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

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

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(clickListener !=null){
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION){
                    clickListener.onItemClick(position);
                }
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);

        void alertDialogShowRentedBikesOptions(int position);
    }

    public void setOnItmClickListener(OnItemClickListener listener){
        clickListener = listener;
    }
}
