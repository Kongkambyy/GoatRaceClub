package com.example.goatraceclub.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class Medlem {

    private Long id;
    private String name;
    private String email;
    private String Password;
    private String addresse;
    private String telefon;
    private Date indmeldelsesDato;
    private Rolle rolle;
    private List<Kæledyr> kæledyr = new ArrayList<>();

    public Medlem(String name, String email, String password, String addresse, String telefon) {
        this.name = name;
        this.email = email;
        this.Password = password;
        this.addresse = addresse;
        this.telefon = telefon;
        this.indmeldelsesDato = new Date();
        this.rolle = Rolle.MEDLEM;
    }

    public void tilføjKæledur(Kæledyr kæledyr) {
        if (!this.kæledyr.contains(kæledyr)) {
            this.kæledyr.add(kæledyr);
            kæledyr.setMedlem(this);
        }
    }

    public void fjernKæledyr(Kæledyr kæledyr) {
        if (this.kæledyr.contains(kæledyr)) {
            this.kæledyr.remove(kæledyr);
            kæledyr.setMedlem(null);
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
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
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

    public Date indmeldelsesDato() {
        return indmeldelsesDato;
    }

    public void setIndmeldelsesDato(Date indmeldelsesDato) {
        this.indmeldelsesDato = indmeldelsesDato;
    }

    public Rolle getRolle(){
        return rolle;
    }

    public void setRolle(Rolle rolle){
        this.rolle = rolle;
    }

    public List<Kæledyr> getKæledyr() {
        return new ArrayList<>(kæledyr);
    }
}
