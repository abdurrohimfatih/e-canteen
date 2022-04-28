package com.ecanteen.ecanteen.entities;

public class Transaction {
    private String id;
    private String username;
    private String date;
    private String time;
//    private String totalAll;
//    private String totalDiscount;
    private String totalAmount;
    private String payAmount;
    private String change;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

//    public String getTotalAll() {
//        return totalAll;
//    }
//
//    public void setTotalAll(String totalAll) {
//        this.totalAll = totalAll;
//    }
//
//    public String getTotalDiscount() {
//        return totalDiscount;
//    }
//
//    public void setTotalDiscount(String totalDiscount) {
//        this.totalDiscount = totalDiscount;
//    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(String payAmount) {
        this.payAmount = payAmount;
    }

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }
}
