package grilex.newsplugin;

import grilex.newsplugin.Commands.NewsCommand;
import grilex.newsplugin.Core.NewsCore;
import grilex.newsplugin.Events.PlayerInventoryClickEvent;
import grilex.newsplugin.Items.ItemFactory;
import grilex.newsplugin.Utils.ChatUtils.ChatUtils;
import grilex.newsplugin.Utils.ConfigUtils.ConfigUtils;
import grilex.newsplugin.Utils.DatabaseUtils.DatabaseManager;
import grilex.newsplugin.Utils.InventoryUtils.InventoryManager;
import org.bukkit.Bukkit;

import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public final class NewsPlugin extends JavaPlugin {

    private InventoryManager inventoryManager;
    private ConfigUtils menuConfig ;
    private static NewsPlugin instance;
    private DatabaseManager manager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfigs();
        instance = this;
        manager = new NewsCore().getDatabaseManager();
        try {
            inventoryManager = new InventoryManager(this, new NewsCore(), new ItemFactory(),new ChatUtils());
            Bukkit.getPluginManager().registerEvents(new PlayerInventoryClickEvent(this),this);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        new NewsCommand();
    }

    @Override
    public void onDisable() {
        try {
            manager.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadConfigs() {
        menuConfig = ConfigUtils.of(this,"menu.yml");
    }
    public ConfigUtils getMenuConfig() {
        return menuConfig;
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }
    public static NewsPlugin getInstance(){
        return instance;
    }
}
