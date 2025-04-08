package com.example.goatraceclub.infrastructure.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration(exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
public class AppConfig {
    // This empty configuration class is used to disable the auto-configuration
    // of database-related components in Spring Boot.
    //
    // By excluding these auto-configurations, Spring Boot won't attempt to
    // create a DataSource, TransactionManager, or JPA EntityManagerFactory,
    // which means it won't complain about missing database drivers or
    // connection properties.
    //
    // This approach is useful for:
    // 1. Getting the application started quickly without database configuration
    // 2. Focusing on UI/frontend development first
    // 3. Situations where you need to manually configure database connections later
}