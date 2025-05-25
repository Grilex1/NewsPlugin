package grilex.newsplugin.Utils.CommandUtils;

import grilex.newsplugin.Commands.subcomands.Create;
import grilex.newsplugin.Commands.subcomands.Help;
import grilex.newsplugin.Commands.subcomands.Reload;

import java.util.ArrayList;

public class CommandManger {
    public ArrayList<SubCommand> getSubCommand() {
        return subCommands;
    }

    private final ArrayList<SubCommand> subCommands = new ArrayList<>();

    public CommandManger() {
        subCommands.add(new Create());
        subCommands.add(new Help());
        subCommands.add(new Reload());
    }
}
