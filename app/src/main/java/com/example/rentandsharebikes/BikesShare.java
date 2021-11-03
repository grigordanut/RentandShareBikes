package com.example.rentandsharebikes;

public class BikesShare {

    private String shareCus_FirstName;
    private String shareCus_LastName;
    private String shareCus_PhoneNo;
    private String shareCus_EmailAdd;
    private String shareBike_Condition;
    private String shareBike_Model;
    private String shareBike_Manufact;
    private double shareBike_Price;
    private String shareBike_DateAv;
    private String shareBike_Image;
    private String shareBikes_CustomId;
    private String shareBike_Key;

    public BikesShare(){

    }

    public BikesShare(String shareCus_FirstName, String shareCus_LastName, String shareCus_PhoneNo,
                      String shareCus_EmailAdd, String shareBike_Condition, String shareBike_Model,
                      String shareBike_Manufact, double shareBike_Price, String shareBike_DateAv,
                      String shareBike_Image, String shareBikes_CustomId, String shareBike_Key) {
        this.shareCus_FirstName = shareCus_FirstName;
        this.shareCus_LastName = shareCus_LastName;
        this.shareCus_PhoneNo = shareCus_PhoneNo;
        this.shareCus_EmailAdd = shareCus_EmailAdd;
        this.shareBike_Condition = shareBike_Condition;
        this.shareBike_Model = shareBike_Model;
        this.shareBike_Manufact = shareBike_Manufact;
        this.shareBike_Price = shareBike_Price;
        this.shareBike_DateAv = shareBike_DateAv;
        this.shareBike_Image = shareBike_Image;
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

    public String getShareCus_EmailAdd() {
        return shareCus_EmailAdd;
    }

    public void setShareCus_EmailAdd(String shareCus_EmailAdd) {
        this.shareCus_EmailAdd = shareCus_EmailAdd;
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

    public String getShareBike_Image() {
        return shareBike_Image;
    }

    public void setShareBike_Image(String shareBike_Image) {
        this.shareBike_Image = shareBike_Image;
    }

    public String getShareBikes_CustomId() {
        return shareBikes_CustomId;
    }

    public void setShareBikes_CustomId(String shareBikes_CustomId) {
        this.shareBikes_CustomId = shareBikes_CustomId;
    }

    public String getShareBike_Key() {
        return shareBike_Key;
    }

    public void setShareBike_Key(String shareBike_Key) {
        this.shareBike_Key = shareBike_Key;
    }
}
