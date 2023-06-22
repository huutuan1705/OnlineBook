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
public class Review {
    private int id, bid, cid, rating;
    private String comment, time;

    public Review() {
    }

    public Review(int id, int bid, int cid, int rating, String comment, String time) {
        this.id = id;
        this.bid = bid;
        this.cid = cid;
        this.rating = rating;
        this.comment = comment;
        this.time = time;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBid() {
        return bid;
    }

    public void setBid(int bid) {
        this.bid = bid;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
    
    @Transient
    public String getTimeFormat(){
        String tmp = "";
        tmp += time.substring(11) + " ";
        tmp+=time.substring(8,10)+"-"+time.substring(5,7)+"-"+time.substring(0,4);
        return tmp;
    }
}
