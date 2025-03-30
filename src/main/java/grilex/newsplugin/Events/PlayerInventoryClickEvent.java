package grilex.newsplugin.Events;

import grilex.newsplugin.Core.NewsCore;
import grilex.newsplugin.Inventories.DraftInventory;
import grilex.newsplugin.Inventories.NewsInventory;
import grilex.newsplugin.NewsPlugin;
import grilex.newsplugin.Utils.DatabaseUtils.DatabaseManager;
import grilex.newsplugin.Utils.InventoryUtils.InventoryManager;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.persistence.PersistentDataType;
import java.sql.SQLException;
import java.util.Objects;

import static org.bukkit.Material.*;

public class PlayerInventoryClickEvent implements Listener {
    private InventoryManager inventoryManager;
    private DatabaseManager databaseManager;
    private FileConfiguration menuConfig;
    private int currentPageNews;
    private int currentPageDrafts;
    private boolean deliveryMode;
    private boolean runMethod;
    public PlayerInventoryClickEvent(NewsPlugin plugin) throws SQLException {
        menuConfig = plugin.getMenuConfig().getConfig();
        inventoryManager = plugin.getInventoryManager();
        databaseManager = new NewsCore().getDatabaseManager();
        currentPageNews = new NewsInventory(plugin).getCurrentPage();
        currentPageDrafts = new DraftInventory(plugin).getCurrentPage();
        runMethod = true;
    }

    @EventHandler
    public void clickEvent(InventoryClickEvent event) throws SQLException {
        if (event.getInventory().equals(inventoryManager.getInventoryMap("news").get(currentPageNews))) {
            event.setCancelled(true);
            handleInventoryNavigation(event, "news", currentPageNews);
            openNews(event, "news",true);
        }
        if (event.getInventory().equals(inventoryManager.getInventoryMap("draft").get(currentPageDrafts))) {
           event.setCancelled(true);
           toggle(event.getClick());
           post(event
           );
           delete(event);
           handleInventoryNavigation(event, "draft", currentPageDrafts);
           openNews(event, "draft",runMethod);
        }
        if (event.getInventory().equals(inventoryManager.getInventory("edit"))) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) {
                return;
            }
            Player player = (Player) event.getWhoClicked();
            if (event.getClick().isRightClick() &&
                    event.getCurrentItem().getType().toString().equals(menuConfig.getString("edit_inventory.draft.item"))) {
                player.closeInventory();
                player.openInventory(inventoryManager
                        .getInventoryMap("draft").get(currentPageNews));
            }
            if (event.getClick().isLeftClick()) {
                deliveryMode = true;
                menuConfig.getStringList("prompt.add_book_prompt").forEach((x -> {
                    player.sendMessage(x);
                }));
            }

        }
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

    @EventHandler
    public void closeInventory(InventoryCloseEvent event) {
        Inventory editInventory = inventoryManager.getInventory("edit");
        if (event.getInventory().equals(editInventory)) {
            deliveryMode = false;
        }
        if (event.getInventory().equals(inventoryManager.getInventoryMap("news").get(currentPageNews))) {
            currentPageNews = 0;
            runMethod = true;
        }
        if (event.getInventory().equals(inventoryManager.getInventoryMap("draft").get(currentPageDrafts))) {
            currentPageDrafts = 0;
        }
    }
    private void openNews(InventoryClickEvent event, String inventoryType, boolean runMethod){
        if (event.getCurrentItem() == null) {
            return; 
        }
        if (Objects.requireNonNull(event.getCurrentItem()).getType().equals(WRITTEN_BOOK)&&runMethod) {
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
    private void handleInventoryNavigation(InventoryClickEvent event, String inventoryType, int currentPage) {
       Player player = (Player) event.getWhoClicked();
        if (event.getCurrentItem() == null) {
            return;
        }
        if (Objects.equals(Objects.requireNonNull(event.
                        getCurrentItem().
                        getItemMeta()).
                getPersistentDataContainer().
                get(Objects.requireNonNull(NamespacedKey.fromString("special")), PersistentDataType.INTEGER), -1)) {
            currentPage -= 1;
            if (inventoryType.equals("draft")) {
                currentPageDrafts--;
            }
            else if(inventoryType.equals("news")) {
                currentPageNews--;
            }
            player.closeInventory();
            player.openInventory(inventoryManager.getInventoryMap(inventoryType).get(currentPage));
        }


        if (Objects.equals(event.
                getCurrentItem().
                getItemMeta().
                getPersistentDataContainer().
                get(Objects.requireNonNull(NamespacedKey.fromString("special")), PersistentDataType.INTEGER), 1)) {
            currentPage += 1;

            if (inventoryType.equals("draft")) {
                currentPageDrafts++;
            }
            else if(inventoryType.equals("news")){
                currentPageNews++;
            }
            player.closeInventory();
            player.openInventory(inventoryManager.getInventoryMap(inventoryType).get(currentPage));
        }


    }
    public void toggle(ClickType clickType){
        if(clickType.equals(ClickType.LEFT)){ runMethod = true;}
        else runMethod = false;
    }

   public void delete(InventoryClickEvent event) throws SQLException {
       if (event.getCurrentItem() == null) {
           return;
       }
       if(event.getClick() == ClickType.SHIFT_RIGHT&&
           event.getCurrentItem().getType() == WRITTEN_BOOK){
       runMethod = false;
       int itemId = currentPageDrafts * 45 + event.getSlot() + 1;
       try{
           databaseManager.deleteItem(itemId,"drafts");
       } catch (SQLException e) {
           throw new SQLException(e);
       }
       event.getInventory().setItem(event.getSlot(), null);
   }}
    public void post(InventoryClickEvent event) throws SQLException { if (event.getCurrentItem() == null) {
        return;
    }
        if (event.getClick() == ClickType.SHIFT_LEFT &&
                event.getCurrentItem().getType() == WRITTEN_BOOK) {
            runMethod =false;
            int itemId = currentPageDrafts * 45 + event.getSlot() + 1;
            try{
                databaseManager.createItem(
                        String.valueOf(databaseManager.
                                getTextAsList(itemId,"drafts")),"clean");
                databaseManager.deleteItem(itemId,"drafts");
            } catch (SQLException e) {
                throw new SQLException(e);
            }
            event.getInventory().setItem(event.getSlot(), null);
            event.getWhoClicked().closeInventory();

        }}
   }
