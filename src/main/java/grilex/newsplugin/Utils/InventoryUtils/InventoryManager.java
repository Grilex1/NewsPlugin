package grilex.newsplugin.Utils.InventoryUtils;

import grilex.newsplugin.Inventories.DraftInventory;
import grilex.newsplugin.Inventories.EditInventory;
import grilex.newsplugin.Inventories.NewsInventory;
import grilex.newsplugin.NewsPlugin;
import org.bukkit.inventory.Inventory;

import java.sql.SQLException;

import java.util.HashMap;
import java.util.Map;

public class InventoryManager {
    private final HashMap<String, Map<Integer, Inventory>> inventoriesMap = new HashMap<>();
    private final HashMap<String, Inventory> inventories = new HashMap<>();

    public InventoryManager(NewsPlugin plugin) throws SQLException {
        inventoriesMap.put("news", new NewsInventory(plugin).getNewsInventory());
        inventories.put("edit", new EditInventory(plugin).getEditInventory());
        inventoriesMap.put("draft", new DraftInventory(plugin).getDraftInventory());
    }

    public Map<Integer, Inventory> getInventoryMap(String name) {
        return inventoriesMap.get(name);
    }
    public Inventory getInventory(String name) {
        return inventories.get(name);
    }
}
