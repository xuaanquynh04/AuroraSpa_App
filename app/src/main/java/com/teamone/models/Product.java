package com.teamone.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Product implements Serializable {
    String productID;
    String productImage;
    String productName;
    Double price;
    Double oldPrice;
    String description;
    Boolean newProduct;
    Double duration;
    String creatorID;
    Double productUse;
    Double saleOff;
    int rate;
    String productTypeID;
    String tryImage;

    ArrayList<Feedback> feedbacks;

    public Product(String productID, String productImage, String productName, Double price, Double oldPrice, String description, Boolean newProduct, Double duration, String creatorID, Double productUse, Double saleOff, int rate, String productTypeID, ArrayList<Feedback> feedbacks, String tryImage) {
        this.productID = productID;
        this.productImage = productImage;
        this.productName = productName;
        this.price = price;
        this.oldPrice = oldPrice;
        this.description = description;
        this.newProduct = newProduct;
        this.duration = duration;
        this.creatorID = creatorID;
        this.productUse = productUse;
        this.saleOff = saleOff;
        this.rate = rate;
        this.productTypeID = productTypeID;
        this.feedbacks = feedbacks;
        this.tryImage = tryImage;
    }
    public Product(){}

    public String getTryImage() {
        return tryImage;
    }
    public String getProductID() {
        return productID;
    }

    public String getProductImage() {
        return productImage;
    }

    public String getProductName() {
        return productName;
    }

    public Double getPrice() {
        return price;
    }

    public Double getOldPrice() {
        return oldPrice;
    }

    public String getDescription() {
        return description;
    }

    public Boolean getNewProduct() {
        return newProduct;
    }

    public Double getDuration() {
        return duration;
    }

    public String getCreatorID() {
        return creatorID;
    }

    public Double getProductUse() {
        return productUse;
    }

    public Double getSaleOff() {
        return saleOff;
    }

    public int getRate() {
        return rate;
    }

    public String getProductTypeID() { return productTypeID; }

    public ArrayList<Feedback> getFeedbacks() {
        return feedbacks;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setOldPrice(Double oldPrice) {
        this.oldPrice = oldPrice;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setNewProduct(Boolean newProduct) {
        this.newProduct = newProduct;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }

    public void setCreatorID(String creatorID) {
        this.creatorID = creatorID;
    }

    public void setProductUse(Double productUse) {
        this.productUse = productUse;
    }

    public void setSaleOff(Double saleOff) {
        this.saleOff = saleOff;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public void setProductTypeID(String productTypeID) {
        this.productTypeID = productTypeID;
    }

    public void setFeedbacks(ArrayList<Feedback> feedbacks) {
        this.feedbacks = feedbacks;
    }

    public void setTryImage(String tryImage) {
        this.tryImage = tryImage;
    }
}
