package com.j148.backend.config;

import com.j148.backend.contractor.model.Contractor;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public abstract class DBConfig {
    private static BasicDataSource basicDataSource;

    static {

        basicDataSource = new BasicDataSource();
        basicDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        basicDataSource.setUsername("root");
        basicDataSource.setPassword("root");
        basicDataSource.setUrl("jdbc:mysql://localhost:3306/hrms?autoReconnect=true&useSSL=false");
        basicDataSource.setMinIdle(10);
        basicDataSource.setMaxIdle(10);
        basicDataSource.setMaxOpenPreparedStatements(100);
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