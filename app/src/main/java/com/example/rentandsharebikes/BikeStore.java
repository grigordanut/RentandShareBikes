package com.example.rentandsharebikes;

import com.google.firebase.database.Exclude;

public class BikeStore {


    public String locationBike_Store;
    public String addressBike_Store;
    public String number_Slots;

    public String storeKey;

    public BikeStore(){

    }

    public BikeStore(String locationBike_Store, String addressBike_Store, String number_Slots) {
        this.locationBike_Store = locationBike_Store;
        this.addressBike_Store = addressBike_Store;
        this.number_Slots = number_Slots;
    }

    public String getLocationBike_Store() {
        return locationBike_Store;
    }

    public void setLocationBike_Store(String locationBike_Store) {
        this.locationBike_Store = locationBike_Store;
    }

    public String getAddressBike_Store() {
        return addressBike_Store;
    }

    public void setAddressBike_Store(String addressBike_Store) {
        this.addressBike_Store = addressBike_Store;
    }

    public String getNumber_Slots() {
        return number_Slots;
    }

    public void setNumber_Slots(String number_Slots) {
        this.number_Slots = number_Slots;
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
