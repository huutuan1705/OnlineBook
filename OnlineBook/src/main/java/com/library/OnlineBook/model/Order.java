/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.library.OnlineBook.model;

import java.beans.Transient;

/**
 *
 * @author huutuan
 */
public class Order {
    private int id;
    private String date;
    private int cusID;
    private double totalMoney;
    private int status;

    public Order() {
    }

    public Order(int id, String date, int cusID, double totalMoney, int status) {
        this.id = id;
        this.date = date;
        this.cusID = cusID;
        this.totalMoney = totalMoney;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getCusID() {
        return cusID;
    }

    public void setCusID(int cusID) {
        this.cusID = cusID;
    }

    public double getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(double totalMoney) {
        this.totalMoney = totalMoney;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    
    @Transient
    public String getDateFormat(){
        String tmp="";
        tmp+=date.substring(8)+"-"+date.substring(5,7)+"-"+date.substring(0,4);
        return tmp;
    }
    
}
