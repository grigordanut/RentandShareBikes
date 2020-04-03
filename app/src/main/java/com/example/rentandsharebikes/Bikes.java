package com.example.rentandsharebikes;

import com.google.firebase.database.Exclude;

public class Bikes {

    private String bike_Date;
    private int bike_Number;
    private String bike_Model;
    private String bike_Manufacturer;
    private int bike_Price;
    private String bike_Image;
    private String bikeStoreKey;

    private String bikesKey;

    public Bikes(){

    }

    public Bikes(String bike_Date, int bike_Number, String bike_Model, String bike_Manufacturer, int bike_Price, String bike_Image, String bikeStoreKey) {
        this.bike_Date = bike_Date;
        this.bike_Number = bike_Number;
        this.bike_Model = bike_Model;
        this.bike_Manufacturer = bike_Manufacturer;
        this.bike_Price = bike_Price;
        this.bike_Image = bike_Image;
        this.bikeStoreKey = bikeStoreKey;
    }

    public String getBike_Date() {
        return bike_Date;
    }

    public void setBike_Date(String bike_Date) {
        this.bike_Date = bike_Date;
    }

    public int getBike_Number() {
        return bike_Number;
    }

    public void setBike_Number(int bike_Number) {
        this.bike_Number = bike_Number;
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

    public int getBike_Price() {
        return bike_Price;
    }

    public void setBike_Price(int bike_Price) {
        this.bike_Price = bike_Price;
    }

    public String getBike_Image() {
        return bike_Image;
    }

    public void setBike_Image(String bike_Image) {
        this.bike_Image = bike_Image;
    }

    public String getBikeStoreKey() {
        return bikeStoreKey;
    }

    public void setBikeStoreKey(String bikeStoreKey) {
        this.bikeStoreKey = bikeStoreKey;
    }

    @Exclude
    public String getBikesKey(){
        return bikesKey;
    }

    @Exclude
    public void setBikesKey(String bikesKey){
        this.bikesKey = bikesKey;
    }

    public class NONE {
    }
}
