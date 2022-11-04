package com.appsinventiv.noorenikahadmin.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("Title")
    @Expose
    private String title;
    @SerializedName("Message")
    @Expose
    private String message;
    @SerializedName("Id")
    @Expose
    private String id;
    @SerializedName("Type")
    @Expose
    private String type;
    @SerializedName("Image")
    @Expose
    private String image;


    public Data(String title, String message, String id, String type, String image) {
        this.title = title;
        this.message = message;
        this.id = id;
        this.type = type;
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


}
