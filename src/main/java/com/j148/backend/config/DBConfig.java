package com.j148.backend.config;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;


public abstract class DBConfig {
    private static BasicDataSource basicDataSource;

    static {
        try {
            basicDataSource = new BasicDataSource();
            basicDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");

            // RDS Configuration - replace with your actual RDS details
            basicDataSource.setUrl(System.getenv("com.mysql.cj.jdbc.Driver//localhost:8080/J148FinalBackend/hrms"));
            basicDataSource.setUsername(System.getenv("root"));
            basicDataSource.setPassword(System.getenv("root"));

            // Connection Pool Settings
            basicDataSource.setMinIdle(20);
            basicDataSource.setMaxIdle(20);
            basicDataSource.setMaxOpenPreparedStatements(150);
//
//            // RDS-specific optimizations
//            basicDataSource.setValidationQuery("SELECT 1");
//            basicDataSource.setTestOnBorrow(true);
//            basicDataSource.setMaxWaitMillis(20000);

        } catch (Exception e) {
            throw new ExceptionInInitializerError("Database initialization failed: " + e.getMessage());
        }
    }

    protected static Connection getCon() throws SQLException {

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
