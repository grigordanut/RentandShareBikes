package com.example.rentandsharebikes;

import com.google.firebase.database.Exclude;

public class BikeStore {

    private String bikeStore_Location;
    private String bikeStore_Address;
    private double bikeStore_Latitude;
    private double bikeStore_Longitude;
    private int bikeStore_NumberSlots;
    private String bikeStore_Key;

    public BikeStore(){

    }

    public BikeStore(String bikeStore_Location, String bikeStore_Address, double bikeStore_Latitude, double bikeStore_Longitude, int bikeStore_NumberSlots, String bikeStore_Key) {
        this.bikeStore_Location = bikeStore_Location;
        this.bikeStore_Address = bikeStore_Address;
        this.bikeStore_Latitude = bikeStore_Latitude;
        this.bikeStore_Longitude = bikeStore_Longitude;
        this.bikeStore_NumberSlots = bikeStore_NumberSlots;
        this.bikeStore_Key = bikeStore_Key;
    }

    public String getBikeStore_Location() {
        return bikeStore_Location;
    }

    public void setBikeStore_Location(String bikeStore_Location) {
        this.bikeStore_Location = bikeStore_Location;
    }

    public String getBikeStore_Address() {
        return bikeStore_Address;
    }

    public void setBikeStore_Address(String bikeStore_Address) {
        this.bikeStore_Address = bikeStore_Address;
    }

    public double getBikeStore_Latitude() {
        return bikeStore_Latitude;
    }

    public void setBikeStore_Latitude(double bikeStore_Latitude) {
        this.bikeStore_Latitude = bikeStore_Latitude;
    }

    public double getBikeStore_Longitude() {
        return bikeStore_Longitude;
    }

    public void setBikeStore_Longitude(double bikeStore_Longitude) {
        this.bikeStore_Longitude = bikeStore_Longitude;
    }

    public int getBikeStore_NumberSlots() {
        return bikeStore_NumberSlots;
    }

    public void setBikeStore_NumberSlots(int bikeStore_NumberSlots) {
        this.bikeStore_NumberSlots = bikeStore_NumberSlots;
    }

    public String getBikeStore_Key() {
        return bikeStore_Key;
    }

    public void setBikeStore_Key(String bikeStore_Key) {
        this.bikeStore_Key = bikeStore_Key;
    }

    //    //Exclude
//    @Exclude
//    public String getBikeStore_Key(){
//        return bikeStore_Key;
//    }
//
//    @Exclude
//    public void setBikeStore_Key(String bikeStore_Key){
//        this.bikeStore_Key = bikeStore_Key;
//    }
//
//    public class NONE{
//    }
}
