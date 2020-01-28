package com.appsinventiv.cablebilling.Models;

public class ParchiModel {
    String title,address,picUrl;

    public ParchiModel(String title, String address, String picUrl) {
        this.title = title;
        this.address = address;
        this.picUrl = picUrl;
    }

    public ParchiModel() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }
}
