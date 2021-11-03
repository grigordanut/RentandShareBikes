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

public class BikesAdapterShowBikesListMainAll extends RecyclerView.Adapter<BikesAdapterShowBikesListMainAll.ImageViewHolder> {

    private final Context bikesContext;
    private final List<BikesRent> bikesRentUploads;

    private OnItemClickListener clickListener;

    public BikesAdapterShowBikesListMainAll(Context bikes_context, List<BikesRent> bikes_ToRent_uploads){
        bikesContext = bikes_context;
        bikesRentUploads = bikes_ToRent_uploads;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(bikesContext).inflate(R.layout.image_bikes_available_main_all,parent, false);
        return new ImageViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {

        BikesRent uploadCurrent = bikesRentUploads.get(position);
        holder.tVBikeStoreAvMainAll.setText(uploadCurrent.getBikeStoreName());
        holder.tVBikeCondAvMainAll.setText(uploadCurrent.getBike_Condition());
        holder.tVBikeModelAvMainAll.setText(uploadCurrent.getBike_Model());
        holder.tVBikeManAvMainAll.setText(uploadCurrent.getBike_Manufacturer());
        holder.tVBikePriceAvMainAll.setText(String.valueOf( +uploadCurrent.getBike_Price()));

        Picasso.get()
                .load(uploadCurrent.getBike_Image())
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(holder.imBikeAvMainAll);
    }

    @Override
    public int getItemCount() {
        return bikesRentUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView imBikeAvMainAll;
        public TextView tVBikeStoreAvMainAll;
        public TextView tVBikeCondAvMainAll;
        public TextView tVBikeModelAvMainAll;
        public TextView tVBikeManAvMainAll;
        public TextView tVBikePriceAvMainAll;

        public ImageViewHolder(View itemView) {
            super(itemView);

            imBikeAvMainAll = itemView.findViewById(R.id.imgBikesAvMainAll);
            tVBikeStoreAvMainAll = itemView.findViewById(R.id.tvBikeStoreAvMainAll);
            tVBikeCondAvMainAll = itemView.findViewById(R.id.tvBikeCondAvMainAll);
            tVBikeModelAvMainAll = itemView.findViewById(R.id.tvBikeModelAvMainAll);
            tVBikeManAvMainAll = itemView.findViewById(R.id.tvBikeManufactAvMainAll);
            tVBikePriceAvMainAll = itemView.findViewById(R.id.tvBikePriceAvMainAll);

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
