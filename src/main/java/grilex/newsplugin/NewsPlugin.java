package grilex.newsplugin;

import grilex.newsplugin.Commands.NewsCommand;
import grilex.newsplugin.Events.PlayerInventoryClickEvent;
import grilex.newsplugin.Utils.ConfigUtils.ConfigUtils;
import grilex.newsplugin.Utils.InventoryUtils.InventoryManager;
import org.bukkit.Bukkit;

import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public final class NewsPlugin extends JavaPlugin {

    private InventoryManager inventoryManager;
    private ConfigUtils menuConfig ;
    private static  NewsPlugin instance;


    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfigs();
        instance = this;
        new NewsCommand();
        try {
            inventoryManager = new InventoryManager(this);
            Bukkit.getPluginManager().registerEvents(new PlayerInventoryClickEvent(this),this);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void onDisable() {

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
