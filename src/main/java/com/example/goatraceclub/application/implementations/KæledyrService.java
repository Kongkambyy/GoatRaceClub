package com.example.goatraceclub.application.implementations;

import com.example.goatraceclub.application.interfaces.IKæledyrService;
import com.example.goatraceclub.domain.Kæledyr;
import com.example.goatraceclub.domain.Medlem;
import com.example.goatraceclub.infrastructure.interfaces.IKæledyrRepository;
import com.example.goatraceclub.infrastructure.interfaces.IMedlemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class KæledyrService implements IKæledyrService {

    private final IKæledyrRepository kæledyrRepository;
    private final IMedlemRepository medlemRepository;

    @Autowired
    public KæledyrService(IKæledyrRepository kæledyrRepository, IMedlemRepository medlemRepository) {
        this.kæledyrRepository = kæledyrRepository;
        this.medlemRepository = medlemRepository;
    }

    @Override
    public Kæledyr opretKæledyr(Long medlemId, String goatName, String race, int weight, String birthday) {
        Optional<Medlem> medlemOptional = medlemRepository.findById(medlemId);
        if (medlemOptional.isEmpty()) {
            throw new IllegalArgumentException("Medlemmet findes ikke");
        }

        Medlem medlem = medlemOptional.get();

        Kæledyr kæledyr = new Kæledyr(medlemId.intValue(), goatName, race, weight, birthday);

        kæledyr = kæledyrRepository.save(kæledyr);
        medlem.tilføjKæledyr(kæledyr);
        medlemRepository.save(medlem);

        return kæledyr;
    }

    @Override
    public Optional<Kæledyr> getKæledyrById(Long id) {
        return kæledyrRepository.findById(id);
    }

    @Override
    public List<Kæledyr> getAlleKæledyr() {
        return kæledyrRepository.findAll();
    }

    @Override
    public List<Kæledyr> getKæledyrByMedlemId(Long medlemId) {
        return kæledyrRepository.findByMedlemId(medlemId);
    }

    @Override
    public List<Kæledyr> getKæledyrByRace(String race) {
        return kæledyrRepository.findByRace(race);
    }

    @Override
    public Map<String, List<Kæledyr>> getAlleMedlemmerMedGeder() {
        Map<String, List<Kæledyr>> medlemmerMedGeder = new HashMap<>();

        // Hent alle medlemmer
        List<Medlem> medlemmer = medlemRepository.findAll();

        for (Medlem medlem : medlemmer) {
            List<Kæledyr> geder = kæledyrRepository.findByMedlemId(medlem.getId());
            if (!geder.isEmpty()) {
                medlemmerMedGeder.put(medlem.getName(), geder);
            }
        }

        return medlemmerMedGeder;
    }

    @Override
    public Kæledyr opdaterKæledyr(Kæledyr kæledyr) {
        if (!kæledyrRepository.existsById(kæledyr.getId())) {
            throw new IllegalArgumentException("Kæledyret findes ikke");
        }

        return kæledyrRepository.save(kæledyr);
    }

    @Override
    public void sletKæledyr(Long id) {
        Optional<Kæledyr> kæledyrOptional = kæledyrRepository.findById(id);
        if (kæledyrOptional.isPresent()) {
            Kæledyr kæledyr = kæledyrOptional.get();
            int ownerId = kæledyr.getOwnerId();

            Optional<Medlem> medlemOptional = medlemRepository.findById((long) ownerId);
            if (medlemOptional.isPresent()) {
                Medlem medlem = medlemOptional.get();
                medlem.fjernKæledyr(kæledyr);
                medlemRepository.save(medlem);
            }

            kæledyrRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Kæledyret findes ikke");
        }
    }
}