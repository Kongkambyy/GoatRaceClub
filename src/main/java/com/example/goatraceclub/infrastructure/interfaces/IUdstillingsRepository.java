package com.example.goatraceclub.infrastructure.interfaces;

import com.example.goatraceclub.domain.Udstilling;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface IUdstillingsRepository {
    Udstilling save(Udstilling udstilling);

    Optional<Udstilling> findById(Long id);

    List<Udstilling> findAll();

    List<Udstilling> findByLocation(String location);

    List<Udstilling> findByDateAfter(Date date);

    void deleteById(Long id);

    boolean existsById(Long id);
}