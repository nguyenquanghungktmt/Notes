package com.example.product_notes;

import java.sql.Array;

public class Content {
    private int id;
    private String content;
    private int checked;
    private int id_note;

    public Content(String content) {
        this.content = content;
    }

    public Content(String content, int checked, int id_note) {
        this.content = content;
        this.checked = checked;
        this.id_note = id_note;
    }

    public Content(int id, String content, int checked, int id_note) {
        this.id = id;
        this.content = content;
        this.checked = checked;
        this.id_note = id_note;
    }

    public int getId_note() {
        return id_note;
    }

    public void setId_note(int id_note) {
        this.id_note = id_note;
    }

    public int getId() {
        return id;
    }

    public int getChecked() {
        return checked;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public void setChecked(int checked) {
        this.checked = checked;
    }

    @Override
    public String toString() {
        return "Content{" +
                "id=" + id +
                ", content='" + content + '\'' +
                '}';
    }
}
