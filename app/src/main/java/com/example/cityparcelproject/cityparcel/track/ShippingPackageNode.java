package com.example.cityparcelproject.cityparcel.track;

public class ShippingPackageNode {

    private int index;
    private String title;
    private String destination;
    private String price;
    private String deliveryman;

    public ShippingPackageNode(String title, String destination, String price, int index, String deliveryman) {
        this.title = title;
        this.destination = destination;
        this.price = price;
        this.index = index;
        this.deliveryman = deliveryman;
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

    public String getDeliveryman() {return  deliveryman;}
}
