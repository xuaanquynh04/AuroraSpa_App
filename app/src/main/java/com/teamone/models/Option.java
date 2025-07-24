package com.teamone.models;

public class Option {
    Double addPrice;
    String name;
    public Option(){}

    public Option(Double addPrice, String name) {
        this.addPrice = addPrice;
        this.name = name;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Option)) return false;
        Option option = (Option) o;
        return name != null && name.equals(option.name);
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }


    public Double getAddPrice() {
        return addPrice;
    }

    public String getName() {
        return name;
    }

    public void setAddPrice(Double addPrice) {
        this.addPrice = addPrice;
    }

    public void setName(String name) {
        this.name = name;
    }
}
