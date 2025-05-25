package grilex.newsplugin.Utils.InventoryUtils;

import grilex.newsplugin.Core.NewsCore;
import grilex.newsplugin.Inventories.DraftInventory;
import grilex.newsplugin.Inventories.EditInventory;
import grilex.newsplugin.Inventories.NewsInventory;
import grilex.newsplugin.Items.ItemFactory;
import grilex.newsplugin.NewsPlugin;
import grilex.newsplugin.Utils.ChatUtils.ChatUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.persistence.PersistentDataType;

import java.sql.SQLException;
import java.util.*;

public class InventoryManager implements InventoryFactory {
    private final HashMap<String, List<Inventory>> inventories = new HashMap<>();
    private final NewsPlugin plugin;
    private final NewsCore newsCore;
    private final ItemFactory itemFactory;
    private final ChatUtils chatUtils;

    public InventoryManager(NewsPlugin plugin, NewsCore newsCore, ItemFactory itemFactory, ChatUtils chatUtils) throws SQLException {
        this.plugin = plugin;
        this.newsCore = newsCore;
        this.itemFactory = itemFactory;
        this.chatUtils = chatUtils;

        initializeInventories();
    }

    private void initializeInventories() throws SQLException {
        addInventory("edit", new EditInventory(plugin).getEditInventory());

        List<Inventory> draftInventories = new DraftInventory(plugin, newsCore, itemFactory, chatUtils).getDraftInventory();
        List<Inventory> newsInventories = new NewsInventory(plugin, newsCore, itemFactory, chatUtils).getNewsInventory();

        for (Inventory draftInventory : draftInventories) {
            addInventory("draft", draftInventory);
        }
        for (Inventory newsInventory : newsInventories) {
            addInventory("news", newsInventory);
        }
    }

    public int changePage(InventoryClickEvent event, String inventoryType, int currentPage) {
        Player player = (Player) event.getWhoClicked();

        if (event.getCurrentItem() == null ||
            event.getCurrentItem().
            getItemMeta().
            getPersistentDataContainer().
            get(NamespacedKey.fromString("special"), PersistentDataType.INTEGER) == null) {
            return currentPage;
        }
        int changeValue =
                Objects.requireNonNull(event.
                getCurrentItem().
                getItemMeta()).
                getPersistentDataContainer().
                get(Objects.requireNonNull(NamespacedKey.fromString("special")), PersistentDataType.INTEGER);
        int newPage = currentPage + changeValue;
        player.closeInventory();
        player.openInventory(this.getInventories(inventoryType).get(newPage));
        return newPage;
    }



    @Override
    public void addInventory(String name, Inventory inventory) {
        inventories.computeIfAbsent(name, k -> new ArrayList<>()).add(inventory);
    }

    @Override
    public void removeInventory(String name) {
        inventories.remove(name);
    }

    @Override
    public boolean inventoryExists(String name) {
        return inventories.containsKey(name) && !inventories.get(name).isEmpty();
    }

    @Override
    public Set<String> getAllInventoryNames() {
        return inventories.keySet();
    }

    public List<Inventory> getInventories(String name) {
        return inventories.get(name);
    }
}