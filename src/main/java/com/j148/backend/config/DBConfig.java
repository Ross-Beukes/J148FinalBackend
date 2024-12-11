package com.j148.backend.config;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.commons.dbcp2.BasicDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class DBConfig {
    private static final Logger logger = Logger.getLogger(DBConfig.class.getName());
    private static BasicDataSource basicDataSource;

    static {
        basicDataSource = new BasicDataSource();
        basicDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        basicDataSource.setUsername("admin");
        basicDataSource.setPassword("Hangwelani");
        basicDataSource.setUrl("jdbc:mysql://hrms.ctqm24m4mbs5.af-south-1.rds.amazonaws.com:3306/hrms?useSSL=false"
                + "&rewriteBatchedStatements=true"
                + "&cachePrepStmts=true"
                + "&useServerPrepStmts=true"
                + "&prepStmtCacheSize=250"
                + "&prepStmtCacheSqlLimit=2048");

        // High capacity connection pool settings
        basicDataSource.setInitialSize(25);           // Start with 25 connections
        basicDataSource.setMaxTotal(150);             // Maximum 150 total connections (allows for spikes)
        basicDataSource.setMinIdle(25);               // Minimum 25 idle connections
        basicDataSource.setMaxIdle(50);               // Maximum 50 idle connections

        // Performance optimized validation settings
        basicDataSource.setTestOnBorrow(false);       // Skip testing on borrow for performance
        basicDataSource.setTestWhileIdle(true);       // Test connections while idle
        basicDataSource.setValidationQuery("SELECT 1");
        basicDataSource.setValidationQueryTimeout(3);  // 3 second timeout

        // Aggressive timeout settings for high throughput
        basicDataSource.setMaxWaitMillis(2000);       // Wait 2 seconds max for connection
        basicDataSource.setRemoveAbandonedTimeout(60); // 60 seconds for abandoned connections
        basicDataSource.setRemoveAbandonedOnBorrow(true);
        basicDataSource.setLogAbandoned(true);        // Log abandoned connections for debugging

        // Connection pooling optimizations
        basicDataSource.setPoolPreparedStatements(true);
        basicDataSource.setMaxOpenPreparedStatements(100);

        // Fast connection cleanup
        basicDataSource.setTimeBetweenEvictionRunsMillis(15000); // Run evictor every 15 seconds
        basicDataSource.setMinEvictableIdleTimeMillis(60000);    // Evict connections idle for 1 minute

        // JVM memory optimizations
        basicDataSource.setFastFailValidation(true);
        basicDataSource.setCacheState(true);
        basicDataSource.setDefaultAutoCommit(false);   // Manage transactions manually for better performance

        logger.log(Level.INFO, "High capacity database connection pool initialized with max {0} connections",
                basicDataSource.getMaxTotal());
    }

    public Connection getCon() throws SQLException {
        try {
            Connection con = basicDataSource.getConnection();
            con.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
            return con;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to get connection. Active: " + basicDataSource.getNumActive()
                    + ", Idle: " + basicDataSource.getNumIdle(), e);
            throw e;
        }
    }

    public void close() throws SQLException {
        if (basicDataSource != null) {
            logger.info("Closing database connection pool. Final stats: " + getPoolStats());
            basicDataSource.close();
        }
    }

    public String getPoolStats() {
        return String.format("Active: %d, Idle: %d, Total: %d",
                basicDataSource.getNumActive(),
                basicDataSource.getNumIdle(),
                basicDataSource.getMaxTotal());
    }
}