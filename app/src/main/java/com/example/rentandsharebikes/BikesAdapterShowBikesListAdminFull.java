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

import static android.icu.text.DateFormat.NONE;

public class BikesAdapterShowBikesListAdminFull extends RecyclerView.Adapter<BikesAdapterShowBikesListAdminFull.ImageViewHolder> {
    private Context bikesContext;
    private List<Bikes> bikesUploads;
    private OnItemClickListener clickListener;

    public BikesAdapterShowBikesListAdminFull(Context bikes_context, List<Bikes> bikes_uploads){
        bikesContext = bikes_context;
        bikesUploads = bikes_uploads;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(bikesContext).inflate(R.layout.image_bikes_admin_full,parent, false);
        return new ImageViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {

        Bikes uploadCurrent = bikesUploads.get(position);
        holder.tVBikeStoreNameFull.setText(uploadCurrent.getBikeStoreName());
        holder.tVBikeConAdminFull.setText(uploadCurrent.getBike_Condition());
        holder.tVBikeMAdminFull.setText(uploadCurrent.getBike_Model());
        holder.tVBikeManAdminFull.setText(uploadCurrent.getBike_Manufacturer());
        holder.tVBikePAdminFull.setText(String.valueOf( +uploadCurrent.getBike_Price()));

        Picasso.get()
                .load(uploadCurrent.getBike_Image())
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(holder.imBikeAdminFull);
    }

    @Override
    public int getItemCount() {
        return bikesUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tVBikeStoreNameFull;
        public ImageView imBikeAdminFull;
        public TextView tVBikeConAdminFull;
        public TextView tVBikeMAdminFull;
        public TextView tVBikeManAdminFull;
        public TextView tVBikePAdminFull;

        public ImageViewHolder(View itemView) {
            super(itemView);
            tVBikeStoreNameFull = itemView.findViewById(R.id.tvBikeStoreNameFull);
            imBikeAdminFull = itemView.findViewById(R.id.imgShowBikeFull);
            tVBikeConAdminFull = itemView.findViewById(R.id.tvBikeCondFull);
            tVBikeMAdminFull = itemView.findViewById(R.id.tvBikeModelFull);
            tVBikeManAdminFull = itemView.findViewById(R.id.tvBikeManufactFull);
            tVBikePAdminFull = itemView.findViewById(R.id.tvBikePriceFull);

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
