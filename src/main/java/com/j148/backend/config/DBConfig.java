package com.j148.backend.config;

import com.j148.backend.contractor.model.Contractor;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public abstract class DBConfig {
    private static BasicDataSource basicDataSource;

    static {
<<<<<<< Updated upstream

        basicDataSource = new BasicDataSource();
        basicDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        basicDataSource.setUsername("root");
        basicDataSource.setPassword("root");
        basicDataSource.setUrl("jdbc:mysql://localhost:3306/hrms?autoReconnect=true&useSSL=false");
        basicDataSource.setMinIdle(10);
        basicDataSource.setMaxIdle(10);
        basicDataSource.setMaxOpenPreparedStatements(100);
=======
        try {
            basicDataSource = new BasicDataSource();
            basicDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
            
             // Environment variables or defaults
            String dbUrl = System.getenv("RDS_URL") != null ? System.getenv("RDS_URL") : "jdbc:mysql://localhost:1527/hrms";
            String dbUsername = System.getenv("RDS_USERNAME") != null ? System.getenv("RDS_USERNAME") : "root";
            String dbPassword = System.getenv("RDS_PASSWORD") != null ? System.getenv("RDS_PASSWORD") : "root";

            basicDataSource.setUrl(dbUrl);
            basicDataSource.setUsername(dbUsername);
            basicDataSource.setPassword(dbPassword);

            /* RDS Configuration - replace with your actual RDS details
            basicDataSource.setUrl(System.getenv("RDS_URL"));
            basicDataSource.setUsername(System.getenv("RDS_USERNAME"));
            basicDataSource.setPassword(System.getenv("RDS_PASSWORD"));

            **/ // Connection Pool Settings
            basicDataSource.setMinIdle(20);
            basicDataSource.setMaxIdle(20);
            basicDataSource.setMaxOpenPreparedStatements(150);

            // RDS-specific optimizations
            basicDataSource.setValidationQuery("SELECT 1");
            basicDataSource.setTestOnBorrow(true);
            basicDataSource.setMaxWaitMillis(20000);

        } catch (Exception e) {
            throw new ExceptionInInitializerError("Database initialization failed: " + e.getMessage());
        }
>>>>>>> Stashed changes
    }

    public static Connection getCon() throws SQLException {
        Connection con = basicDataSource.getConnection();
        con.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
        return con;
    }

    public static void close() throws SQLException {
        if (basicDataSource != null) {
            basicDataSource.close();
        }
    }
}