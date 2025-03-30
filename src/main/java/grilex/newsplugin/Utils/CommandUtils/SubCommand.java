package grilex.newsplugin.Utils.CommandUtils;

import org.bukkit.entity.Player;

import java.sql.SQLException;

public abstract class SubCommand {
    public abstract String getName();
    public abstract String getDescription();
    public  abstract String getSyntax();
public abstract void perform(Player player,String args[]) throws SQLException;
}
