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

        // Better connection pool settings for t3.medium
        basicDataSource.setInitialSize(5);
        basicDataSource.setMinIdle(5);
        basicDataSource.setMaxIdle(10);
        basicDataSource.setMaxTotal(30);
        basicDataSource.setMaxWaitMillis(10000);

        // Connection validation
        basicDataSource.setTestOnBorrow(true);
        basicDataSource.setTestWhileIdle(true);
        basicDataSource.setValidationQuery("SELECT 1");
        basicDataSource.setValidationQueryTimeout(5);

        // Connection cleanup
        basicDataSource.setRemoveAbandonedOnBorrow(true);
        basicDataSource.setRemoveAbandonedTimeout(60);

        // Better transaction isolation
        basicDataSource.setDefaultTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
    }

    public Connection getCon() throws SQLException {
        return basicDataSource.getConnection();
    }

    public void close() throws SQLException {
        if (basicDataSource != null) {
            basicDataSource.close();
        }
    }
}