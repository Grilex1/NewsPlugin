package grilex.newsplugin.Commands;

import grilex.newsplugin.NewsPlugin;
import grilex.newsplugin.Utils.CommandUtils.AbstractCommand;
import grilex.newsplugin.Utils.CommandUtils.CommandManger;
import grilex.newsplugin.Utils.CommandUtils.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NewsCommand extends AbstractCommand {
    FileConfiguration config = NewsPlugin.getInstance().getConfig();
    String bypass = config.getString("permission.bypass");
    String news = config.getString("permission.news");
    String create = config.getString("permission.news_create");
    CommandManger commandManager = new CommandManger();

    public NewsCommand() {
        super("news");
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (args.length != 0) {
            for (int i = 0; i < commandManager.getSubCommand().size(); i++) {
                if (args[0].equalsIgnoreCase(commandManager.getSubCommand().get(i).getName())) {
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        try {
                            commandManager.getSubCommand().get(i).perform(player, args);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        } else {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.hasPermission(news) || player.hasPermission(bypass)) {
                    Inventory newsInventory = NewsPlugin.getInstance().getInventoryManager().getInventories("news").get(0);
                    player.openInventory(newsInventory);
                }
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            return List.of();
        }

        Player player = (Player) commandSender;

        if (strings.length > 1) {
            return List.of();
        }

        if (player.hasPermission(create) || player.hasPermission(bypass)) {
            List<String> tabCompleter = new ArrayList<>();
            for (SubCommand subCommand : commandManager.getSubCommand()) {
                tabCompleter.add(subCommand.getName());
            }
            return tabCompleter;
        }
        return List.of();
    }
}
