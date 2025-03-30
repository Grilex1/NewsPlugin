package grilex.newsplugin.Utils.DatabaseUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DatabaseManager {
    private Connection connection;
    public DatabaseManager(String dbUrl, String user, String password) throws SQLException {
        connection = DriverManager.getConnection(dbUrl, user, password);
        createTable();
    }

    private void createTable() throws SQLException {
        String sqlClean = "CREATE TABLE IF NOT EXISTS clean ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "text TEXT"
                + ")";
        String sqlDrafts = "CREATE TABLE IF NOT EXISTS drafts ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "text TEXT"
                + ")";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sqlClean);
            stmt.execute(sqlDrafts);
        }

    }

    public void createItem(String text, String table) throws SQLException {
        if (text == null || text.isEmpty()) {
            return;
        }
        String sql = "INSERT INTO " + table + " (text) VALUES (?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, text);
                pstmt.executeUpdate();

        }
    }

    public void readItems(String table) throws SQLException {
        String sql = "SELECT * FROM "+ table;
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + ", Text: " + rs.getString("text") + ", Created At: " + rs.getTimestamp("created_at"));            }
        }
    }

    public void updateItem(int id, String newText, String table) throws SQLException {
        if (newText == null || newText.isEmpty()) {
            return;
        }
        String sql = "UPDATE " + table + " SET text = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newText);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        }
    }

  
    public void deleteItem(int id, String table) throws SQLException {
        String sql = "DELETE FROM " + table + " WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
    public int countItems(String table) throws SQLException {
        String sql = "SELECT COUNT(*) AS count FROM " + table;
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("count");
            }
        }
        return 0;
    }
    public List<String> getTextAsList(int itemId, String table) throws SQLException {
        String sql = "SELECT text FROM " + table + " WHERE id = ? LIMIT 1";
        List<String> lines = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, itemId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String text = rs.getString("text");
                    if (text != null) {
                        lines = Arrays.stream(text.split("\n"))
                                .map(String::trim)
                                .filter(line -> !line.isEmpty())
                                .collect(Collectors.toList());
                    }
                }
            }
        }

        return lines;
    }

    public void close() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }
}
