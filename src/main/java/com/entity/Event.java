package com.entity;

import com.util.Generate;
import com.util.RESTUtil;

import java.util.UUID;

public class Event {
    private String id;
    private String title;
    private String image;
    private String type;
    private String address;
    private String description;
    private String place;
    private long price;
    private long start_at;
    private long time;
    private long quantity;
    private long sold;


    public Event() {
        this.id = Generate.randomAlphaNumeric(20);
        this.sold = 100;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public long getStart_at() {
        return start_at;
    }

    public void setStart_at(long start_at) {
        this.start_at = start_at;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public long getSold() {
        return sold;
    }

    public void setSold(long sold) {
        this.sold = sold;
    }
}
