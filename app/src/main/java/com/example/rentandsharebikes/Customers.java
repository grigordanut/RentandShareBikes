package com.example.rentandsharebikes;

public class Customers {

    public String fName_Customer;
    public String lName_Customer;
    public String uName_Customer;
    public String phoneNumb_Customer;
    public String email_Customer;

    public Customers(){

    }

    public Customers(String fName_Customer, String lName_Customer, String uName_Customer, String phoneNumb_Customer, String email_Customer) {
        this.fName_Customer = fName_Customer;
        this.lName_Customer = lName_Customer;
        this.uName_Customer = uName_Customer;
        this.phoneNumb_Customer = phoneNumb_Customer;
        this.email_Customer = email_Customer;
    }

    public String getfName_Customer() {
        return fName_Customer;
    }

    public void setfName_Customer(String fName_Customer) {
        this.fName_Customer = fName_Customer;
    }

    public String getlName_Customer() {
        return lName_Customer;
    }

    public void setlName_Customer(String lName_Customer) {
        this.lName_Customer = lName_Customer;
    }

    public String getuName_Customer() {
        return uName_Customer;
    }

    public void setuName_Customer(String uName_Customer) {
        this.uName_Customer = uName_Customer;
    }

    public String getPhoneNumb_Customer() {
        return phoneNumb_Customer;
    }

    public void setPhoneNumb_Customer(String phoneNumb_Customer) {
        this.phoneNumb_Customer = phoneNumb_Customer;
    }

    public String getEmail_Customer() {
        return email_Customer;
    }

    public void setEmail_Customer(String email_Customer) {
        this.email_Customer = email_Customer;
    }
}
