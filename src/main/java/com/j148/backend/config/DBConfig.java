package com.j148.backend.config;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.commons.dbcp2.BasicDataSource;
import java.sql.Connection;
import java.sql.SQLException;
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
        basicDataSource.setUrl("jdbc:mysql://hrms.ctqm24m4mbs5.af-south-1.rds.amazonaws.com:3306/hrms?useSSL=false");
        basicDataSource.setInitialSize(10);
        basicDataSource.setMaxTotal(80);
        basicDataSource.setMaxIdle(20);
        basicDataSource.setMaxWaitMillis(5000);
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