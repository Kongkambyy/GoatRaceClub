package com.example.goatraceclub.infrastructure.implementations;

import com.example.goatraceclub.domain.Kæledyr;
import com.example.goatraceclub.domain.Medlem;
import com.example.goatraceclub.domain.Rolle;
import com.example.goatraceclub.infrastructure.interfaces.IKæledyrRepository;
import com.example.goatraceclub.infrastructure.interfaces.IMedlemRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MedlemRepository implements IMedlemRepository {

    private final Connection connection;
    private IKæledyrRepository kæledyrRepository;

    public MedlemRepository(Connection connection) {
        this.connection = connection;
    }

    public void setKæledyrRepository(IKæledyrRepository kæledyrRepository) {
        this.kæledyrRepository = kæledyrRepository;
    }

    @Override
    public Medlem save(Medlem medlem) {
        if (medlem.getId() == null) {
            return insert(medlem);
        } else {
            return update(medlem);
        }
    }

    private Medlem insert(Medlem medlem) {
        String sql = "INSERT INTO members (name, email, password, addresse, telefon, indmeldelsesDato, rolle) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, medlem.getName());
            pstmt.setString(2, medlem.getEmail());
            pstmt.setString(3, medlem.getPassword());
            pstmt.setString(4, medlem.getAddresse());
            pstmt.setString(5, medlem.getTelefon());
            pstmt.setDate(6, new java.sql.Date(medlem.getIndmeldelsesDato().getTime()));
            pstmt.setString(7, medlem.getRolle().toString());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Oprettelse af medlem fejlet");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    medlem.setId(generatedKeys.getLong(1));

                    // Opdater alle kæledyr med medlemmets ID
                    if (kæledyrRepository != null) {
                        for (Kæledyr kæledyr : medlem.getKæledyr()) {
                            kæledyr.setOwnerId(medlem.getId().intValue());
                            kæledyrRepository.save(kæledyr);
                        }
                    }

                } else {
                    throw new SQLException("Kunne ikke oprette medlem");
                }
            }

            return medlem;
        } catch (SQLException e) {
            throw new RuntimeException("Kunne ikke gemme medlem", e);
        }
    }

    private Medlem update(Medlem medlem) {
        String sql = "UPDATE members SET name = ?, email = ?, password = ?, addresse = ?, telefon = ?, rolle = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, medlem.getName());
            pstmt.setString(2, medlem.getEmail());
            pstmt.setString(3, medlem.getPassword());
            pstmt.setString(4, medlem.getAddresse());
            pstmt.setString(5, medlem.getTelefon());
            pstmt.setString(6, medlem.getRolle().toString());
            pstmt.setLong(7, medlem.getId());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Opdatering af medlem fejlet");
            }

            if (kæledyrRepository != null) {
                for (Kæledyr kæledyr : medlem.getKæledyr()) {
                    if (kæledyr.getOwnerId() != medlem.getId().intValue()) {
                        kæledyr.setOwnerId(medlem.getId().intValue());
                        kæledyrRepository.save(kæledyr);
                    }
                }
            }

            return medlem;
        } catch (SQLException e) {
            throw new RuntimeException("Kunne ikke opdatere medlem", e);
        }
    }

    @Override
    public Optional<Medlem> findById(Long id) {
        String sql = "SELECT * FROM members WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Medlem medlem = mapResultSetToMedlem(rs);

                    if (kæledyrRepository != null) {
                        List<Kæledyr> kæledyrList = kæledyrRepository.findByMedlemId(id);
                        for (Kæledyr kæledyr : kæledyrList) {
                            medlem.tilføjKæledyr(kæledyr);
                        }
                    }

                    return Optional.of(medlem);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Kunne ikke finde medlem med id" + id, e);
        }
    }

    @Override
    public List<Medlem> findAll() {
        String sql = "SELECT * FROM members";
        List<Medlem> medlemmer = new ArrayList<>();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Medlem medlem = mapResultSetToMedlem(rs);

                if (kæledyrRepository != null && medlem.getId() != null) {
                    List<Kæledyr> kæledyrList = kæledyrRepository.findByMedlemId(medlem.getId());
                    for (Kæledyr kæledyr : kæledyrList) {
                        medlem.tilføjKæledyr(kæledyr);
                    }
                }

                medlemmer.add(medlem);
            }

            return medlemmer;
        } catch (SQLException e) {
            throw new RuntimeException("Kunne ikke finde medlemmer", e);
        }
    }

    @Override
    public Optional<Medlem> findByEmail(String email) {
        String sql = "SELECT * FROM members WHERE email = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Medlem medlem = mapResultSetToMedlem(rs);

                    if (kæledyrRepository != null && medlem.getId() != null) {
                        List<Kæledyr> kæledyrList = kæledyrRepository.findByMedlemId(medlem.getId());
                        for (Kæledyr kæledyr : kæledyrList) {
                            medlem.tilføjKæledyr(kæledyr);
                        }
                    }

                    return Optional.of(medlem);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Kunne ikke finde medlem med email: " + email, e);
        }
    }

    @Override
    public void deleteById(Long id) {
        try {
            if (kæledyrRepository != null) {
                List<Kæledyr> kæledyrList = kæledyrRepository.findByMedlemId(id);
                for (Kæledyr kæledyr : kæledyrList) {
                    kæledyrRepository.deleteById(kæledyr.getId());
                }
            }

            String sql = "DELETE FROM members WHERE id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setLong(1, id);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Kunne ikke slette medlem med ID: " + id, e);
        }
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM members WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Kunne ikke tjekke eksisterende : " + id, e);
        }
    }

    private Medlem mapResultSetToMedlem(ResultSet rs) throws SQLException {
        String name = rs.getString("name");
        String email = rs.getString("email");
        String password = rs.getString("password");
        String addresse = rs.getString("addresse");
        String telefon = rs.getString("telefon");

        Medlem medlem = new Medlem(name, email, password, addresse, telefon);
        medlem.setId(rs.getLong("id"));

        String rolleStr = rs.getString("rolle");
        if (rolleStr != null) {
            medlem.setRolle(Rolle.valueOf(rolleStr));
        }

        Date indmeldelsesDato = rs.getDate("indmeldelsesDato");
        if (indmeldelsesDato != null) {
            medlem.setIndmeldelsesDato(new java.util.Date(indmeldelsesDato.getTime()));
        }

        return medlem;
    }
}