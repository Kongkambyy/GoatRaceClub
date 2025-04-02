package com.example.goatraceclub.domain;

import java.util.Date;

public class Tilmelding {

    private Long id;
    private Udstilling udstilling;
    private Kæledyr kæledyr;
    private Date tilmeldingsDato;
    private boolean status;
    private String kategori;

    public Tilmelding(Udstilling udstilling, Kæledyr kæledyr, String kategori) {
        this.udstilling = udstilling;
        this.kæledyr = kæledyr;
        this.kategori = kategori;
        this.status = false;
        this.tilmeldingsDato = new Date();
    }

    public Tilmelding(Long id, Udstilling udstilling, String kategori, Kæledyr kæledyr, Date tilmeldingsDato, boolean status) {
        this.id = id;
        this.udstilling = udstilling;
        this.kæledyr = kæledyr;
        this.tilmeldingsDato = tilmeldingsDato;
        this.status = status;
        this.kategori = kategori;
    }

    public void bekræftTilmelding(String kategori) {
        this.status = true;
    }

    public void annullerTilmelding() {
        this.status = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Udstilling getUdstilling() {
        return udstilling;
    }

    public void setUdstilling(Udstilling udstilling) {
        this.udstilling = udstilling;
    }

    public Kæledyr getKæledyr() {
        return kæledyr;
    }

    public void setKæledyr(Kæledyr kæledyr) {
        this.kæledyr = kæledyr;
    }

    public Date getTilmeldingsDato() {
        return tilmeldingsDato;
    }

    public void setTilmeldingsDato(Date tilmeldingsDato) {
        this.tilmeldingsDato = tilmeldingsDato;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }
}
