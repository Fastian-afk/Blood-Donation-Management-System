package app.database;

import java.sql.*;

/**
 * DatabaseConnection - Singleton Pattern
 * Ensures only ONE database connection exists throughout the application
 */

public class DatabaseConnection {
    
    private static DatabaseConnection instance;
    private static Connection connection;
    
    // Database Configuration
    private static final String DB_URL = "jdbc:mysql://bdms-prod-imdufazal-71c9.c.aivencloud.com:16789/defaultdb?sslMode=REQUIRED";
    private static final String DB_USER = "avnadmin";
    private static final String DB_PASSWORD = "YOUR_PASSWORD_HERE"; 
    
    /**
     * Private Constructor - Singleton Pattern
     * Prevents direct instantiation
     */
    
    private DatabaseConnection() {
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Establish Connection
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            
            System.out.println("✅ Database Connected Successfully!");
            
            // bdms_schema.txt file is now the single source of truth.
            
        } catch (ClassNotFoundException e) {
            System.err.println("❌ MySQL JDBC Driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Database Connection Failed!");
            System.err.println("❌ Make sure your MySQL server is running and the 'bdms_db' database exists.");
            e.printStackTrace();
        }
    }
    
    /**
     * Get Singleton Instance
     */
    
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }
    
    /**
     * Get Connection Object
     */
    
    public static Connection getConnection() {
        try {
            // Check if connection is still alive or null
            if (connection == null || connection.isClosed()) {
                System.out.println("🔧 Re-establishing database connection...");
                instance = new DatabaseConnection();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
    
    /**
     * Close Database Connection
     */
    
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✅ Database Connection Closed!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Test Database Connection
     */
    
    public boolean testConnection() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}