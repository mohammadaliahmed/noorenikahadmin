package com.appsinventiv.noorenikahadmin.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SendNotificationModel {


    @SerializedName("to")
    @Expose
    private String to;
    @SerializedName("data")
    @Expose
    private Data data;


    public SendNotificationModel(String to, Data data) {
        this.to = to;
        this.data = data;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

}
