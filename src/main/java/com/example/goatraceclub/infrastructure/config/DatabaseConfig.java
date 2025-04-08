package com.example.goatraceclub.infrastructure.config;

import com.example.goatraceclub.infrastructure.implementations.KæledyrRepository;
import com.example.goatraceclub.infrastructure.implementations.MedlemRepository;
import com.example.goatraceclub.infrastructure.implementations.TilmeldingsRepository;
import com.example.goatraceclub.infrastructure.implementations.UdstillingsRepository;
import com.example.goatraceclub.infrastructure.interfaces.IKæledyrRepository;
import com.example.goatraceclub.infrastructure.interfaces.IMedlemRepository;
import com.example.goatraceclub.infrastructure.interfaces.ITilmeldingsRepository;
import com.example.goatraceclub.infrastructure.interfaces.IUdstillingsRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Configuration
public class DatabaseConfig {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/goatraceclub";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root"; // Add your password here if needed

    @Bean
    public Connection databaseConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        return connection;
    }

    @Bean
    public IMedlemRepository medlemRepository(Connection connection) {
        return new MedlemRepository(connection);
    }

    @Bean
    public IKæledyrRepository kæledyrRepository(Connection connection, IMedlemRepository medlemRepository) {
        return new KæledyrRepository(connection, medlemRepository);
    }

    @Bean
    public IUdstillingsRepository udstillingsRepository(Connection connection) {
        return new UdstillingsRepository(connection);
    }

    @Bean
    public ITilmeldingsRepository tilmeldingsRepository(Connection connection,
                                                        IKæledyrRepository kæledyrRepository,
                                                        IUdstillingsRepository udstillingsRepository) {
        return new TilmeldingsRepository(connection, kæledyrRepository, udstillingsRepository);
    }

    @Bean
    public void setupRepositoryDependencies(IMedlemRepository medlemRepository, IKæledyrRepository kæledyrRepository) {
        ((MedlemRepository) medlemRepository).setKæledyrRepository(kæledyrRepository);
    }
}