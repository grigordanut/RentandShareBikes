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

public class BikesAdapterCustomer extends RecyclerView.Adapter<BikesAdapterCustomer.ImageViewHolder> {

    private Context bikesContext;
    private List<Bikes> bikesUploads;
    private BikesAdapterAdmin.OnItemClickListener clickListener;

    public BikesAdapterCustomer(Context bikes_context, List<Bikes> bikes_uploads){
        bikesContext = bikes_context;
        bikesUploads = bikes_uploads;
    }

    @NonNull
    @Override
    public BikesAdapterCustomer.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(bikesContext).inflate(R.layout.image_bikes,parent, false);
        return new BikesAdapterCustomer.ImageViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(BikesAdapterCustomer.ImageViewHolder holder, int position) {

        Bikes uploadCurrent = bikesUploads.get(position);
        holder.tvBikeDate.setText("Date: "+uploadCurrent.getBike_Date());
        holder.tvBikeModel.setText("Model: "+uploadCurrent.getBike_Model());
        holder.tvBikeManufacturer.setText("Factory: "+uploadCurrent.getBike_Manufacturer());
        holder.tvBikePrice.setText("Price/Day: € "+uploadCurrent.getBike_Price());

        Picasso.get()
                .load(uploadCurrent.getBike_Image())
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(holder.imageBike);
    }

    @Override
    public int getItemCount() {
        return bikesUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener{

        public TextView tvBikeDate;
        public ImageView imageBike;
        public TextView tvBikeModel;
        public TextView tvBikeManufacturer;
        public TextView tvBikePrice;

        public ImageViewHolder(View itemView) {
            super(itemView);

            tvBikeDate = itemView.findViewById(R.id.tvAddBikeDate);
            imageBike = itemView.findViewById(R.id.imgShowBike);
            tvBikeModel = itemView.findViewById(R.id.tvAddBikeModel);
            tvBikeManufacturer = itemView.findViewById(R.id.tvAddBikeManufact);
            tvBikePrice = itemView.findViewById(R.id.tvAddBikePrice);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
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

        //create onItem click menu
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select an Action");
            MenuItem doShowMap  = menu.add(NONE, 1, 1, "Update");
            MenuItem doDelete  = menu.add(NONE, 2, 2, "Delete");

            doShowMap.setOnMenuItemClickListener(this);
            doDelete.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if(clickListener !=null){
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION){
                    switch (item.getItemId()){
                        case 1:
                            clickListener.onUpdateClick(position);
                            return true;

                        case 2:
                            clickListener.onDeleteClick(position);
                            return true;
                    }
                }
            }
            return false;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onUpdateClick(int position);

        void onDeleteClick(int position);
    }

    public void setOnItmClickListener(BikesAdapterAdmin.OnItemClickListener listener){
        clickListener = listener;
    }
}
