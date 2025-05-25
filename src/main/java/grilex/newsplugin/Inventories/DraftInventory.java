package grilex.newsplugin.Inventories;

import grilex.newsplugin.Core.NewsCore;
import grilex.newsplugin.Items.ItemFactory;
import grilex.newsplugin.NewsPlugin;
import grilex.newsplugin.Utils.ChatUtils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DraftInventory {
    private final List<Inventory> inventories = new ArrayList<>();
    private final FileConfiguration menuConfig;
    private final NewsCore newsCore;
    private final ItemFactory itemFactory;
    private final ChatUtils chatUtils;

    public DraftInventory(NewsPlugin plugin, NewsCore newsCore, ItemFactory itemFactory, ChatUtils chatUtils) throws SQLException {
        menuConfig = plugin.getMenuConfig().getConfig();
        this.newsCore = newsCore;
        this.itemFactory = itemFactory;
        this.chatUtils = chatUtils;
        createNewInventory();
    }

    public void createNewInventory() throws SQLException {
        int menuSize = menuConfig.getInt("draft_inventory.menu_line_size") * 9;
        String menuName = menuConfig.getString("draft_inventory.menu_name");
        int draftCount = newsCore.getDatabaseManager().countItems("drafts");
        int fullInventoriesCount = (int) Math.ceil((double) draftCount / (menuSize - 9));

        for (int i = 0; i < fullInventoriesCount; i++) {
            Inventory inventory = createInventory(menuSize, menuName, i, fullInventoriesCount);
            inventories.add(inventory);
        }
    }


    private Inventory createInventory(int menuSize, String menuName, int index, int total) throws SQLException {
        Inventory inventory = Bukkit.createInventory(null, menuSize, chatUtils.hexColorString(menuName + "(" + (index + 1) + "/" + total + ")"));
        fillInventory(inventory, index, menuSize);
        setNavigationItems(inventory, index, total);
        return inventory;
    }

    private void fillInventory(Inventory inventory, int index, int menuSize) throws SQLException {
        int draftCount = newsCore.getDatabaseManager().countItems("drafts");
        int itemId = index * (menuSize - 9) + 1;

        for (int j = 0; j < (menuSize - 9) && itemId <= draftCount; j++) {
            ItemStack book = createBook(itemId);
            inventory.setItem(j, book);
            itemId++;
        }
    }

    private ItemStack createBook(int itemId) throws SQLException {
        ItemStack book = itemFactory.createBook(
                menuConfig.getString("draft_inventory.standard_news_name"),
                menuConfig.getString("draft_inventory.author"),
                newsCore.getDatabaseManager().getTextAsList(itemId, "drafts")
        );
        itemFactory.addInfo(book, menuConfig.getStringList("draft_inventory.lore"));
        return book;
    }

    private void setNavigationItems(Inventory inventory, int index, int total) {
        if (index < total - 1) {
            inventory.setItem(inventory.getSize() - 1, itemFactory.createPlayerSkull(
                    menuConfig.getString("special_items.forward.player"),
                    chatUtils.hexColorString(menuConfig.getString("special_items.forward.name")),
                    menuConfig.getInt("special_items.forward.nbt")));
        }
        if (index > 0) {
            inventory.setItem(inventory.getSize() - 9, itemFactory.createPlayerSkull(
                    menuConfig.getString("special_items.back.player"),
                    chatUtils.hexColorString(menuConfig.getString("special_items.back.name")),
                    menuConfig.getInt("special_items.back.nbt")));
        }
    }

    public int getCurrentPage() {
        return 0;
    }

    public List<Inventory> getDraftInventory() {
        return inventories;
    }
}
