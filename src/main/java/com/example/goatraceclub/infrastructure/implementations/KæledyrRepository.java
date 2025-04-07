package com.example.goatraceclub.infrastructure.implementations;

import com.example.goatraceclub.domain.Kæledyr;
import com.example.goatraceclub.infrastructure.interfaces.IKæledyrRepository;
import com.example.goatraceclub.infrastructure.interfaces.IMedlemRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
            pstmt.setInt(1, kæledyr.getOwnerId());
            pstmt.setString(2, kæledyr.getGoatName());
            pstmt.setString(3, kæledyr.getRace());
            pstmt.setInt(4, kæledyr.getWeight());
            pstmt.setDate(5, new java.sql.Date(kæledyr.birthday().getTime()));

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Kunne ikke oprette ged");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    kæledyr.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Oprettelse af ged fejlet");
                }
            }

            return kæledyr;
        } catch (SQLException e) {
            throw new RuntimeException("Kunne ikke gemme geden", e);
        }
    }

    private Kæledyr update(Kæledyr kæledyr) {
        String sql = "UPDATE goats SET ownerId = ?, goatName = ?, race = ?, weight = ?, birthday = ? WHERE goatId = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, kæledyr.getOwnerId());
            pstmt.setString(2, kæledyr.getGoatName());
            pstmt.setString(3, kæledyr.getRace());
            pstmt.setInt(4, kæledyr.getWeight());
            pstmt.setDate(5, new java.sql.Date(kæledyr.birthday().getTime()));
            pstmt.setLong(6, kæledyr.getId());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Opdatering af ged fejlet");
            }

            return kæledyr;
        } catch (SQLException e) {
            throw new RuntimeException("Kunne ikke opdatere ged", e);
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
            throw new RuntimeException("Kunne ikke finde ged med ID: " + id, e);
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
            throw new RuntimeException("Kunne ikke finde alle geder", e);
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
            throw new RuntimeException("Kunne ikke finde medlemmernes geder: " + medlemId, e);
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
            throw new RuntimeException("Kunne ikke finde gedernes race " + race, e);
        }
    }

    @Override
    public void deleteById(Long id) {
        String checkSql = "SELECT COUNT(*) FROM registration WHERE petId = ?";

        try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
            checkStmt.setLong(1, id);

            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    String deleteTilmeldingSql = "DELETE FROM registration WHERE petId = ?";
                    try (PreparedStatement deleteRegStmt = connection.prepareStatement(deleteTilmeldingSql)) {
                        deleteRegStmt.setLong(1, id);
                        deleteRegStmt.executeUpdate();
                    }
                }
            }

            String sql = "DELETE FROM goats WHERE goatId = ?";

            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setLong(1, id);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("kunne ikke slette ged med id: " + id, e);
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
            throw new RuntimeException("Kunne ikke finde eksisterende geder med ID: " + id, e);
        }
    }

    private Kæledyr mapResultSetToKæledyr(ResultSet rs) throws SQLException {
        Kæledyr kæledyr = new Kæledyr(
                rs.getInt("ownerId"),
                rs.getString("goatName"),
                rs.getString("race"),
                rs.getInt("weight"),
                rs.getDate("birthday").toString()
        );
        kæledyr.setId(rs.getLong("goatId"));
        return kæledyr;
    }
}