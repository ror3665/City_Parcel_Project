package com.example.cityparcelproject.cityparcel.track;

public class ScheduledPackageNode {

    private int index;
    private String title;
    private String destination;
    private String price;

    public ScheduledPackageNode(String title, String destination, String price, int index) {
        this.title = title;
        this.destination = destination;
        this.price = price;
        this.index = index;
    }

    public int getIndex() {return index; }

    public String getTitle() {
        return title;
    }

    public String getDestination() {
        return destination;
    }

    public String getPrice() {
        return price;
    }

}
