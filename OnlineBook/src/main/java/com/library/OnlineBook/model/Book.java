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
public class Book {
    private int id;
    private String title, author, description, releaseDate;
    private int pages;
    private int categoryID;
    private String image;
    private double price;

    public Book() {
    }

    public Book(int id, String title, String author, String description, String releaseDate, int pages, int categoryID, String image, double price) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.description = description;
        this.releaseDate = releaseDate;
        this.pages = pages;
        this.categoryID = categoryID;
        this.image = image;
        this.price = price;
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }
    
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
    
    @Transient
    public String getImagePath(){
        if(image==null || id<1) return null;
        return "/book-image/" + id + "/" + image;
    }
    
    @Transient
    public String getDateFormat(){
        String tmp="";
        tmp+=releaseDate.substring(8)+"-"+releaseDate.substring(5,7)+"-"+releaseDate.substring(0,4);
        return tmp;
    }
}
