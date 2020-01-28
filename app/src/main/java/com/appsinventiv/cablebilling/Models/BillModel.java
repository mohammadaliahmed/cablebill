package com.appsinventiv.cablebilling.Models;

public class BillModel {
    String id;
    int billAmount;
    String name, phone,address;
    long time;
    String billById,billTo,admin;

    public BillModel(String id, int billAmount, long time, String name, String phone, String address, String billById, String billTo, String admin) {
        this.id = id;
        this.billAmount = billAmount;
        this.time = time;
        this.name = name;
        this.phone = phone;
        this.billById = billById;
        this.billTo = billTo;
        this.admin = admin;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public BillModel() {
    }

    public String getBillTo() {
        return billTo;
    }

    public void setBillTo(String billTo) {
        this.billTo = billTo;
    }

    public String getBillById() {
        return billById;
    }

    public void setBillById(String billById) {
        this.billById = billById;
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
