package grilex.newsplugin.Commands.subcomands;

import grilex.newsplugin.Utils.CommandUtils.SubCommand;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class help extends SubCommand {
    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getSyntax() {
        return "/news help";
    }

    @Override
    public void perform(Player player, String[] args) throws SQLException {

    }
}
