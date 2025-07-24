package com.teamone.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

import java.io.Serializable;

public class BookItem implements Serializable {
    CartItem item;
    Timestamp chosenTime;
    public BookItem() {}

    public BookItem(CartItem item, Timestamp chosenTime) {
        this.item = item;
        this.chosenTime = chosenTime;
    }

    public CartItem getItem() {
        return item;
    }

    public Timestamp getChosenTime() {
        return chosenTime;
    }

    public void setItem(CartItem item) {
        this.item = item;
    }

    public void setChosenTime(Timestamp chosenTime) {
        this.chosenTime = chosenTime;
    }

}
