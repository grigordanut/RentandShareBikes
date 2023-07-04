package com.example.rentandsharebikes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BikeStoreAdapterReturnBikesNew extends ArrayAdapter<BikeStores> {

    private Context mContext;
    private int mResorces;

    public BikeStoreAdapterReturnBikesNew(@NonNull Context context, int resources, @NonNull List<BikeStores> objects) {
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
//                tVBikeStoreNameSpin.setTextColor(Color.GREEN);
//            }
//        });

        return convertView;
    }
}
