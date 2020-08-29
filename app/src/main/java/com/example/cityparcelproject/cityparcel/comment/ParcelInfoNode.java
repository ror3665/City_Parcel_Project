package com.example.cityparcelproject.cityparcel.comment;

public class ParcelInfoNode {

    private String writer;
    private String comment;


    public ParcelInfoNode(String writer, String comment) {
        this.writer = writer;
        this.comment = comment;
    }

    public String getWriter() {return writer; }

    public String getComment() {
        return comment;
    }

}
