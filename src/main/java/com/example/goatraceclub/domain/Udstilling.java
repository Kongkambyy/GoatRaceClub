package com.example.goatraceclub.domain;

import java.util.Date;

public class Udstilling {

    private Long id;
    private String name;
    private String location;
    private Date date;
    private int cost;

    public Udstilling(String name, String location, int cost) {
        this.name = name;
        this.location = location;
        this.cost = cost;
        this.date = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }
}