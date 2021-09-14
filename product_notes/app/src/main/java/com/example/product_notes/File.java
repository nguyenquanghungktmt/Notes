package com.example.product_notes;

public class File {
    private String uri;
    private int id_note;

    public File(String uri, int id_note) {
        this.uri = uri;
        this.id_note = id_note;
    }

    public int getId_note() {
        return id_note;
    }

    public void setId_note(int id_note) {
        this.id_note = id_note;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public File(String uri) {
        this.uri = uri;
    }
}
