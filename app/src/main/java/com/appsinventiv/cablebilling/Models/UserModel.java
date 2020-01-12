package com.appsinventiv.cablebilling.Models;

public class UserModel {
    String name,phone,address;
    int bill;
    long time;

    public UserModel(String name, String phone, String address, int bill, long time) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.bill = bill;
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public UserModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getBill() {
        return bill;
    }

    public void setBill(int bill) {
        this.bill = bill;
    }
}
