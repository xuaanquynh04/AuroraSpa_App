package com.teamone.models;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.ArrayList;

public class CartItem implements Serializable {
    @DocumentId
    String id;
    String productName;
    String productID;
    String productImage;
    Double basePrice;
    Double finalPrice;
    String customerID;
    ArrayList<Option> selectedOptions;
    String productTypeID;
    Timestamp addTime;
    int quantity;
    public CartItem(){}

    public CartItem(String productName, String productID, String productImage, Double basePrice, Double finalPrice, String customerID, ArrayList<Option> selectedOptions, String productTypeID, Timestamp addTime, int quantity) {
        this.productName = productName;
        this.productID = productID;
        this.productImage = productImage;
        this.basePrice = basePrice;
        this.finalPrice = finalPrice;
        this.customerID = customerID;
        this.selectedOptions = selectedOptions;
        this.productTypeID = productTypeID;
        this.addTime = addTime;
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductID() {
        return productID;
    }
    public String getProductImage(){ return productImage; }

    public Double getBasePrice() {
        return basePrice;
    }

    public Double getFinalPrice() {
        return finalPrice;
    }

    public String getCustomerID() {
        return customerID;
    }

    public ArrayList<Option> getSelectedOptions() {
        return selectedOptions;
    }

    public String getProductTypeID() {
        return productTypeID;
    }

    public Timestamp getAddTime() {
        return addTime;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }
    public void setProductImage (String productImage) { this.productImage = productImage; }

    public void setBasePrice(Double basePrice) {
        this.basePrice = basePrice;
    }

    public void setFinalPrice(Double finalPrice) {
        this.finalPrice = finalPrice;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public void setSelectedOptions(ArrayList<Option> selectedOptions) {
        this.selectedOptions = selectedOptions;
    }

    public void setProductTypeID(String productTypeID) {
        this.productTypeID = productTypeID;
    }

    public void setAddTime(Timestamp addTime) {
        this.addTime = addTime;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
