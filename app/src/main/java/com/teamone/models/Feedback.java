package com.teamone.models;

import com.google.firebase.Timestamp;

import java.io.Serializable;

public class Feedback implements Serializable {
    String productID;
    String customerID;
    String content;
    int rating;
    Timestamp date;

    public Feedback(String productID, String customerID, String content, int rating, Timestamp date) {
        this.productID = productID;
        this.customerID = customerID;
        this.content = content;
        this.rating = rating;
        this.date = date;
    }

    public Feedback(){}

    public String getProductID() {
        return productID;
    }

    public String getCustomerID() {
        return customerID;
    }

    public String getContent() {
        return content;
    }

    public int getRating() {
        return rating;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }
}
