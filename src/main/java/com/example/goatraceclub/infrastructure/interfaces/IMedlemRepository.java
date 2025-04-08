package com.example.goatraceclub.infrastructure.interfaces;

import com.example.goatraceclub.domain.Medlem;
import java.util.List;
import java.util.Optional;

public interface IMedlemRepository {
    Medlem save(Medlem medlem);

    Optional<Medlem> findById(Long id);

    List<Medlem> findAll();

    Optional<Medlem> findByEmail(String email);

    void deleteById(Long id);

    boolean existsById(Long id);
}