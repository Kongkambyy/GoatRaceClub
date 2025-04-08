package com.example.goatraceclub.application.implementations;

import com.example.goatraceclub.application.interfaces.IMedlemService;
import com.example.goatraceclub.domain.Medlem;
import com.example.goatraceclub.infrastructure.interfaces.IMedlemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MedlemService implements IMedlemService {

    private final IMedlemRepository medlemRepository;

    @Autowired
    public MedlemService(IMedlemRepository medlemRepository) {
        this.medlemRepository = medlemRepository;
    }

    @Override
    public Medlem opretMedlem(String name, String email, String password, String addresse, String telefon) {
        // Check if member with email already exists
        if (medlemRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("En bruger med denne email findes allerede");
        }

        // Create new member - using password directly without hashing
        Medlem medlem = new Medlem(name, email, password, addresse, telefon);

        // Save and return
        return medlemRepository.save(medlem);
    }

    @Override
    public Optional<Medlem> login(String email, String password) {
        Optional<Medlem> medlemOptional = medlemRepository.findByEmail(email);

        if (medlemOptional.isPresent()) {
            Medlem medlem = medlemOptional.get();

            // Direct password comparison without hashing
            if (medlem.getPassword().equals(password)) {
                return Optional.of(medlem);
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<Medlem> getMedlemById(Long id) {
        return medlemRepository.findById(id);
    }

    @Override
    public Optional<Medlem> getMedlemByEmail(String email) {
        return medlemRepository.findByEmail(email);
    }

    @Override
    public List<Medlem> getAlleMedlemmer() {
        return medlemRepository.findAll();
    }

    @Override
    public Medlem opdaterMedlem(Medlem medlem) {
        if (!medlemRepository.existsById(medlem.getId())) {
            throw new IllegalArgumentException("Medlem findes ikke");
        }

        return medlemRepository.save(medlem);
    }

    @Override
    public void sletMedlem(Long id) {
        medlemRepository.deleteById(id);
    }
}