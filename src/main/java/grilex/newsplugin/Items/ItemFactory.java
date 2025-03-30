package grilex.newsplugin.Items;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;


import java.util.List;

public class ItemFactory {

    public ItemStack createBook(String title,
                                String author,
                                List<String> pages
                               ) {
        Material bookType =  Material.WRITTEN_BOOK ;
        ItemStack book = new ItemStack(bookType);
            BookMeta bookMeta = (BookMeta) book.getItemMeta();
        //    LocalDate today = LocalDate.now();
        //    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy");
       //     String formattedDate = today.format(formatter);
            bookMeta.setTitle(title);
            bookMeta.setAuthor(author);
            bookMeta.setPages(pages);
            book.setItemMeta(bookMeta);

        return book;
    }
    public ItemStack createPlayerSkull(String playerName, String displayName,Object symbol) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();

        if (skullMeta != null) {
            skullMeta.setDisplayName(displayName);
            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(playerName));
            addNBT(skull,skullMeta,"special",PersistentDataType.INTEGER,symbol);
            skull.setItemMeta(skullMeta);
        }
        return skull;
    }
    public void addNBT(ItemStack itemStack, ItemMeta itemMeta,String key, PersistentDataType type,Object symbol){
        itemMeta.getPersistentDataContainer().set(NamespacedKey.fromString(key), type, symbol);
        itemStack.setItemMeta(itemMeta);
    }
    public void addInfo(ItemStack itemStack,List<String> info){
        ItemMeta meta = itemStack.getItemMeta();
        meta.setLore(info);
        itemStack.setItemMeta(meta);
    }
}
