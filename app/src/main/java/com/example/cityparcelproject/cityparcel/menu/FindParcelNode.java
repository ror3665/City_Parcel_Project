package com.example.cityparcelproject.cityparcel.menu;

public class FindParcelNode {

    private int index;
    private String title;
    private String destination;
    private String price;
    private String member;

    public FindParcelNode(int index, String title, String destination, String price, String member) {
        this.index = index;
        this.title = title;
        this.destination = destination;
        this.price = price;
        this.member = member;
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

    public String getMember() {return member;}
}
