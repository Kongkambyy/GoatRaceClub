package com.example.goatraceclub.application.interfaces;

import com.example.goatraceclub.domain.Medlem;

import java.util.List;
import java.util.Optional;

public interface IMedlemService {

    Medlem opretMedlem(String name, String email, String password, String addresse, String telefon);

    Optional<Medlem> login(String email, String password);

    Optional<Medlem> getMedlemById(Long id);

    Optional<Medlem> getMedlemByEmail(String email);

    List<Medlem> getAlleMedlemmer();

    Medlem opdaterMedlem(Medlem medlem);

    void sletMedlem(Long id);
}