package com.example.goatraceclub.infrastructure.interfaces;

import com.example.goatraceclub.domain.Kæledyr;
import java.util.List;
import java.util.Optional;

public interface IKæledyrRepository {
    Kæledyr save(Kæledyr kæledyr);

    Optional<Kæledyr> findById(Long id);

    List<Kæledyr> findAll();


    List<Kæledyr> findByMedlemId(Long medlemId);

    void deleteById(Long id);

    boolean existsById(Long id);

    List<Kæledyr> findByRace(String race);
}