package grilex.newsplugin.Utils.CommandUtils;

import grilex.newsplugin.NewsPlugin;
import org.bukkit.command.*;

public abstract class AbstractCommand  implements CommandExecutor, TabCompleter {

    public AbstractCommand(String command){
        PluginCommand pluginCommand = NewsPlugin.getInstance().getCommand(command);
        if (pluginCommand != null) {
            pluginCommand.setExecutor(this);
            pluginCommand.setTabCompleter(this);
        }

    }
    public abstract void  execute(CommandSender sender,String label, String[] args);
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        execute(sender,label,args);
        return true;
    }
}
