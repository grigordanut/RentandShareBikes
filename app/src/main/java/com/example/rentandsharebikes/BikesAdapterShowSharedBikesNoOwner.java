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

public class BikesAdapterShowSharedBikesNoOwner extends RecyclerView.Adapter<BikesAdapterShowSharedBikesNoOwner.ImageViewHolder>{

    private final Context bikesContext;
    private final List<ShareBikes> bikesUploads;

    private OnItemClickListener clickListener;

    public BikesAdapterShowSharedBikesNoOwner(Context bikes_context, List<ShareBikes> bikes_uploads){
        bikesContext = bikes_context;
        bikesUploads = bikes_uploads;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(bikesContext).inflate(R.layout.image_bikes_shared_no_owner,parent, false);
        return new ImageViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {

        final ShareBikes uploadCurrent = bikesUploads.get(position);
        holder.tvSharedBikesUserNameFull.setText(uploadCurrent.getShareCus_FirstName()+" "+uploadCurrent.getShareCus_LastName());
        holder.tvSharedBikesUserPhoneFull.setText(uploadCurrent.getShareCus_PhoneNo());
        holder.tvSharedBikesUserEmailFull.setText(uploadCurrent.getShareCus_EmailAdd());
        holder.tvSharedBikeCondUserFull.setText(uploadCurrent.getShareBike_Condition());
        holder.tvSharedBikeModelUserFull.setText(uploadCurrent.getShareBike_Model());
        holder.tvSharedBikeManufactUserFull.setText(uploadCurrent.getShareBike_Manufact());
        holder.tvSharedBikePriceUserFull.setText(String.valueOf(uploadCurrent.getShareBike_Price()));
        holder.tvSharedBikeDateAvUserFull.setText(uploadCurrent.getShareBike_DateAv());

        Picasso.get()
                .load(uploadCurrent.getShareBike_Image())
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(holder.imageSharedBikesUserFull);
    }

    @Override
    public int getItemCount() {
        return bikesUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public ImageView imageSharedBikesUserFull;
        public TextView tvSharedBikesUserNameFull;
        public TextView tvSharedBikesUserPhoneFull;
        public TextView tvSharedBikesUserEmailFull;
        public TextView tvSharedBikeCondUserFull;
        public TextView tvSharedBikeModelUserFull;
        public TextView tvSharedBikeManufactUserFull;
        public TextView tvSharedBikePriceUserFull;
        public TextView tvSharedBikeDateAvUserFull;

        public ImageViewHolder(View itemView) {
            super(itemView);

            imageSharedBikesUserFull = itemView.findViewById(R.id.imgSBikesCustomFull);
            tvSharedBikesUserNameFull = itemView.findViewById(R.id.tvSharedBikeCustomNameFull);
            tvSharedBikesUserPhoneFull = itemView.findViewById(R.id.tvCustomPhoneShareFull);
            tvSharedBikesUserEmailFull = itemView.findViewById(R.id.tvCustomEmailShareFull);
            tvSharedBikeCondUserFull = itemView.findViewById(R.id.tvBikeCondShareFull);
            tvSharedBikeModelUserFull = itemView.findViewById(R.id.tvBikeModelShareFull);
            tvSharedBikeManufactUserFull = itemView.findViewById(R.id.tvBikeManufactShareFull);
            tvSharedBikePriceUserFull = itemView.findViewById(R.id.tvBikePriceShareFull);
            tvSharedBikeDateAvUserFull = itemView.findViewById(R.id.tvDateAvShareFull);

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
