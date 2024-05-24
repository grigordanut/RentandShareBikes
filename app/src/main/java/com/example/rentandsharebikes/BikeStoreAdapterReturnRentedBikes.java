package com.example.rentandsharebikes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.Objects;

public class BikeStoreAdapterReturnRentedBikes extends ArrayAdapter<BikeStores> {

    private Context mContext;
    private int mResorces;

//    private int lastPosition = -1;
//    private int row_index = -1;

    public BikeStoreAdapterReturnRentedBikes(@NonNull Context context, int resources, @NonNull List<BikeStores> objects) {
        super(context, resources, objects);

        this.mContext = context;
        this.mResorces = resources;
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable @org.jetbrains.annotations.Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        convertView = layoutInflater.inflate(mResorces, parent, false);

        TextView tVBikeStoreNameSpin = convertView.findViewById(R.id.tvBikeStoreNameSpin);

        tVBikeStoreNameSpin.setText(Objects.requireNonNull(getItem(position)).getBikeStore_Location());

//        convertView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                row_index = position;
//                notifyDataSetChanged();
//            }
//        });
//
//        if (row_index == position) {
//            //convertView.setBackgroundColor(Color.GREEN);
//            tVBikeStoreNameSpin.setTextColor(Color.GREEN);
//        }
//
//        else {
//            tVBikeStoreNameSpin.setTextColor(Color.BLACK);
//        }

        return convertView;
    }
}
