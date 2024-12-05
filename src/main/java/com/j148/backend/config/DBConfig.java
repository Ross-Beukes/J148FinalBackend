package com.j148.backend.config;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.commons.dbcp2.BasicDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

  // Make it injectable
public class DBConfig {
    private static final Logger logger = Logger.getLogger(DBConfig.class.getName());
    private static BasicDataSource basicDataSource;

    static {
        basicDataSource = new BasicDataSource();
        basicDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        basicDataSource.setUsername("root");
        basicDataSource.setPassword("root");
        basicDataSource.setUrl("jdbc:mysql://localhost:3306/hrms?autoReconnect=true&useSSL=false");
        basicDataSource.setMinIdle(10);
        basicDataSource.setMaxIdle(10);
        basicDataSource.setMaxOpenPreparedStatements(150);
    }

    public Connection getCon() throws SQLException {
        Connection con = basicDataSource.getConnection();
        con.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
        return con;
    }

    public void close() throws SQLException {
        if (basicDataSource != null) {
            basicDataSource.close();
        }
    }
}