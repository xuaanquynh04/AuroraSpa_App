package com.teamone.models;

import com.google.firebase.firestore.DocumentId;

import java.util.ArrayList;

public class Custom {
    @DocumentId
    String id;
    String groupName;
    String name;
    String optionType;
    ArrayList<Option> options;
    String productTypeID;
    boolean required;
    public Custom(){}

    public Custom(String groupName, String name, String optionType, ArrayList<Option> options, String productTypeID, boolean required) {
        this.groupName = groupName;
        this.name = name;
        this.optionType = optionType;
        this.options = options;
        this.productTypeID = productTypeID;
        this.required = required;
    }


    public String getId() {
        return id;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getName() {
        return name;
    }

    public String getOptionType() {
        return optionType;
    }

    public ArrayList<Option> getOptions() {
        return options;
    }

    public String getProductTypeID() {
        return productTypeID;
    }

    public boolean isRequired() {
        return required;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOptionType(String optionType) {
        this.optionType = optionType;
    }

    public void setOptions(ArrayList<Option> options) {
        this.options = options;
    }

    public void setProductTypeID(String productTypeID) {
        this.productTypeID = productTypeID;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }
}
