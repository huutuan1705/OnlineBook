/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.library.OnlineBook.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author huutuan
 */
public class Cart {
    private List<Item> items;

    public Cart() {
        items = new ArrayList<>();
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
    
    private Item getItemById(int id) {
        for (Item i : items) {
            if (i.getBook().getId() == id) {
                return i;
            }
        }
        return null;
    }
    
    public int getQuantityById(int id) {
        return getItemById(id).getQuantity();
    }
    
    public void addItem(Item t) {
        if (getItemById(t.getBook().getId()) != null) {
            Item m = getItemById(t.getBook().getId());
            m.setQuantity(m.getQuantity() + t.getQuantity());
        } else {
            items.add(t);
        }
    }
    
    public void removeItem(int id) {
        if (getItemById(id) != null) {
            items.remove(getItemById(id));
        }
    }
    
    public void removeAllItems(){
        for (Item i : items) {
            items.remove(i);
        }
    }
    
    public double getTotalMoney() {
        double t = 0;
        for (Item i : items) {
            t += i.getQuantity() * i.getPrice();
        }
        return t;
    }
    
    private Book getBookById(int id,List<Book> list){
        for(Book i:list){
            if(i.getId()==id)
                return i;
        }
        return null;
    }
    
}
