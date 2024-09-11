package com.example.test;

public class Item {
    private String productName;
    private String serialNumber;
    private String position;
    private String delivery;
    private String branch;
    private boolean isLoaded;

    // Getters and Setters
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getDelivery() {
        return delivery;
    }

    public void setDelivery(String delivery) {
        this.delivery = delivery;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public void setLoaded(boolean loaded) {
        isLoaded = loaded;
    }

    public Item(String productName, String serialNumber, String position, String delivery, String branch, boolean isLoaded) {
        this.productName = productName;
        this.serialNumber = serialNumber;
        this.position = position;
        this.delivery = delivery;
        this.branch = branch;
        this.isLoaded = isLoaded;
    }
}
