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

public class BikeAdapterBikesAdmin extends RecyclerView.Adapter<BikeAdapterBikesAdmin.ImageViewHolder> {

    private final Context bikesContext;
    private final List<Bikes> bikesUploads;

    private OnItemClickListener clickListener;

    public BikeAdapterBikesAdmin(Context bikes_context, List<Bikes> bikes_ToRent_uploads){
        bikesContext = bikes_context;
        bikesUploads = bikes_ToRent_uploads;
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

        Bikes uploadCurrent = bikesUploads.get(position);
        holder.tvBikeConAdmin.setText(uploadCurrent.getBike_Condition());
        holder.tvBikeMAdmin.setText(uploadCurrent.getBike_Model());
        holder.tvBikeManAdmin.setText(uploadCurrent.getBike_Manufacturer());
        holder.tvBikePAdmin.setText(String.valueOf( +uploadCurrent.getBike_Price()));

        Picasso.get()
                .load(uploadCurrent.getBike_Image())
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(holder.imBikeAdmin);
    }

    @Override
    public int getItemCount() {
        return bikesUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView imBikeAdmin;
        public TextView tvBikeConAdmin;
        public TextView tvBikeMAdmin;
        public TextView tvBikeManAdmin;
        public TextView tvBikePAdmin;

        public ImageViewHolder(View itemView) {
            super(itemView);

            imBikeAdmin = itemView.findViewById(R.id.imgShowBike);
            tvBikeConAdmin = itemView.findViewById(R.id.tvBikeCondition);
            tvBikeMAdmin = itemView.findViewById(R.id.tvBikeModel);
            tvBikeManAdmin = itemView.findViewById(R.id.tvBikeManufact);
            tvBikePAdmin = itemView.findViewById(R.id.tvBikePrice);

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

    public void setOnItmClickListener(OnItemClickListener listener){
        clickListener = listener;
    }
}
