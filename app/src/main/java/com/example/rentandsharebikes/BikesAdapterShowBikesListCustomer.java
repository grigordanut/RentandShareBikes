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

public class BikesAdapterShowBikesListCustomer extends RecyclerView.Adapter<BikesAdapterShowBikesListCustomer.ImageViewHolder> {

    private final Context bikesContext;
    private final List<BikesRent> bikesRentUploads;
    private OnItemClickListener clickListener;

    public BikesAdapterShowBikesListCustomer(Context bikes_context, List<BikesRent> bikes_ToRent_uploads){
        bikesContext = bikes_context;
        bikesRentUploads = bikes_ToRent_uploads;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(bikesContext).inflate(R.layout.image_bikes_customer,parent, false);
        return new ImageViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final ImageViewHolder holder, int position) {

        final BikesRent uploadCurrent = bikesRentUploads.get(position);
        holder.tvBikeSUser.setText(uploadCurrent.getBikeStoreName());
        holder.tvBikeCUser.setText(uploadCurrent.getBike_Condition());
        holder.tvBikeMUser.setText(uploadCurrent.getBike_Model());
        holder.tvBikeManUser.setText(uploadCurrent.getBike_Manufacturer());
        holder.tvBikePUser.setText(String.valueOf(uploadCurrent.getBike_Price()));

        Picasso.get()
                .load(uploadCurrent.getBike_Image())
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(holder.imBikeUser);
    }

    @Override
    public int getItemCount() {
        return bikesRentUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView imBikeUser;
        public TextView tvBikeSUser;
        public TextView tvBikeCUser;
        public TextView tvBikeMUser;
        public TextView tvBikeManUser;
        public TextView tvBikePUser;

        public ImageViewHolder(View itemView) {
            super(itemView);

            imBikeUser = itemView.findViewById(R.id.imgBikesUser);
            tvBikeSUser = itemView.findViewById(R.id.tvStoreNameUser);
            tvBikeCUser = itemView.findViewById(R.id.tvBikeCondUser);
            tvBikeMUser = itemView.findViewById(R.id.tvBikeModelUser);
            tvBikeManUser = itemView.findViewById(R.id.tvBikeManufactUser);
            tvBikePUser = itemView.findViewById(R.id.tvBikePriceUser);

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
    }

    public void setOnItmClickListener(OnItemClickListener listener){
        clickListener = listener;
    }
}
