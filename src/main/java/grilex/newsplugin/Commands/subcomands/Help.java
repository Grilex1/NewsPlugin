package grilex.newsplugin.Commands.subcomands;

import grilex.newsplugin.NewsPlugin;
import grilex.newsplugin.Utils.ChatUtils.ChatUtils;
import grilex.newsplugin.Utils.CommandUtils.CommandManger;
import grilex.newsplugin.Utils.CommandUtils.SubCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class Help extends SubCommand {
    FileConfiguration config = NewsPlugin.getInstance().getConfig();
    String help = config.getString("permission.news_help");
    ChatUtils chatUtils = new ChatUtils();
    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "показывает список команд и их описание";
    }

    @Override
    public String getSyntax() {
        return "/news help";
    }

    @Override
    public void perform(Player player, String[] args) throws SQLException {
        if (player.hasPermission(help) || player.isOp()) {
        CommandManger commandManager = new CommandManger();
        player.sendMessage("============");
        for (int i = 0; i < commandManager.getSubCommand().size(); i++) {
            String syntax = new ChatUtils().hexColorString(commandManager.getSubCommand().get(i).getSyntax());
            String description = new ChatUtils().hexColorString(commandManager.getSubCommand().get(i).getDescription());
            player.sendMessage(syntax + " - " + description);
        }
        player.sendMessage("============");
    }else {
            player.sendMessage(chatUtils.hexColorString(config.getString("exception.permission_exception")));
        }}

}
