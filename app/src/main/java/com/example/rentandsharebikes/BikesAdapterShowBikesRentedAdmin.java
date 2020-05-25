package com.example.rentandsharebikes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class BikesAdapterShowBikesRentedAdmin extends RecyclerView.Adapter<BikesAdapterShowBikesRentedAdmin.ImageViewHolder> {

    private Context bikesContext;
    private List<RentBikes> bikesUploads;

    public BikesAdapterShowBikesRentedAdmin(Context bikes_context, List<RentBikes> bikes_uploads) {
        bikesContext = bikes_context;
        bikesUploads = bikes_uploads;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(bikesContext).inflate(R.layout.image_bikes_rented_admin, parent, false);
        return new ImageViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {

        final RentBikes uploadCurrent = bikesUploads.get(position);
        holder.tvRentedBikeConditionAdmin.setText(uploadCurrent.getBikeCond_RentBikes());
        holder.tvRentedBikeModelAdmin.setText(uploadCurrent.getBikeModel_RentBikes());
        holder.tvRentedBikeManufacturerAdmin.setText(uploadCurrent.getBikeManufact_RentBikes());
        holder.tvRentedBikePriceAdmin.setText(String.valueOf(+uploadCurrent.getBikePrice_RentBikes()));
        holder.tvRentedBikeStoreNameAdmin.setText(uploadCurrent.getStoreLocation_RentBikes());
        holder.tvRentedBikeCustomNameAdmin.setText(uploadCurrent.getfName_RentBikes() + " " + uploadCurrent.getlName_RentBikes());
        holder.tvRentedBikeCustomPhoneAdmin.setText(uploadCurrent.getPhoneNo_RentBikes());
        holder.tvRentedBikeCustomEmailAdmin.setText(uploadCurrent.getEmail_RentBikes());
        holder.tvRentedBikeDateRentAdmin.setText(uploadCurrent.getDate_RentBikes());

        Picasso.get()
                .load(uploadCurrent.getBikeImage_RentBike())
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(holder.imageRentedBikeAdmin);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //bikesContext = BikesAdapterShowBikesRentedAdmin.this;
                LayoutInflater li = LayoutInflater.from(bikesContext);
                View promptsView = li.inflate(R.layout.image_bike_full, null);

                androidx.appcompat.app.AlertDialog.Builder
                alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(bikesContext);

                alertDialogBuilder.setView(promptsView);

                final ImageView img_full = (ImageView) promptsView.findViewById(R.id.imgImageFull);

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Picasso.get()
                                                .load(uploadCurrent.getBikeImage_RentBike())
                                                .placeholder(R.mipmap.ic_launcher)
                                                .fit()
                                                .centerCrop()
                                                .into(img_full);


                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return bikesUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageRentedBikeAdmin;
        public TextView tvRentedBikeConditionAdmin;
        public TextView tvRentedBikeModelAdmin;
        public TextView tvRentedBikeManufacturerAdmin;
        public TextView tvRentedBikePriceAdmin;
        public TextView tvRentedBikeStoreNameAdmin;
        public TextView tvRentedBikeCustomNameAdmin;
        public TextView tvRentedBikeCustomPhoneAdmin;
        public TextView tvRentedBikeCustomEmailAdmin;
        public TextView tvRentedBikeDateRentAdmin;

        public ImageViewHolder(View itemView) {
            super(itemView);

            imageRentedBikeAdmin = itemView.findViewById(R.id.imgRentedBikesAdmin);
            tvRentedBikeConditionAdmin = itemView.findViewById(R.id.tvBikeCondRentAdmin);
            tvRentedBikeModelAdmin = itemView.findViewById(R.id.tvBikeModelRentAdmin);
            tvRentedBikeManufacturerAdmin = itemView.findViewById(R.id.tvBikeManufactRentAdmin);
            tvRentedBikePriceAdmin = itemView.findViewById(R.id.tvBikePriceRentAdmin);
            tvRentedBikeStoreNameAdmin = itemView.findViewById(R.id.tvBikeStoreRentAdmin);
            tvRentedBikeCustomNameAdmin = itemView.findViewById(R.id.tvCustomNameRentAdmin);
            tvRentedBikeCustomPhoneAdmin = itemView.findViewById(R.id.tvCustomPhoneRentAdmin);
            tvRentedBikeCustomEmailAdmin = itemView.findViewById(R.id.tvCustomEmailRentAdmin);
            tvRentedBikeDateRentAdmin = itemView.findViewById(R.id.tvBikeDateRentAdmin);
        }
    }
}
