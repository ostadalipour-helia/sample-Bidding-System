package server.ws;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

public class DatabaseUtil {
    private static final String DB_URL = "jdbc:sqlite:" + new File(System.getProperty("catalina.base"), "webapps/PracticeJsonWebSocketServer2/WEB-INF/items.db").getAbsolutePath();
    
    static {
        try {
            // Explicitly load the SQLite driver
            Class.forName("org.sqlite.JDBC");
            System.out.println("Database URL: " + DB_URL);
            
            // Create database directory if it doesn't exist
            File dbFile = new File(System.getProperty("catalina.base"), "webapps/PracticeJsonWebSocketServer2/WEB-INF");
            if (!dbFile.exists()) {
                dbFile.mkdirs();
            }
            
            // Initialize database and create table
            try (Connection conn = getConnection()) {
                String createTableSQL = 
                    "CREATE TABLE IF NOT EXISTS items (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "code TEXT NOT NULL," +
                    "name TEXT NOT NULL," +
                    "description TEXT" +
                    ")";
                
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(createTableSQL);
                    System.out.println("Database table created successfully");
                    
                    // Check if table is empty and insert default data if needed
                    ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM items");
                    if (rs.next() && rs.getInt(1) == 0) {
                        String[] insertData = {
                            "INSERT INTO items (code, name, description) VALUES ('S1', 'Item 1', 'Description for Item 1')",
                            "INSERT INTO items (code, name, description) VALUES ('S2', 'Item 2', 'Description for Item 2')",
                            "INSERT INTO items (code, name, description) VALUES ('S3', 'Item 3', 'Description for Item 3')"
                        };
                        
                        for (String sql : insertData) {
                            stmt.execute(sql);
                        }
                        System.out.println("Default data inserted successfully");
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Database initialization error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
    
    public static List<Item> getAllItems() {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT * FROM items";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Item item = new Item(
                    rs.getString("code"),
                    rs.getString("name"),
                    rs.getString("description")
                );
                items.add(item);
            }
        } catch (SQLException e) {
            System.err.println("Error getting items: " + e.getMessage());
            e.printStackTrace();
        }
        
        return items;
    }
    
    public static Item getItemByCode(String code) {
        String sql = "SELECT * FROM items WHERE code = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, code);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Item(
                    rs.getString("code"),
                    rs.getString("name"),
                    rs.getString("description")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error getting item by code: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
}