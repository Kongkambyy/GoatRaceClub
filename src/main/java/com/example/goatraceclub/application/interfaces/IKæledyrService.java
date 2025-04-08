package com.example.goatraceclub.application.interfaces;

import com.example.goatraceclub.domain.Kæledyr;

import java.util.List;
import java.util.Optional;

public interface IKæledyrService {

    Kæledyr opretKæledyr(Long medlemId, String goatName, String race, int weight, String birthday);

    Optional<Kæledyr> getKæledyrById(Long id);

    List<Kæledyr> getAlleKæledyr();

    List<Kæledyr> getKæledyrByMedlemId(Long medlemId);

    List<Kæledyr> getKæledyrByRace(String race);

    Kæledyr opdaterKæledyr(Kæledyr kæledyr);

    void sletKæledyr(Long id);
}