package grilex.newsplugin.Core;

import grilex.newsplugin.NewsPlugin;
import grilex.newsplugin.Utils.DatabaseUtils.DatabaseManager;

import java.sql.SQLException;

public class NewsCore {
    private DatabaseManager databaseManager;

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public NewsCore() {
       try {
            String host = NewsPlugin.getInstance().getConfig().getString("databases.host");
            String port = NewsPlugin.getInstance().getConfig().getString("databases.port");
            String database = NewsPlugin.getInstance().getConfig().getString("databases.database");
            String user = NewsPlugin.getInstance().getConfig().getString("databases.user");
            String password = NewsPlugin.getInstance().getConfig().getString("databases.password");
            databaseManager = new DatabaseManager(
                    "jdbc:mysql://" + host + ":" + port + "/" + database,
                    user,
                    password
            );
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }
}
