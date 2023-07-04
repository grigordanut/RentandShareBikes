package com.example.rentandsharebikes;

public class Admins {

    private String firstName_Admin;
    private String lastName_Admin;
    private String userName_Admin;
    private String phoneNumb_Admin;
    private String email_Admin;

    public Admins() {

    }

    public Admins(String firstName_Admin, String lastName_Admin, String userName_Admin, String phoneNumb_Admin, String email_Admin) {
        this.firstName_Admin = firstName_Admin;
        this.lastName_Admin = lastName_Admin;
        this.userName_Admin = userName_Admin;
        this.phoneNumb_Admin = phoneNumb_Admin;
        this.email_Admin = email_Admin;
    }

    public String getFirstName_Admin() {
        return firstName_Admin;
    }

    public void setFirstName_Admin(String firstName_Admin) {
        this.firstName_Admin = firstName_Admin;
    }

    public String getLastName_Admin() {
        return lastName_Admin;
    }

    public void setLastName_Admin(String lastName_Admin) {
        this.lastName_Admin = lastName_Admin;
    }

    public String getUserName_Admin() {
        return userName_Admin;
    }

    public void setUserName_Admin(String userName_Admin) {
        this.userName_Admin = userName_Admin;
    }

    public String getPhoneNumb_Admin() {
        return phoneNumb_Admin;
    }

    public void setPhoneNumb_Admin(String phoneNumb_Admin) {
        this.phoneNumb_Admin = phoneNumb_Admin;
    }

    public String getEmail_Admin() {
        return email_Admin;
    }

    public void setEmail_Admin(String email_Admin) {
        this.email_Admin = email_Admin;
    }
}
