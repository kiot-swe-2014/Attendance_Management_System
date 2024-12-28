package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class ConnectionProvider {
    private static final String DB_NAME = "attendancejframebd";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "";

    // Method to establish a connection
    public static Connection getCon() {
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Connect to MySQL without specifying a database
            Connection con = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            
            // Check if the database exists, and create it if not
            if (!databaseExists(con, DB_NAME)) {
                createDatabase(con, DB_NAME);
            }
            
            // Connect to the specified database
            return DriverManager.getConnection(DB_URL + DB_NAME, DB_USERNAME, DB_PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Helper method to check if a database exists
    private static boolean databaseExists(Connection con, String dbName) throws Exception {
        String query = "SHOW DATABASES LIKE '" + dbName + "'";
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            return rs.next();
        }
    }

    // Helper method to create a database if it doesn't exist
    private static void createDatabase(Connection con, String dbName) throws Exception {
        String query = "CREATE DATABASE " + dbName;
        try (Statement stmt = con.createStatement()) {
            stmt.executeUpdate(query);
            System.out.println("Database '" + dbName + "' created successfully.");
        }
    }
}
