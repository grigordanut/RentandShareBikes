package com.example.rentandsharebikes;

import com.google.firebase.database.Exclude;

public class BikeStore {

    private int bikeStore_Number;
    private String bikeStore_Location;
    private String bikeStore_Address;
    private int bikeStore_NumberSlots;

    private String storeKey;

    public BikeStore(){

    }

    public BikeStore(int bikeStore_Number, String bikeStore_Location, String bikeStore_Address, int bikeStore_NumberSlots) {
        this.bikeStore_Number = bikeStore_Number;
        this.bikeStore_Location = bikeStore_Location;
        this.bikeStore_Address = bikeStore_Address;
        this.bikeStore_NumberSlots = bikeStore_NumberSlots;
    }

    public int getBikeStore_Number() {
        return bikeStore_Number;
    }

    public void setBikeStore_Number(int bikeStore_Number) {
        this.bikeStore_Number = bikeStore_Number;
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

    public int getBikeStore_NumberSlots() {
        return bikeStore_NumberSlots;
    }

    public void setBikeStore_NumberSlots(int bikeStore_NumberSlots) {
        this.bikeStore_NumberSlots = bikeStore_NumberSlots;
    }

    @Exclude
    public String getStoreKey(){
        return storeKey;
    }

    @Exclude
    public void setStoreKey(String storeKey){
        this.storeKey = storeKey;
    }

    public class NONE{
    }
}
