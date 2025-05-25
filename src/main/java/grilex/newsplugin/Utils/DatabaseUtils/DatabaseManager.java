package grilex.newsplugin.Utils.DatabaseUtils;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.bukkit.Material.WRITTEN_BOOK;

public class DatabaseManager {
    private final Connection connection;

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

    public void post(InventoryClickEvent event, int currentPageDrafts) throws SQLException {
        if (event.getCurrentItem() == null) {
            return;
        }
        if (event.getClick() == ClickType.SHIFT_LEFT &&
                event.getCurrentItem().getType() == WRITTEN_BOOK) {
            int itemId = currentPageDrafts * 45 + event.getSlot() + 1;
            try {
                String itemText = String.valueOf(getTextAsList(itemId, "drafts"));
                itemText = itemText.replaceAll("[\\[\\]]", "");
                createItem(itemText, "clean");
                deleteItem(itemId, "drafts");
                updateId("drafts");
            } catch (SQLException e) {
                throw new SQLException(e);
            }
            event.getInventory().setItem(event.getSlot(), null);

        }
    }

    public void delete(InventoryClickEvent event, int currentPageDrafts) throws SQLException {
        if (event.getCurrentItem() == null) {
            return;
        }
        if (event.getClick() == ClickType.SHIFT_RIGHT &&
                event.getCurrentItem().getType() == WRITTEN_BOOK) {

            int itemId = currentPageDrafts * 45 + event.getSlot() + 1;
            try {
                deleteItem(itemId, "drafts");
                updateId("drafts");
            } catch (SQLException e) {
                throw new SQLException(e);
            }
            event.getInventory().setItem(event.getSlot(), null);
        }
    }
    private void updateId(String table) throws SQLException {
        String setSql = "SET @new_id = 0;";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(setSql);
        }

        String updateSql = "UPDATE " + table + " SET id = (@new_id := @new_id + 1) ORDER BY id;";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(updateSql);

        }}

    public void close() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }
}
