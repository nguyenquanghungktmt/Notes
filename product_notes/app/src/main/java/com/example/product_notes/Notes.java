package com.example.product_notes;


import android.os.Parcel;
import android.os.Parcelable;

public class Notes implements Parcelable {
    int id;
    String title;
    String content;
    String tag;
    String type;
    String location;
    int is_archive;
    String reminder;
    String created_at;
    String updated_at;


    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    public Notes(int id, String title, String content, String tag, String type, String location,int is_archive, String reminder, String created_at, String updated_at) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.tag = tag;
        this.type = type;
        this.location = location;
        this.is_archive = is_archive;
        this.reminder = reminder;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public int getIs_archive() {
        return is_archive;
    }

    public void setIs_archive(int is_archive) {
        this.is_archive = is_archive;
    }

    public static Creator getCREATOR() {
        return CREATOR;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getReminder() {
        return reminder;
    }

    public void setReminder(String reminder) {
        this.reminder = reminder;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public Notes(String title, String content, String tag,String type,String location,int is_archive, String reminder, String created_at, String updated_at) {
        this.title = title;
        this.content = content;
        this.tag = tag;
        this.type = type;
        this.location = location;
        this.is_archive = is_archive;
        this.reminder = reminder;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }
    public Notes(Parcel in){
        String[] data = new String[10];

        in.readStringArray(data);
        // the order needs to be the same as in writeToParcel() method
        this.id = Integer.valueOf(data[0]);
        this.title = data[1];
        this.content = data[2];
        this.tag = data[3];
        this.type = data[4];
        this.location = data[5];
        this.is_archive = Integer.valueOf(data[6]);
        this.reminder = data[7];
        this.created_at = data[8];
        this.updated_at = data[9];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{String.valueOf(this.id),
                this.title,
                this.content,
                this.tag,
                this.type,
                this.location,
                String.valueOf(this.is_archive),
                this.reminder,
                this.created_at,
                this.updated_at
        });
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Notes createFromParcel(Parcel in) {
            return new Notes(in);
        }

        public Notes[] newArray(int size) {
            return new Notes[size];
        }
    };
}
