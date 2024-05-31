package com.example.rentandsharebikes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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

    private int row_index = -1;

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

        TextView tVBikeStoreName = convertView.findViewById(R.id.tvBikeStoreName);

        tVBikeStoreName.setText(Objects.requireNonNull(getItem(position)).getBikeStore_Location());

        convertView.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                row_index = position;
                notifyDataSetChanged();

                return false;
            }
        });

        if (row_index == position) {

            //convertView.setBackgroundColor(Color.rgb(216, 27, 96));
            tVBikeStoreName.setBackgroundColor(Color.GREEN);
            tVBikeStoreName.setTextColor(Color.BLUE);

        } else {
            tVBikeStoreName.setTextColor(Color.BLACK);
        }

        return convertView;
    }
}
