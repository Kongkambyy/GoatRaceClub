package com.example.goatraceclub.infrastructure.implementations;

import com.example.goatraceclub.domain.Udstilling;
import com.example.goatraceclub.infrastructure.interfaces.IUdstillingsRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class UdstillingsRepository implements IUdstillingsRepository {

    private final Connection connection;

    public UdstillingsRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Udstilling save(Udstilling udstilling) {
        if (udstilling.getId() == 0) {
            return insert(udstilling);
        } else {
            return update(udstilling);
        }
    }

    private Udstilling insert(Udstilling udstilling) {
        String sql = "INSERT INTO exhibitions (showName, location, cost, occasion) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, udstilling.getName());
            pstmt.setString(2, udstilling.getLocation());
            pstmt.setInt(3, udstilling.getCost());
            pstmt.setDate(4, new java.sql.Date(udstilling.getDate().getTime()));

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Kunne ikke oprette udstilling");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    udstilling.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Oprettelse af udstilling fejlet");
                }
            }

            return udstilling;
        } catch (SQLException e) {
            throw new RuntimeException("Kunne ikke gemme udstilling", e);
        }
    }

    private Udstilling update(Udstilling udstilling) {
        String sql = "UPDATE exhibitions SET showName = ?, location = ?, cost = ?, occasion = ? WHERE showId = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, udstilling.getName());
            pstmt.setString(2, udstilling.getLocation());
            pstmt.setInt(3, udstilling.getCost());
            pstmt.setDate(4, new java.sql.Date(udstilling.getDate().getTime()));
            pstmt.setLong(5, udstilling.getId());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Opdatering af udstilling fejlet");
            }

            return udstilling;
        } catch (SQLException e) {
            throw new RuntimeException("Kunne ikke opdatere udstilling", e);
        }
    }

    @Override
    public Optional<Udstilling> findById(Long id) {
        String sql = "SELECT * FROM exhibitions WHERE showId = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Udstilling udstilling = mapResultSetToUdstilling(rs);
                    return Optional.of(udstilling);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Kunne ikke finde tilmelding med ID: " + id, e);
        }
    }

    @Override
    public List<Udstilling> findAll() {
        String sql = "SELECT * FROM exhibitions";
        List<Udstilling> udstillinger = new ArrayList<>();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                udstillinger.add(mapResultSetToUdstilling(rs));
            }

            return udstillinger;
        } catch (SQLException e) {
            throw new RuntimeException("Kunne ikke finde alle udstillinger", e);
        }
    }

    @Override
    public List<Udstilling> findByLocation(String location) {
        String sql = "SELECT * FROM exhibitions WHERE location = ?";
        List<Udstilling> udstillinger = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, location);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    udstillinger.add(mapResultSetToUdstilling(rs));
                }
            }

            return udstillinger;
        } catch (SQLException e) {
            throw new RuntimeException("Kunne ikke finde udstillinger ved lokation: " + location, e);
        }
    }

    @Override
    public List<Udstilling> findByDateAfter(Date date) {
        String sql = "SELECT * FROM exhibitions WHERE occasion >= ?";
        List<Udstilling> udstillinger = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDate(1, new java.sql.Date(date.getTime()));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    udstillinger.add(mapResultSetToUdstilling(rs));
                }
            }

            return udstillinger;
        } catch (SQLException e) {
            throw new RuntimeException("Kunne ikke finde udstillinger efter dato: " + date, e);
        }
    }

    @Override
    public void deleteById(Long id) {
        // First check if there are registrations for this exhibition
        String checkSql = "SELECT COUNT(*) FROM registration WHERE showId = ?";

        try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
            checkStmt.setLong(1, id);

            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    // There are registrations - delete them first
                    String deleteTilmeldingSql = "DELETE FROM registration WHERE showId = ?";
                    try (PreparedStatement deleteRegStmt = connection.prepareStatement(deleteTilmeldingSql)) {
                        deleteRegStmt.setLong(1, id);
                        deleteRegStmt.executeUpdate();
                    }
                }
            }

            // Now we can safely delete the exhibition
            String sql = "DELETE FROM exhibitions WHERE showId = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setLong(1, id);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Kunne ikke slette udstilling med iD: " + id, e);
        }
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM exhibitions WHERE showId = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Kunne ikke finde udstilling med ID: " + id, e);
        }
    }

    private Udstilling mapResultSetToUdstilling(ResultSet rs) throws SQLException {
        String name = rs.getString("showName");
        String location = rs.getString("location");
        int cost = rs.getInt("cost");

        Udstilling udstilling = new Udstilling(name, location, cost);
        udstilling.setId(rs.getLong("showId"));

        Date date = rs.getDate("occasion");
        if (date != null) {
            udstilling.setDate(new java.util.Date(date.getTime()));
        }

        return udstilling;
    }
}