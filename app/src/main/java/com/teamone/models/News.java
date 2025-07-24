package com.teamone.models;

import com.google.firebase.firestore.DocumentId;

import java.io.Serializable;

public class News implements Serializable {
    @DocumentId
    public String id;
    public String title;
    public String image;
    public News() {}
    public News(String title, String image) {
        this.title = title;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }

    public String getImage() {
        return image;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
