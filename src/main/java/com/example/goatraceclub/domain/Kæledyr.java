package com.example.goatraceclub.domain;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
public class Kæledyr {

    private Long id;
    private int ownerId;
    private String goatName;
    private String race;
    private int weight;
    private Date birthday;

    public Kæledyr(int ownerId, String goatName, String race, int weight, String birthdayStr) {
        this.ownerId = ownerId;
        this.goatName = goatName;
        this.race = race;
        this.weight = weight;

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            this.birthday = format.parse(birthdayStr);
        } catch (ParseException e) {
            this.birthday = new Date();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public String getGoatName() {
        return goatName;
    }

    public void setGoatName(String goatName) {
        this.goatName = goatName;
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public int getWeight(){
        return weight;
    }

    public void setWeight(int weight){
        this.weight = weight;
    }

    public Date birthday(){
        return birthday;
    }

    public void setBirthday(Date birthday){
        this.birthday = new Date();
    }
}
