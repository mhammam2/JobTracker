package com.example.jobtracker;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:job_tracker.db";  // SQLite database file path

    // Get the database connection
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    // Initialize the database by creating tables
    public static void initializeDatabase() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS JobApplications ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "companyName TEXT NOT NULL, "
                + "jobTitle TEXT NOT NULL, "
                + "status TEXT NOT NULL, "
                + "applicationDate TEXT NOT NULL, "
                + "notes TEXT)";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
