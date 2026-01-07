package org.example.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    
    private static final String HOST = System.getenv().getOrDefault("DB_HOST", "localhost");
    private static final String PORT = System.getenv().getOrDefault("DB_PORT", "3306");
    private static final String DB_NAME = System.getenv().getOrDefault("DB_NAME", "projet_db");
    private static final String USER = System.getenv().getOrDefault("DB_USER", "user_projet");
    private static final String PASSWORD = System.getenv().getOrDefault("DB_PASSWORD", "password123");

    private static final String URL = String.format("jdbc:mysql://%s:%s/%s?useUnicode=true&characterEncoding=UTF-8", HOST, PORT, DB_NAME);

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
