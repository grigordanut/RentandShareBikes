package com.example.rentandsharebikes;

import com.google.firebase.database.Exclude;

public class BikesShare {

    private String shareCus_FirstName;
    private String shareCus_LastName;
    private String shareCus_PhoneNo;
    private String shareCus_Email;
    private String shareBike_Image;
    private String shareBike_Condition;
    private String shareBike_Model;
    private String shareBike_Manufact;
    private double shareBike_Price;
    private String shareBike_DateAv;
    private String shareBikes_CustomId;
    private String shareBike_Key;

    public BikesShare(){

    }

    public BikesShare(String shareCus_FirstName, String shareCus_LastName, String shareCus_PhoneNo, String shareCus_Email, String shareBike_Image, String shareBike_Condition, String shareBike_Model, String shareBike_Manufact, double shareBike_Price, String shareBike_DateAv, String shareBikes_CustomId, String shareBike_Key) {
        this.shareCus_FirstName = shareCus_FirstName;
        this.shareCus_LastName = shareCus_LastName;
        this.shareCus_PhoneNo = shareCus_PhoneNo;
        this.shareCus_Email = shareCus_Email;
        this.shareBike_Image = shareBike_Image;
        this.shareBike_Condition = shareBike_Condition;
        this.shareBike_Model = shareBike_Model;
        this.shareBike_Manufact = shareBike_Manufact;
        this.shareBike_Price = shareBike_Price;
        this.shareBike_DateAv = shareBike_DateAv;
        this.shareBikes_CustomId = shareBikes_CustomId;
        this.shareBike_Key = shareBike_Key;
    }

    public String getShareCus_FirstName() {
        return shareCus_FirstName;
    }

    public void setShareCus_FirstName(String shareCus_FirstName) {
        this.shareCus_FirstName = shareCus_FirstName;
    }

    public String getShareCus_LastName() {
        return shareCus_LastName;
    }

    public void setShareCus_LastName(String shareCus_LastName) {
        this.shareCus_LastName = shareCus_LastName;
    }

    public String getShareCus_PhoneNo() {
        return shareCus_PhoneNo;
    }

    public void setShareCus_PhoneNo(String shareCus_PhoneNo) {
        this.shareCus_PhoneNo = shareCus_PhoneNo;
    }

    public String getShareCus_Email() {
        return shareCus_Email;
    }

    public void setShareCus_Email(String shareCus_Email) {
        this.shareCus_Email = shareCus_Email;
    }

    public String getShareBike_Image() {
        return shareBike_Image;
    }

    public void setShareBike_Image(String shareBike_Image) {
        this.shareBike_Image = shareBike_Image;
    }

    public String getShareBike_Condition() {
        return shareBike_Condition;
    }

    public void setShareBike_Condition(String shareBike_Condition) {
        this.shareBike_Condition = shareBike_Condition;
    }

    public String getShareBike_Model() {
        return shareBike_Model;
    }

    public void setShareBike_Model(String shareBike_Model) {
        this.shareBike_Model = shareBike_Model;
    }

    public String getShareBike_Manufact() {
        return shareBike_Manufact;
    }

    public void setShareBike_Manufact(String shareBike_Manufact) {
        this.shareBike_Manufact = shareBike_Manufact;
    }

    public double getShareBike_Price() {
        return shareBike_Price;
    }

    public void setShareBike_Price(double shareBike_Price) {
        this.shareBike_Price = shareBike_Price;
    }

    public String getShareBike_DateAv() {
        return shareBike_DateAv;
    }

    public void setShareBike_DateAv(String shareBike_DateAv) {
        this.shareBike_DateAv = shareBike_DateAv;
    }

    public String getShareBikes_CustomId() {
        return shareBikes_CustomId;
    }

    public void setShareBikes_CustomId(String shareBikes_CustomId) {
        this.shareBikes_CustomId = shareBikes_CustomId;
    }

    @Exclude
    public String getShareBike_Key() {
        return shareBike_Key;
    }

    @Exclude
    public void setShareBike_Key(String shareBike_Key) {
        this.shareBike_Key = shareBike_Key;
    }
}
