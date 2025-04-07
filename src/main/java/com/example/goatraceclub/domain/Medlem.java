package com.example.goatraceclub.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class Medlem {

    private Long id;
    private String name;
    private String email;
    private String password;
    private String addresse;
    private String telefon;
    private Date indmeldelsesDato;
    private Rolle rolle;
    private List<Kæledyr> kæledyr = new ArrayList<>();

    public Medlem(String name, String email, String password, String addresse, String telefon) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.addresse = addresse;
        this.telefon = telefon;
        this.indmeldelsesDato = new Date();
        this.rolle = Rolle.MEDLEM;
    }

    public void tilføjKæledyr(Kæledyr kæledyr) {
        if (!this.kæledyr.contains(kæledyr)) {
            this.kæledyr.add(kæledyr);
            kæledyr.setOwnerId(this.id.intValue());
        }
    }

    public void fjernKæledyr(Kæledyr kæledyr) {
        if (this.kæledyr.contains(kæledyr)) {
            this.kæledyr.remove(kæledyr);
            kæledyr.setOwnerId(0); // 0 angiver at geden ikke har en ejer
        }
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddresse() {
        return addresse;
    }

    public void setAddresse(String addresse) {
        this.addresse = addresse;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public Date getIndmeldelsesDato() {
        return indmeldelsesDato;
    }

    public void setIndmeldelsesDato(Date indmeldelsesDato) {
        this.indmeldelsesDato = indmeldelsesDato;
    }

    public Rolle getRolle() {
        return rolle;
    }

    public void setRolle(Rolle rolle) {
        this.rolle = rolle;
    }

    public List<Kæledyr> getKæledyr() {
        return new ArrayList<>(kæledyr);
    }
}