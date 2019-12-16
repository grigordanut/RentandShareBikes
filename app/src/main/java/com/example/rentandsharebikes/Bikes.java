package com.example.rentandsharebikes;

import com.google.firebase.database.Exclude;

public class Bikes {

    private String addBike_Date;
    private String addBike_Model;
    private String addBike_Manufacturer;
    private String addBike_Price;
    private String addBike_Image;
    private String bikeStoreName;
    private String bikesKey;

    public Bikes(){

    }

    public Bikes(String addBike_Date, String addBike_Model, String addBike_Manufacturer, String addBike_Price, String addBike_Image, String bikeStoreName) {
        this.addBike_Date = addBike_Date;
        this.addBike_Model = addBike_Model;
        this.addBike_Manufacturer = addBike_Manufacturer;
        this.addBike_Price = addBike_Price;
        this.addBike_Image = addBike_Image;
        this.bikeStoreName = bikeStoreName;
    }

    public String getAddBike_Date() {
        return addBike_Date;
    }

    public void setAddBike_Date(String addBike_Date) {
        this.addBike_Date = addBike_Date;
    }

    public String getAddBike_Model() {
        return addBike_Model;
    }

    public void setAddBike_Model(String addBike_Model) {
        this.addBike_Model = addBike_Model;
    }

    public String getAddBike_Manufacturer() {
        return addBike_Manufacturer;
    }

    public void setAddBike_Manufacturer(String addBike_Manufacturer) {
        this.addBike_Manufacturer = addBike_Manufacturer;
    }

    public String getAddBike_Price() {
        return addBike_Price;
    }

    public void setAddBike_Price(String addBike_Price) {
        this.addBike_Price = addBike_Price;
    }

    public String getAddBike_Image() {
        return addBike_Image;
    }

    public void setAddBike_Image(String addBike_Image) {
        this.addBike_Image = addBike_Image;
    }

    public String getBikeStoreName() {
        return bikeStoreName;
    }

    public void setBikeStoreName(String bikeStoreName) {
        this.bikeStoreName = bikeStoreName;
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
