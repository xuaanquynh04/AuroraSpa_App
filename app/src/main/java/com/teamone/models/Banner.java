package com.teamone.models;

public class Banner {
    String imgURL;
    public Banner() {}

    public Banner(String imgURL) {
        this.imgURL = imgURL;
    }
    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }
}
