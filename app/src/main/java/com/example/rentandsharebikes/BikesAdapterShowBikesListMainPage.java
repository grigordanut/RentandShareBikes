package com.example.rentandsharebikes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class BikesAdapterShowBikesListMainPage extends RecyclerView.Adapter<BikesAdapterShowBikesListMainPage.ImageViewHolder>{

    private Context bikesContext;
    private List<Bikes> bikesUploads;

    private OnItemClickListener clickListener;

    public BikesAdapterShowBikesListMainPage(Context bikes_context, List<Bikes> bikes_uploads){
        bikesContext = bikes_context;
        bikesUploads = bikes_uploads;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(bikesContext).inflate(R.layout.image_bikes_available_main,parent, false);
        return new ImageViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {

        Bikes uploadCurrent = bikesUploads.get(position);
        holder.tVBikeStoreAvMain.setText(uploadCurrent.getBikeStoreName());
        holder.tVBikeCondAvMain.setText(uploadCurrent.getBike_Condition());
        holder.tVBikeModelAvMain.setText(uploadCurrent.getBike_Model());
        holder.tVBikeManAvMain.setText(uploadCurrent.getBike_Manufacturer());
        holder.tVBikePriceAvMain.setText(String.valueOf( +uploadCurrent.getBike_Price()));

        Picasso.get()
                .load(uploadCurrent.getBike_Image())
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(holder.imBikeAvMain);
    }

    @Override
    public int getItemCount() {
        return bikesUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView imBikeAvMain;
        public TextView tVBikeStoreAvMain;
        public TextView tVBikeCondAvMain;
        public TextView tVBikeModelAvMain;
        public TextView tVBikeManAvMain;
        public TextView tVBikePriceAvMain;

        public ImageViewHolder(View itemView) {
            super(itemView);

            imBikeAvMain = itemView.findViewById(R.id.imgBikesAvMain);
            tVBikeStoreAvMain = itemView.findViewById(R.id.tvBikeStoreAvMain);
            tVBikeCondAvMain = itemView.findViewById(R.id.tvBikeCondAvMain);
            tVBikeModelAvMain = itemView.findViewById(R.id.tvBikeModelAvMain);
            tVBikeManAvMain = itemView.findViewById(R.id.tvBikeManufactAvMain);
            tVBikePriceAvMain = itemView.findViewById(R.id.tvBikePriceAvMain);

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
