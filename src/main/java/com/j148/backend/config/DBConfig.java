package com.j148.backend.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class DBConfig {
    private static final Logger LOGGER = Logger.getLogger(DBConfig.class.getName());
    private BasicDataSource dataSource;

    @PostConstruct
    public void init() {
        try {
            dataSource = new BasicDataSource();
            dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");

            // Get configuration from environment variables
            String url = System.getenv("RDS_URL");
            String username = System.getenv("RDS_USERNAME");
            String password = System.getenv("RDS_PASSWORD");

            if (url == null || username == null || password == null) {
                throw new IllegalStateException("Database configuration environment variables not set");
            }

            dataSource.setUrl(url);
            dataSource.setUsername(username);
            dataSource.setPassword(password);

            // Connection Pool Settings
            dataSource.setMinIdle(20);
            dataSource.setMaxIdle(20);
            dataSource.setMaxOpenPreparedStatements(150);

            // RDS-specific optimizations
            dataSource.setValidationQuery("SELECT 1");
            dataSource.setTestOnBorrow(true);
            dataSource.setMaxWaitMillis(20000);

            LOGGER.info("Database connection pool initialized successfully");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize database connection pool", e);
            throw new ExceptionInInitializerError("Database initialization failed: " + e.getMessage());
        }
    }

    @Produces
    @ApplicationScoped
    public Connection  getCon() throws SQLException {
        try {
            Connection connection = dataSource.getConnection();
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
            return connection;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error obtaining database connection", e);
            throw e;
        }
    }

    @PreDestroy
    public void cleanup() {
        try {
            if (dataSource != null) {
                dataSource.close();
                LOGGER.info("Database connection pool closed successfully");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error closing database connection pool", e);
        }
    }
}