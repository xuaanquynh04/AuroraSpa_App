package com.teamone.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;

import java.io.Serializable;
import java.util.ArrayList;


public class Book implements Serializable {
    @DocumentId
    String id;
    String customerID;
    String customerName;
    String phone;
    Timestamp addTime;
    Timestamp bookDate;
    ArrayList<BookItem> bookItems;
    String paymentMethod;
    String bookStatus;
    Double total;
    public Book(){}

    public Book(String customerID, String customerName, String phone, Timestamp addTime, Timestamp bookDate, ArrayList<BookItem> bookItems, String paymentMethod, String bookStatus, Double total) {
        this.customerID = customerID;
        this.customerName = customerName;
        this.phone = phone;
        this.addTime = addTime;
        this.bookDate = bookDate;
        this.bookItems = bookItems;
        this.paymentMethod = paymentMethod;
        this.bookStatus = bookStatus;
        this.total = total;
    }

    public String getId() {
        return id;
    }

    public String getCustomerID() {
        return customerID;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getPhone() {
        return phone;
    }

    public Timestamp getAddTime() {
        return addTime;
    }

    public Timestamp getBookDate() {
        return bookDate;
    }

    public ArrayList<BookItem> getBookItems() {
        return bookItems;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getBookStatus() {
        return bookStatus;
    }
    public Double getTotal() {return total; }

    public void setId(String id) {
        this.id = id;
    }
    public void setTotal(Double id) { this.total = total; }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddTime(Timestamp addTime) {
        this.addTime = addTime;
    }

    public void setBookDate(Timestamp bookDate) {
        this.bookDate = bookDate;
    }

    public void setBookItems(ArrayList<BookItem> bookItems) {
        this.bookItems = bookItems;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setBookStatus(String bookStatus) {
        this.bookStatus = bookStatus;
    }
}
