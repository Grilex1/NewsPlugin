package grilex.newsplugin.Commands.subcomands;

import grilex.newsplugin.NewsPlugin;
import grilex.newsplugin.Utils.ChatUtils.ChatUtils;
import grilex.newsplugin.Utils.CommandUtils.SubCommand;
import grilex.newsplugin.Utils.ConfigUtils.ConfigUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class Reload extends SubCommand {

    FileConfiguration config = NewsPlugin.getInstance().getConfig();
    String reload = config.getString("permission.news_reload");
    ChatUtils chatUtils = new ChatUtils();
    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "перезагрузка плагина";
    }

    @Override
    public String getSyntax() {
        return "/news reload";
    }

    @Override
    public void perform(Player player, String[] args) throws SQLException {
        if (player.hasPermission(reload) || player.isOp()) {
            NewsPlugin.getInstance().saveConfig();
            ConfigUtils.of(NewsPlugin.getInstance(),"menu.yml");
            player.sendMessage(chatUtils.hexColorString(config.getString("prefix"))+chatUtils.hexColorString(config.getString("reload_message")));
        }
        else {
            player.sendMessage(chatUtils.hexColorString(config.getString("exception.permission_exception")));
        }
    }
}
