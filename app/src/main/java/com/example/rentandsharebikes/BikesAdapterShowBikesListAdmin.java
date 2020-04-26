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

public class BikesAdapterShowBikesListAdmin extends RecyclerView.Adapter<BikesAdapterShowBikesListAdmin.ImageViewHolder> {

    private Context bikesContext;
    private List<Bikes> bikesUploads;
    private OnItemClickListener clickListener;

    public BikesAdapterShowBikesListAdmin(Context bikes_context, List<Bikes> bikes_uploads){
        bikesContext = bikes_context;
        bikesUploads = bikes_uploads;
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

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener{

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
            MenuItem doUpdate  = menu.add(NONE, 1, 1, "Update");
            MenuItem doDelete  = menu.add(NONE, 2, 2, "Delete");

            doUpdate.setOnMenuItemClickListener(this);
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

    public void setOnItmClickListener(OnItemClickListener listener){
        clickListener = listener;
    }
}
