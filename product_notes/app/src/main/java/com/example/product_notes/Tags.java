package com.example.product_notes;

public class Tags {
    int id;
    String title;
    String code;
    public Tags(String title, String code) {
        this.title = title;
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }



    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


    public Tags(int id, String title, String code) {
        this.id = id;
        this.title = title;
        this.code = code;
    }
}
