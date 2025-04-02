package com.example.goatraceclub.infrastructure.repositories;

import com.example.goatraceclub.domain.Kæledyr;
import com.example.goatraceclub.domain.Køn;
import com.example.goatraceclub.domain.Medlem;
import com.example.goatraceclub.infrastructure.interfaces.IKæledyrRepository;
import com.example.goatraceclub.infrastructure.interfaces.IMedlemRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation af IKæledyrRepository der bruger JDBC til databaseadgang
 */
public class KæledyrRepository implements IKæledyrRepository {

    private final Connection connection;
    private final IMedlemRepository medlemRepository;

    public KæledyrRepository(Connection connection, IMedlemRepository medlemRepository) {
        this.connection = connection;
        this.medlemRepository = medlemRepository;
    }

    @Override
    public Kæledyr save(Kæledyr kæledyr) {
        if (kæledyr.getId() == null) {
            return insert(kæledyr);
        } else {
            return update(kæledyr);
        }
    }

    private Kæledyr insert(Kæledyr kæledyr) {
        String sql = "INSERT INTO goats (ownerId, goatName, race, weight, birthday) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // Sikre at kæledyret har et medlem
            if (kæledyr.getMedlem() == null || kæledyr.getMedlem().getId() == null) {
                throw new SQLException("Kæledyr skal have et gyldigt medlem");
            }

            pstmt.setLong(1, kæledyr.getMedlem().getId());
            pstmt.setString(2, kæledyr.getNavn());
            pstmt.setString(3, kæledyr.getRace());

            // Vægt er ikke direkte i vores domænemodel, men er i databasen
            // Vi kunne opdatere domænemodellen eller håndtere dette her
            int vægt = 0; // Standard værdi
            if (kæledyr instanceof Udvidet) {
                vægt = ((Udvidet) kæledyr).getVægt();
            }
            pstmt.setInt(4, vægt);

            pstmt.setDate(5, new java.sql.Date(kæledyr.getFødselsdato().getTime()));

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Oprettelse af kæledyr fejlede, ingen rækker påvirket.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    kæledyr.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Oprettelse af kæledyr fejlede, intet ID opnået.");
                }
            }

            return kæledyr;
        } catch (SQLException e) {
            throw new RuntimeException("Kunne ikke gemme kæledyr", e);
        }
    }

    private Kæledyr update(Kæledyr kæledyr) {
        String sql = "UPDATE goats SET ownerId = ?, goatName = ?, race = ?, weight = ?, birthday = ? WHERE goatId = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, kæledyr.getMedlem().getId());
            pstmt.setString(2, kæledyr.getNavn());
            pstmt.setString(3, kæledyr.getRace());

            // Håndter vægt ligesom i insert-metoden
            int vægt = 0;
            if (kæledyr instanceof Udvidet) {
                vægt = ((Udvidet) kæledyr).getVægt();
            }
            pstmt.setInt(4, vægt);

            pstmt.setDate(5, new java.sql.Date(kæledyr.getFødselsdato().getTime()));
            pstmt.setLong(6, kæledyr.getId());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Opdatering af kæledyr fejlede, ingen rækker påvirket.");
            }

            return kæledyr;
        } catch (SQLException e) {
            throw new RuntimeException("Kunne ikke opdatere kæledyr", e);
        }
    }

    @Override
    public Optional<Kæledyr> findById(Long id) {
        String sql = "SELECT * FROM goats WHERE goatId = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Kæledyr kæledyr = mapResultSetToKæledyr(rs);
                    return Optional.of(kæledyr);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Kunne ikke finde kæledyr med id: " + id, e);
        }
    }

    @Override
    public List<Kæledyr> findAll() {
        String sql = "SELECT * FROM goats";
        List<Kæledyr> kæledyr = new ArrayList<>();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                kæledyr.add(mapResultSetToKæledyr(rs));
            }

            return kæledyr;
        } catch (SQLException e) {
            throw new RuntimeException("Kunne ikke hente alle kæledyr", e);
        }
    }

    @Override
    public List<Kæledyr> findByMedlemId(Long medlemId) {
        String sql = "SELECT * FROM goats WHERE ownerId = ?";
        List<Kæledyr> kæledyr = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, medlemId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    kæledyr.add(mapResultSetToKæledyr(rs));
                }
            }

            return kæledyr;
        } catch (SQLException e) {
            throw new RuntimeException("Kunne ikke finde kæledyr for medlem: " + medlemId, e);
        }
    }

    @Override
    public List<Kæledyr> findByRace(String race) {
        String sql = "SELECT * FROM goats WHERE race = ?";
        List<Kæledyr> kæledyr = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, race);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    kæledyr.add(mapResultSetToKæledyr(rs));
                }
            }

            return kæledyr;
        } catch (SQLException e) {
            throw new RuntimeException("Kunne ikke finde kæledyr af race: " + race, e);
        }
    }

    @Override
    public void deleteById(Long id) {
        // Først kontrollér om der er tilmeldinger for dette kæledyr
        String checkSql = "SELECT COUNT(*) FROM registration WHERE petId = ?";

        try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
            checkStmt.setLong(1, id);

            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    // Der er tilmeldinger - slet dem først
                    String deleteTilmeldingSql = "DELETE FROM registration WHERE petId = ?";
                    try (PreparedStatement deleteRegStmt = connection.prepareStatement(deleteTilmeldingSql)) {
                        deleteRegStmt.setLong(1, id);
                        deleteRegStmt.executeUpdate();
                    }
                }
            }

            // Nu kan vi trygt slette kæledyret
            String sql = "DELETE FROM goats WHERE goatId = ?";

            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setLong(1, id);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Kunne ikke slette kæledyr med id: " + id, e);
        }
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM goats WHERE goatId = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Kunne ikke kontrollere eksistens af kæledyr med id: " + id, e);
        }
    }

    // Hjælpemetode til at konvertere ResultSet til Kæledyr objekt
    private Kæledyr mapResultSetToKæledyr(ResultSet rs) throws SQLException {
        Long ownerId = rs.getLong("ownerId");
        Optional<Medlem> ejer = medlemRepository.findById(ownerId);

        if (ejer.isEmpty()) {
            throw new SQLException("Kunne ikke finde ejer med id: " + ownerId);
        }

        // Bemærk: Køn er ikke i databasen, så vi sætter en standard værdi
        Køn køn = Køn.NEUTRAL; // Standard værdi

        // Opret en udvidet implementering af Kæledyr der også har vægt
        return new UdvidetKæledyr(
                rs.getLong("goatId"),
                rs.getString("goatName"),
                rs.getString("race"),
                rs.getDate("birthday"),
                "Ikke specificeret", // Farve er ikke i databasen
                "Ikke specificeret", // StamtavleNr er ikke i databasen
                køn,
                ejer.get(),
                rs.getInt("weight") // Gem vægten fra databasen
        );
    }

    // Indre klasse til at håndtere ekstra felter fra databasen
    private class UdvidetKæledyr extends Kæledyr implements Udvidet {
        private int vægt;

        public UdvidetKæledyr(Long id, String navn, String race, Date fødselsdato,
                              String farve, String stamtavleNr, Køn køn,
                              Medlem medlem, int vægt) {
            super(id, navn, race, fødselsdato, farve, stamtavleNr, køn, medlem);
            this.vægt = vægt;
        }

        @Override
        public int getVægt() {
            return vægt;
        }

        @Override
        public void setVægt(int vægt) {
            this.vægt = vægt;
        }
    }

    // Interface til at håndtere ekstra felter
    private interface Udvidet {
        int getVægt();
        void setVægt(int vægt);
    }
}