package grilex.newsplugin.Events;

import grilex.newsplugin.Core.NewsCore;
import grilex.newsplugin.Inventories.DraftInventory;
import grilex.newsplugin.Inventories.NewsInventory;
import grilex.newsplugin.Items.ItemFactory;
import grilex.newsplugin.NewsPlugin;
import grilex.newsplugin.Utils.ChatUtils.ChatUtils;
import grilex.newsplugin.Utils.DatabaseUtils.DatabaseManager;
import grilex.newsplugin.Utils.InventoryUtils.InventoryManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.BookMeta;

import java.sql.SQLException;
import java.util.Objects;

import static org.bukkit.Material.WRITABLE_BOOK;
import static org.bukkit.Material.WRITTEN_BOOK;

public class PlayerInventoryClickEvent implements Listener {
    private InventoryManager inventoryManager;
    private DatabaseManager databaseManager;
    private FileConfiguration menuConfig;
    private int currentPageNews;
    private int currentPageDrafts;
    private boolean deliveryMode;
    private boolean runMethod;
    private NewsCore newsCore = new NewsCore();
    private ItemFactory itemFactory = new ItemFactory();
    private ChatUtils chatUtils = new ChatUtils();

    public PlayerInventoryClickEvent(NewsPlugin plugin) throws SQLException {
        menuConfig = plugin.getMenuConfig().getConfig();
        inventoryManager = plugin.getInventoryManager();
        databaseManager = new NewsCore().getDatabaseManager();
        currentPageNews = new NewsInventory(plugin, newsCore, itemFactory, chatUtils).getCurrentPage();
        currentPageDrafts = new DraftInventory(plugin, newsCore, itemFactory, chatUtils).getCurrentPage();
        runMethod = true;
    }

    @EventHandler
    public void clickEvent(InventoryClickEvent event) throws SQLException {
        Player player = (Player) event.getWhoClicked();
        if (event.getInventory().equals(inventoryManager.getInventories("edit").get(0))) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) {
                return;
            }


            if (event.getClick().isRightClick() &&
                    event.getCurrentItem().getType().toString().equals(menuConfig.getString("edit_inventory.draft.item"))) {
                player.closeInventory();
                player.openInventory(inventoryManager
                        .getInventories("draft").get(currentPageNews));
            }
            if (event.getClick().isLeftClick()) {
                deliveryMode = true;
                menuConfig.getStringList("prompt.add_book_prompt").forEach((x -> {
                    player.sendMessage(x);
                }));
            }

        }

        createNewDrafts(event);

        if (event.getInventory().equals(inventoryManager.getInventories("news").get(currentPageNews))) {
            event.setCancelled(true);
            currentPageNews = inventoryManager.changePage(event, "news", currentPageNews);

            openNews(event, "news", true);
        }
        if (inventoryManager.getInventories("draft") == null) {
            return;
        }
        if (inventoryManager.getInventories("draft").get(currentPageDrafts).equals(event.getInventory())) {
            event.setCancelled(true);
            toggle(event.getClick());
            databaseManager.post(event, currentPageDrafts);
            databaseManager.delete(event, currentPageDrafts);
            currentPageDrafts = inventoryManager.changePage(event, "draft", currentPageDrafts);

            openNews(event, "draft", runMethod);
        }

    }

    @EventHandler
    public void closeInventory(InventoryCloseEvent event) {
        Inventory editInventory = inventoryManager.getInventories("edit").get(0);

        if (event.getInventory().equals(editInventory)) {
            deliveryMode = false;
        }
        if (event.getInventory().equals(inventoryManager.getInventories("news").get(currentPageNews))) {
            currentPageNews = 0;
            runMethod = true;
        }
        if (inventoryManager.getInventories("draft") == null) {
            return;
        }
        if (event.getInventory().equals(inventoryManager.getInventories("draft").get(currentPageDrafts))) {
            currentPageDrafts = 0;
        }
    }

    private void openNews(InventoryClickEvent event, String inventoryType, boolean runMethod) {
        if (event.getCurrentItem() == null) {
            return;
        }
        if (Objects.requireNonNull(event.getCurrentItem()).getType().equals(WRITTEN_BOOK) && runMethod) {
            Player player = (Player) event.getWhoClicked();
            player.closeInventory();
            if (inventoryType.equals("draft")) {
                currentPageDrafts = 0;
            } else {
                currentPageNews = 0;
            }
            player.openBook(event.getCurrentItem());
        }
    }

    private void createNewDrafts(InventoryClickEvent event) {
        if (event.getClick() == ClickType.DROP
                && deliveryMode
                && event.getCurrentItem() != null
                && event.getCurrentItem().getType().equals(WRITABLE_BOOK)) {

            BookMeta bookMeta = (BookMeta) event.getCurrentItem().getItemMeta();
            if (bookMeta != null) {
                StringBuilder bookContent = new StringBuilder();
                for (String page : bookMeta.getPages()) {
                    bookContent.append(page).append("\n");
                }
                String text = bookContent.toString().trim();
                try {
                    databaseManager.createItem(text, "drafts");
                    event.getWhoClicked().sendMessage("Ваш текст был сохранен в черновиках!");
                } catch (SQLException e) {
                    e.printStackTrace();
                    event.getWhoClicked().sendMessage("Произошла ошибка при сохранении текста.");
                }
            }
        }
    }


    public void toggle(ClickType clickType) {
        if (clickType.equals(ClickType.LEFT)) {
            runMethod = true;
        } else runMethod = false;
    }


}
