package com.appsinventiv.cablebilling.Models;

public class AgentModel {
    String name,address,phone,password;
    long time;
    String admin;

    public AgentModel(String name, String phone , String password , String address, long time, String admin) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.password = password;
        this.time = time;
        this.admin = admin;
    }

    public AgentModel() {
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
