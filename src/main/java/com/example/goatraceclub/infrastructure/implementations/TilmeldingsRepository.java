package com.example.goatraceclub.infrastructure.implementations;

import com.example.goatraceclub.domain.Kæledyr;
import com.example.goatraceclub.domain.Tilmelding;
import com.example.goatraceclub.domain.Udstilling;
import com.example.goatraceclub.infrastructure.interfaces.IKæledyrRepository;
import com.example.goatraceclub.infrastructure.interfaces.ITilmeldingsRepository;
import com.example.goatraceclub.infrastructure.interfaces.IUdstillingsRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TilmeldingsRepository implements ITilmeldingsRepository {

    private final Connection connection;
    private final IKæledyrRepository kæledyrRepository;
    private final IUdstillingsRepository udstillingsRepository;

    public TilmeldingsRepository(Connection connection,
                                 IKæledyrRepository kæledyrRepository,
                                 IUdstillingsRepository udstillingsRepository) {
        this.connection = connection;
        this.kæledyrRepository = kæledyrRepository;
        this.udstillingsRepository = udstillingsRepository;
    }

    @Override
    public Tilmelding save(Tilmelding tilmelding) {
        if (tilmelding.getId() == null) {
            return insert(tilmelding);
        } else {
            return update(tilmelding);
        }
    }

    private Tilmelding insert(Tilmelding tilmelding) {
        String sql = "INSERT INTO registration (showId, petId, kategori, tilmeldingsDato, status) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setLong(1, tilmelding.getUdstilling().getId());
            pstmt.setLong(2, tilmelding.getKæledyr().getId());
            pstmt.setString(3, tilmelding.getKategori());
            pstmt.setDate(4, new java.sql.Date(tilmelding.getTilmeldingsDato().getTime()));
            pstmt.setBoolean(5, tilmelding.isStatus());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Kunne ikke registrere");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    tilmelding.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Intet ID fundet, registrering fejlet");
                }
            }

            return tilmelding;
        } catch (SQLException e) {
            throw new RuntimeException("Kan ikke gemme registrering", e);
        }
    }

    private Tilmelding update(Tilmelding tilmelding) {
        String sql = "UPDATE registration SET showId = ?, petId = ?, kategori = ?, tilmeldingsDato = ?, status = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, tilmelding.getUdstilling().getId());
            pstmt.setLong(2, tilmelding.getKæledyr().getId());
            pstmt.setString(3, tilmelding.getKategori());
            pstmt.setDate(4, new java.sql.Date(tilmelding.getTilmeldingsDato().getTime()));
            pstmt.setBoolean(5, tilmelding.isStatus());
            pstmt.setLong(6, tilmelding.getId());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Opdatering af registrering fejl");
            }

            return tilmelding;
        } catch (SQLException e) {
            throw new RuntimeException("Kan ikke opdatere", e);
        }
    }

    @Override
    public Optional<Tilmelding> findById(Long id) {
        String sql = "SELECT * FROM registration WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Tilmelding tilmelding = mapResultSetToTilmelding(rs);
                    return Optional.of(tilmelding);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Kunne ikke finde registrering med ID " + id, e);
        }
    }

    @Override
    public List<Tilmelding> findAll() {
        String sql = "SELECT * FROM registration";
        List<Tilmelding> tilmeldinger = new ArrayList<>();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                tilmeldinger.add(mapResultSetToTilmelding(rs));
            }

            return tilmeldinger;
        } catch (SQLException e) {
            throw new RuntimeException("Kunne ikke finde alle tilmeldinger", e);
        }
    }

    @Override
    public List<Tilmelding> findByUdstillingId(Long udstillingId) {
        String sql = "SELECT * FROM registration WHERE showId = ?";
        List<Tilmelding> tilmeldinger = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, udstillingId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tilmeldinger.add(mapResultSetToTilmelding(rs));
                }
            }

            return tilmeldinger;
        } catch (SQLException e) {
            throw new RuntimeException("Kunne ikke finde ID for udstilling: " + udstillingId, e);
        }
    }

    @Override
    public List<Tilmelding> findByKæledyrId(Long kæledyrId) {
        String sql = "SELECT * FROM registration WHERE petId = ?";
        List<Tilmelding> tilmeldinger = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, kæledyrId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tilmeldinger.add(mapResultSetToTilmelding(rs));
                }
            }

            return tilmeldinger;
        } catch (SQLException e) {
            throw new RuntimeException("Kunne ikke finde registrering for kæledyr: " + kæledyrId, e);
        }
    }

    @Override
    public List<Tilmelding> findByMedlemId(Long medlemId) {
        String sql = "SELECT r.* FROM registration r " +
                "JOIN goats g ON r.petId = g.goatId " +
                "WHERE g.ownerId = ?";
        List<Tilmelding> tilmeldinger = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, medlemId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tilmeldinger.add(mapResultSetToTilmelding(rs));
                }
            }

            return tilmeldinger;
        } catch (SQLException e) {
            throw new RuntimeException("Kunne ikke finde medlem med ID: " + medlemId, e);
        }
    }

    @Override
    public List<Tilmelding> findByKategori(String kategori) {
        String sql = "SELECT * FROM registration WHERE kategori = ?";
        List<Tilmelding> tilmeldinger = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, kategori);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tilmeldinger.add(mapResultSetToTilmelding(rs));
                }
            }

            return tilmeldinger;
        } catch (SQLException e) {
            throw new RuntimeException("Kunne ikke finde efter kategori: " + kategori, e);
        }
    }

    @Override
    public List<Tilmelding> findByStatus(boolean status) {
        String sql = "SELECT * FROM registration WHERE status = ?";
        List<Tilmelding> tilmeldinger = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setBoolean(1, status);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tilmeldinger.add(mapResultSetToTilmelding(rs));
                }
            }

            return tilmeldinger;
        } catch (SQLException e) {
            throw new RuntimeException("Kunne ikke finde registrering: " + status, e);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM registration WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Kunne ikke slette tilmelding med ID " + id, e);
        }
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM registration WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Kunne ikke registrere " + id, e);
        }
    }

    private Tilmelding mapResultSetToTilmelding(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        Long udstillingId = rs.getLong("showId");
        Long kæledyrId = rs.getLong("petId");
        String kategori = rs.getString("kategori");
        Date tilmeldingsDato = rs.getDate("tilmeldingsDato");
        boolean status = rs.getBoolean("status");

        // Hent udstilling og kæledyr
        Udstilling udstilling = null;
        Kæledyr kæledyr = null;

        try {
            // Direkte SQL-forespørgsel for at undgå cirkulære afhængigheder
            String udstillingsSql = "SELECT * FROM exhibitions WHERE id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(udstillingsSql)) {
                pstmt.setLong(1, udstillingId);
                try (ResultSet udstillingsRs = pstmt.executeQuery()) {
                    if (udstillingsRs.next()) {
                        udstilling = new Udstilling(
                                udstillingsRs.getString("name"),
                                udstillingsRs.getString("location"),
                                udstillingsRs.getInt("cost")
                        );
                        udstilling.setId(udstillingId);

                        Date date = udstillingsRs.getDate("date");
                        if (date != null) {
                            udstilling.setDate(new java.util.Date(date.getTime()));
                        }
                    }
                }
            }

            String kæledyrSql = "SELECT * FROM goats WHERE goatId = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(kæledyrSql)) {
                pstmt.setLong(1, kæledyrId);
                try (ResultSet kæledyrRs = pstmt.executeQuery()) {
                    if (kæledyrRs.next()) {
                        kæledyr = new Kæledyr(
                                kæledyrRs.getInt("ownerId"),
                                kæledyrRs.getString("goatName"),
                                kæledyrRs.getString("race"),
                                kæledyrRs.getInt("weight"),
                                kæledyrRs.getDate("birthday").toString()
                        );
                        kæledyr.setId(kæledyrId);
                    }
                }
            }

            if (udstilling == null || kæledyr == null) {
                throw new SQLException("Kunne ikke finde tilhørende udstilling eller kæledyr for tilmeldingen");
            }

            return new Tilmelding(id, udstilling, kategori, kæledyr,
                    new java.util.Date(tilmeldingsDato.getTime()), status);

        } catch (SQLException e) {
            throw new SQLException("Fejl ved mappning af tilmelding: " + e.getMessage(), e);
        }
    }
}