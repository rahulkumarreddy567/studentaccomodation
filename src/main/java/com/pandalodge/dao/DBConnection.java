package com.pandalodge.dao;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    public static Connection getConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection("jdbc:sqlite:data/sa.db");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

