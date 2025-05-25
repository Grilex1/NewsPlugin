package grilex.newsplugin.Utils.InventoryUtils;

import org.bukkit.inventory.Inventory;

import java.util.Set;

public interface InventoryFactory {
    void addInventory(String name, Inventory inventory);

    void removeInventory(String name);

    boolean inventoryExists(String name);

    Set<String> getAllInventoryNames();
}
