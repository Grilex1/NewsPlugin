package grilex.newsplugin.Inventories;

import grilex.newsplugin.NewsPlugin;
import grilex.newsplugin.Utils.ChatUtils.ChatUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.bukkit.Bukkit.createInventory;

public class EditInventory {
    private Inventory editInventory;
    private ItemStack draft;

    public  EditInventory(NewsPlugin plugin){
        FileConfiguration menuConfig =  plugin.getMenuConfig().getConfig();
        FileConfiguration config = plugin.getConfig();
        ChatUtils chatUtils = new ChatUtils();
        draft = new ItemStack(Material.valueOf(Objects.requireNonNull(menuConfig.getString( "edit_inventory.draft.item")).toUpperCase()));
        ItemMeta draftMeta = draft.getItemMeta();
        draftMeta.setDisplayName(chatUtils.hexColorString(menuConfig.getString("edit_inventory.draft.name")));
        List<?> lore = (chatUtils.hexColorList((ArrayList<?>) menuConfig.getList("edit_inventory.draft.lore")));
        draftMeta.setLore((List<String>) lore);
        draft.setItemMeta(draftMeta);
        int menuSize = menuConfig.getInt("edit_inventory.menu_line_size")*9;

        if( menuSize < 9 || menuSize > 54 ){
            plugin.getLogger().info(chatUtils.hexColorString(config.getString("prefix")+menuConfig.getString("exception.size_exception")));
        }
        String menuName = menuConfig.getString("edit_inventory.menu_name");
        if(menuName == null){
            plugin.getLogger().info(chatUtils.hexColorString(config.getString("prefix")+menuConfig.getString("exception.name_exception")));
        }
        if(menuName != null || menuSize < 9 || menuSize > 54 ){editInventory = createInventory(null,menuSize, menuName);}
    editInventory.setItem(menuConfig.getInt("edit_inventory.draft.menu_location"), draft);

    }

    public Inventory getEditInventory() {
        return editInventory;
    }
}
