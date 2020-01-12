package com.appsinventiv.cablebilling.Models;

public class BillModel {
    String id;
    int billAmount;
    String name, phone,address;
    long time;

    public BillModel(String id, int billAmount, long time, String name, String phone, String address) {
        this.id = id;
        this.billAmount = billAmount;
        this.time = time;
        this.name = name;
        this.phone = phone;
        this.address = address;
    }

    public BillModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getBillAmount() {
        return billAmount;
    }

    public void setBillAmount(int billAmount) {
        this.billAmount = billAmount;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
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
}
