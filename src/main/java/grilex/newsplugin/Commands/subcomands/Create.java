package grilex.newsplugin.Commands.subcomands;

import grilex.newsplugin.NewsPlugin;
import grilex.newsplugin.Utils.ChatUtils.ChatUtils;
import grilex.newsplugin.Utils.CommandUtils.SubCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;


public class Create extends SubCommand {
    FileConfiguration config = NewsPlugin.getInstance().getConfig();
    String create = config.getString("permission.news_create");
    ChatUtils chatUtils = new ChatUtils();
    @Override
    public String getName() {
        return "create";
    }

    @Override
    public String getDescription() {
        return "если у вас есть разрешение, вы можете создать новость";
    }

    @Override
    public String getSyntax() {
        return "/news create";
    }

    @Override
    public void perform(Player player, String[] args) {
        if (player.hasPermission(create) || player.isOp()) {
            player.openInventory(NewsPlugin.getInstance().getInventoryManager().getInventories("edit").get(0));
        } else {
            player.sendMessage(chatUtils.hexColorString(config.getString("exception.permission_exception")));
        }
    }
}
