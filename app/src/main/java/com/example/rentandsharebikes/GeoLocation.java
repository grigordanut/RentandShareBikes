package com.example.rentandsharebikes;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;


import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GeoLocation {
    public static void getAddress(final String locationAddress, final Context context, final Handler handler) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                String resultLatitude = null;
                String resultLongitude = null;

                try {
                    List<Address> addressList = geocoder.getFromLocationName(locationAddress, 1);
                    if (addressList != null && addressList.size() > 0) {
                        Address address = addressList.get(0);

                        resultLatitude = address.getLatitude() + "\n";
                        resultLongitude = address.getLongitude() + "\n";
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    Message message = Message.obtain();
                    message.setTarget(handler);
                    if (resultLatitude != null && resultLongitude != null) {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        bundle.putString("locationAddress", locationAddress);
                        bundle.putString("addressLat", resultLatitude);
                        bundle.putString("addressLong", resultLongitude);

                        message.setData(bundle);
                    }
                    message.sendToTarget();

                }
            }
        };
        thread.start();
    }
}
