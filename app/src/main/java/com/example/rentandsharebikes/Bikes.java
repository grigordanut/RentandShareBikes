package com.example.rentandsharebikes;

import com.google.firebase.database.Exclude;

public class Bikes {

    private String bike_Condition;
    private String bike_Model;
    private String bike_Manufacturer;
    private double bike_Price;
    private String bike_Image;
    private String bikeStoreName;
    private String bikeStoreKey;
    private String bike_Key;

    public Bikes(){

    }

    public Bikes(String bike_Condition, String bike_Model, String bike_Manufacturer, double bike_Price, String bike_Image, String bikeStoreName, String bikeStoreKey) {
        this.bike_Condition = bike_Condition;
        this.bike_Model = bike_Model;
        this.bike_Manufacturer = bike_Manufacturer;
        this.bike_Price = bike_Price;
        this.bike_Image = bike_Image;
        this.bikeStoreName = bikeStoreName;
        this.bikeStoreKey = bikeStoreKey;
    }

    public String getBike_Condition() {
        return bike_Condition;
    }

    public void setBike_Condition(String bike_Condition) {
        this.bike_Condition = bike_Condition;
    }

    public String getBike_Model() {
        return bike_Model;
    }

    public void setBike_Model(String bike_Model) {
        this.bike_Model = bike_Model;
    }

    public String getBike_Manufacturer() {
        return bike_Manufacturer;
    }

    public void setBike_Manufacturer(String bike_Manufacturer) {
        this.bike_Manufacturer = bike_Manufacturer;
    }

    public double getBike_Price() {
        return bike_Price;
    }

    public void setBike_Price(double bike_Price) {
        this.bike_Price = bike_Price;
    }

    public String getBike_Image() {
        return bike_Image;
    }

    public void setBike_Image(String bike_Image) {
        this.bike_Image = bike_Image;
    }

    public String getBikeStoreName() {
        return bikeStoreName;
    }

    public void setBikeStoreName(String bikeStoreName) {
        this.bikeStoreName = bikeStoreName;
    }

    public String getBikeStoreKey() {
        return bikeStoreKey;
    }

    public void setBikeStoreKey(String bikeStoreKey) {
        this.bikeStoreKey = bikeStoreKey;
    }

    @Exclude
    public String getBike_Key() {
        return bike_Key;
    }

    @Exclude
    public void setBike_Key(String bike_Key) {
        this.bike_Key = bike_Key;
    }
}
