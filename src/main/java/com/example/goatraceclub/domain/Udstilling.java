package com.example.goatraceclub.domain;

import java.util.Date;

public class Udstilling {

    private long showId;
    private String showName;
    private String location;
    private Date occasion;
    private int cost;

    public Udstilling(String name, String location, int cost) {
        this.name = name;
        this.location = location;
        this.cost = cost;
        this.date = new Date();
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }
}
