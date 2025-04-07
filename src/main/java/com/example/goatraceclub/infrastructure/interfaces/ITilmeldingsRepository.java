package com.example.goatraceclub.infrastructure.interfaces;

import com.example.goatraceclub.domain.Tilmelding;
import java.util.List;
import java.util.Optional;

public interface ITilmeldingsRepository {
    Tilmelding save(Tilmelding tilmelding);

    Optional<Tilmelding> findById(Long id);

    List<Tilmelding> findAll();

    List<Tilmelding> findByUdstillingId(Long udstillingId);

    List<Tilmelding> findByKæledyrId(Long kæledyrId);

    List<Tilmelding> findByMedlemId(Long medlemId);

    List<Tilmelding> findByKategori(String kategori);

    List<Tilmelding> findByStatus(boolean status);

    void deleteById(Long id);

    boolean existsById(Long id);
}