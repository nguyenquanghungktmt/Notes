package com.example.product_notes;

public class Categoties {
    int id;
    String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Categoties(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
