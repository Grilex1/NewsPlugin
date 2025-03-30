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
import java.util.HashMap;
import java.util.Map;

public class DraftInventory {
    private final Map<Integer, Inventory> inventoryHashMap = new HashMap<>();
    private final FileConfiguration menuConfig;
    private int currentPage =0;

    public  DraftInventory(NewsPlugin plugin) throws SQLException {
        menuConfig =  plugin.getMenuConfig().getConfig();
        createNewInventory();
    }

    public void createNewInventory()throws SQLException {
        int menuSize = menuConfig.getInt("draft_inventory.menu_line_size")*9;
        String menuName = menuConfig.getString("draft_inventory.menu_name");
        NewsCore newsCore = new NewsCore();
        ChatUtils chatUtils = new ChatUtils();
        ItemFactory itemFactory = new ItemFactory();

        int draftCount = newsCore.getDatabaseManager().countItems("drafts");
        int fullInventoriesCount =(int) Math.floor((double) draftCount /(menuSize-9));
        int inventorySizeMultiplier = 9;
        int lastInventorySize = (int) (Math.ceil((double) (draftCount % (menuSize - inventorySizeMultiplier)) / inventorySizeMultiplier) ) * inventorySizeMultiplier;


        if(lastInventorySize!= 0 && lastInventorySize <=45){
            lastInventorySize+=inventorySizeMultiplier;
        }

        for(int i = 0 ;i<fullInventoriesCount;i++ ){
            if(lastInventorySize<0){Inventory inventory = Bukkit.createInventory(null,menuSize,chatUtils.hexColorString(menuName+"("+(i+1)+"/"+fullInventoriesCount+")"));
            for(int j = 0; j< menuSize-9 ;j++){
                int itemId = i * menuSize + j+1;
                ItemStack book = createBook(itemFactory,newsCore,itemId);
                itemFactory.addInfo(book,menuConfig.getStringList("draft_inventory.lore"));
                inventory.setItem(j,book);
            }
            inventoryHashMap.put(i,inventory);}
            else {Inventory inventory = Bukkit.createInventory(null,menuSize,chatUtils.hexColorString(menuName+"("+(i+1)+"/"+(fullInventoriesCount+1)+")"));
                for(int j = 0; j< menuSize-9 ;j++){
                    int itemId = i * menuSize + j+1;
                    ItemStack book = createBook(itemFactory,newsCore,itemId);
                    itemFactory.addInfo(book,menuConfig.getStringList("draft_inventory.lore"));
                    inventory.setItem(j,book);
                }
                inventoryHashMap.put(i,inventory);}
        }

       if(lastInventorySize >0){ Inventory inventory = Bukkit.createInventory(null,lastInventorySize,chatUtils.hexColorString(menuName+"("+(inventoryHashMap.size()+1)+"/"+(fullInventoriesCount+1)+")"));

        for(int i =0;i<lastInventorySize-1 &&
            draftCount!=0 &&
            (inventory.getSize()-inventorySizeMultiplier) !=0 &&
            i<draftCount % (inventory.getSize()-inventorySizeMultiplier) ;i++){
            int itemId= fullInventoriesCount*(menuSize-inventorySizeMultiplier)+i+1 ;
            ItemStack book = createBook(itemFactory,newsCore,itemId);
            itemFactory.addInfo(book,menuConfig.getStringList("draft_inventory.lore"));
            inventory.setItem(i,book);
        }

        inventoryHashMap.put(inventoryHashMap.size(),inventory);
        setNavigationItems(inventoryHashMap,itemFactory); }
    }

    private ItemStack createBook(ItemFactory itemFactory, NewsCore newsCore, int itemId) throws SQLException {
        return itemFactory.createBook(
                menuConfig.getString("draft_inventory.standard_news_name"),
                menuConfig.getString("draft_inventory.author"),
                newsCore.getDatabaseManager().getTextAsList(itemId, "drafts")
        );
    }

    public void setNavigationItems(Map<Integer, Inventory> hashMap , ItemFactory itemFactory){
        ChatUtils chatUtils = new ChatUtils();
        for(int i =0;i <hashMap.size();i++){
            if(i==0 && hashMap.size() !=1){
                hashMap.get(i).setItem(( hashMap.get(i).getSize()-1), itemFactory.createPlayerSkull(
                        menuConfig.getString("special_items.forward.player"),
                        chatUtils.hexColorString(menuConfig.getString("special_items.forward.name")),
                        menuConfig.getInt("special_items.forward.nbt")));
            }
           else if(i==hashMap.size()-1 && hashMap.size() !=1 ){
                hashMap.get(i).setItem(( hashMap.get(i).getSize()-9), itemFactory.createPlayerSkull(
                        menuConfig.getString("special_items.back.player"),
                        chatUtils.hexColorString(menuConfig.getString("special_items.back.name")),
                        menuConfig.getInt("special_items.back.nbt")));
            }
           else if(i!=hashMap.size()-1)  {
                hashMap.get(i).setItem(( hashMap.get(i).getSize()-1), itemFactory.createPlayerSkull(
                        menuConfig.getString("special_items.forward.player"),
                        chatUtils.hexColorString(menuConfig.getString("special_items.forward.name")),
                        menuConfig.getInt("special_items.forward.nbt")));
                hashMap.get(i).setItem(( hashMap.get(i).getSize()-9), itemFactory.createPlayerSkull(
                        menuConfig.getString("special_items.back.player"),
                        chatUtils.hexColorString(menuConfig.getString("special_items.back.name")),
                        menuConfig.getInt("special_items.back.nbt")));
            }
        }
    }

    public int getCurrentPage() {
        return currentPage;
    }
    public Map<Integer, Inventory> getDraftInventory() {
        return inventoryHashMap;
    }
}
