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

public class NewsInventory {
    private final Map<Integer, Inventory> inventoryHashMap = new HashMap<>();
    private final FileConfiguration menuConfig;
    private int currentPage =0;

   public  NewsInventory(NewsPlugin plugin) throws SQLException {
       menuConfig =  plugin.getMenuConfig().getConfig();
      createNewInventory();
   }

  public void createNewInventory()throws SQLException {
      int menuSize = menuConfig.getInt("news_inventory.menu_line_size")*9;
      String menuName = menuConfig.getString("news_inventory.menu_name");
      NewsCore newsCore = new NewsCore();
      ChatUtils chatUtils = new ChatUtils();
      ItemFactory itemFactory = new ItemFactory();

      int newsCount = newsCore.getDatabaseManager().countItems("clean");
      int inventorySizeMultiplier = 9;
      int fullInventoriesCount =(int) Math.floor((double) newsCount /(menuSize-9));
      int lastInventorySize = (int) (Math.ceil((double) (newsCount % (menuSize - inventorySizeMultiplier)) / inventorySizeMultiplier) + 1) * inventorySizeMultiplier;

      for(int i = 0 ;i<fullInventoriesCount;i++ ){
          if(lastInventorySize<0){
          Inventory inventory = Bukkit.createInventory(null,menuSize,chatUtils.hexColorString(menuName+"("+(i+1)+"/"+fullInventoriesCount+")"));
          for(int j = 0; j< menuSize-9 ;j++){
              int itemId = i * menuSize + j+1;
              ItemStack book = createBook(itemFactory,newsCore,itemId);
              inventory.setItem(j,book);
          }
          inventoryHashMap.put(i,inventory);}
          else {
              Inventory inventory = Bukkit.createInventory(null,menuSize,chatUtils.hexColorString(menuName+"("+(i+1)+"/"+(fullInventoriesCount+1)+")"));
              for(int j = 0; j< menuSize-9 ;j++){
                  int itemId = i * menuSize + j+1;
                  ItemStack book = createBook(itemFactory,newsCore,itemId);
                  inventory.setItem(j,book);
              }
              inventoryHashMap.put(i,inventory);}
      }
      if(lastInventorySize>0){
          Inventory inventory = Bukkit.createInventory(null,lastInventorySize+inventorySizeMultiplier,chatUtils.hexColorString(menuName+"("+(inventoryHashMap.size())+"/"+fullInventoriesCount+")"));
          for(int i =0;i<lastInventorySize-1 &&
                  newsCount!=0 &&
                  (inventory.getSize()-inventorySizeMultiplier) !=0 &&
                  i<newsCount % (inventory.getSize()-inventorySizeMultiplier) ;i++){
              int itemId= fullInventoriesCount*(menuSize-inventorySizeMultiplier)+i+1 ;

              ItemStack book = createBook(itemFactory,newsCore,itemId);
              inventory.setItem(i,book);
          }
      inventoryHashMap.put(inventoryHashMap.size(),inventory);
          setNavigationItems(inventoryHashMap,itemFactory);
  }}
    private ItemStack createBook(ItemFactory itemFactory, NewsCore newsCore, int itemId) throws SQLException {
        return itemFactory.createBook(
                menuConfig.getString("news_inventory.standard_news_name"),
                menuConfig.getString("news_inventory.author"),
               newsCore.getDatabaseManager().getTextAsList(itemId, "clean")
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
    public Map<Integer, Inventory> getNewsInventory() {
        return inventoryHashMap;
    }
}
