package com.j148.backend.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class DBConfig {
    private static final Logger LOGGER = Logger.getLogger(DBConfig.class.getName());
    private HikariDataSource dataSource;

    @PostConstruct
    public void init() {
        try {
            HikariConfig config = new HikariConfig();

            // Basic Configuration
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");

            // Get configuration from environment variables
            String username = System.getenv("RDS_USERNAME");
            String password = System.getenv("RDS_PASSWORD");

            if (username == null || password == null) {
                throw new IllegalStateException("Database configuration environment variables not set");
            }

            config.setJdbcUrl("jdbc:mysql://hrms.ctqm24m4mbs5.af-south-1.rds.amazonaws.com:3306/hrms");
            config.setUsername(username);
            config.setPassword(password);

            // Pool Configuration - Optimized for 10 concurrent users
            config.setMaximumPoolSize(10);        // Maximum number of actual connections
            config.setMinimumIdle(5);             // Minimum number of idle connections
            config.setIdleTimeout(300000);        // 5 minutes - How long a connection can remain idle
            config.setMaxLifetime(600000);        // 10 minutes - Maximum lifetime of a connection
            config.setConnectionTimeout(20000);    // 20 seconds - How long to wait for a connection

            // Performance Optimization
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.addDataSourceProperty("useServerPrepStmts", "true");

            // Connection testing
            config.setConnectionTestQuery("SELECT 1");
            config.setValidationTimeout(TimeUnit.SECONDS.toMillis(5));

            // Leak detection
            config.setLeakDetectionThreshold(60000); // 1 minute

            // Pool name for easier monitoring
            config.setPoolName("HRMSConnectionPool");

            dataSource = new HikariDataSource(config);

            LOGGER.info("HikariCP connection pool initialized successfully");
            logPoolConfiguration();

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize database connection pool", e);
            throw new ExceptionInInitializerError("Database initialization failed: " + e.getMessage());
        }
    }

    @Produces
    @ApplicationScoped
    public Connection getCon() throws SQLException {
        try {
            Connection connection = dataSource.getConnection();
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED); // Changed from UNCOMMITTED for better data consistency
            return connection;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error obtaining database connection", e);
            throw e;
        }
    }

    @PreDestroy
    public void cleanup() {
        try {
            if (dataSource != null && !dataSource.isClosed()) {
                dataSource.close();
                LOGGER.info("Database connection pool closed successfully");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error closing database connection pool", e);
        }
    }

    private void logPoolConfiguration() {
        LOGGER.info(String.format("""
            HikariCP Pool Configuration:
            Maximum Pool Size: %d
            Minimum Idle: %d
            Connection Timeout: %d ms
            Idle Timeout: %d ms
            Max Lifetime: %d ms
            """,
                dataSource.getMaximumPoolSize(),
                dataSource.getMinimumIdle(),
                dataSource.getConnectionTimeout(),
                dataSource.getIdleTimeout(),
                dataSource.getMaxLifetime()
        ));
    }

    // Method to get pool statistics - useful for monitoring
    public String getPoolStats() {
        return String.format("""
            Active Connections: %d
            Idle Connections: %d
            Total Connections: %d
            Waiting Threads: %d
            """,
                dataSource.getHikariPoolMXBean().getActiveConnections(),
                dataSource.getHikariPoolMXBean().getIdleConnections(),
                dataSource.getHikariPoolMXBean().getTotalConnections(),
                dataSource.getHikariPoolMXBean().getThreadsAwaitingConnection()
        );
    }
}