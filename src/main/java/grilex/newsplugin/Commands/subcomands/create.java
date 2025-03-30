package grilex.newsplugin.Commands.subcomands;

import grilex.newsplugin.NewsPlugin;
import grilex.newsplugin.Utils.CommandUtils.SubCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;


public class create extends SubCommand {
    FileConfiguration config = NewsPlugin.getInstance().getConfig();
    String create = config.getString("permission.news_create");
    @Override
    public String getName() {
        return "create";
    }

    @Override
    public String getDescription() {
        return "if you have permission, you can create a news";
    }

    @Override
    public String getSyntax() {
        return "/news create";
    }

    @Override
    public void perform(Player player, String[] args)  {
        if (player.hasPermission(create)) {
            player.openInventory(NewsPlugin.getInstance().getInventoryManager().getInventory("edit"));
        }else {
            player.sendMessage("У вас нет разрешения на создание новостей.");
        }
    }
}
