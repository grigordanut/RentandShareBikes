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
    public static void getAddress(final String locationAddress, final Context context, final Handler handler){
        Thread thread = new Thread(){
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                //String result = null;
                String resultStoreAddress = null;
                String resultLatitude = null;
                String resultLongitude = null;

                try {
                    List addressList = geocoder.getFromLocationName(locationAddress,1);
                    if (addressList != null && addressList.size() > 0 ){
                        Address address = (Address) addressList.get(0);
                        //StringBuilder stringBuilder = new StringBuilder();
                        StringBuilder stringBuilderStoreAddress = new StringBuilder();
                        StringBuilder stringBuilderLatitude = new StringBuilder();
                        StringBuilder stringBuilderLongitude = new StringBuilder();

                        stringBuilderLatitude.append(address.getLatitude()).append("\n");
                        stringBuilderLongitude.append(address.getLongitude()).append("\n");
                        //result = stringBuilder.toString();
                        resultLatitude = stringBuilderLatitude.toString();
                        resultLongitude = stringBuilderLongitude.toString();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    Message message = Message.obtain();
                    message.setTarget(handler);
                    if (resultLatitude != null && resultLongitude != null){
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        bundle.putString("locationAddress",locationAddress);
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
