package com.appsinventiv.noorenikahadmin.Models;

public class RequestPayoutModel {
    String id,phone,payoutOption,name;
    int amount;
    long time;
    long payoutTime;
    boolean paid;


    public RequestPayoutModel() {
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public long getPayoutTime() {
        return payoutTime;
    }

    public void setPayoutTime(long payoutTime) {
        this.payoutTime = payoutTime;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPayoutOption() {
        return payoutOption;
    }

    public void setPayoutOption(String payoutOption) {
        this.payoutOption = payoutOption;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
