package com.teamone.models;

import com.google.firebase.firestore.DocumentId;

import java.io.Serializable;

public class NailRec implements Serializable {
    @DocumentId
    String id;
    String name;
    String description;
    String productID;
    public NailRec(){}

    public NailRec(String id, String name, String description, String productID) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.productID = productID;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getProductID() {
        return productID;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }
}
