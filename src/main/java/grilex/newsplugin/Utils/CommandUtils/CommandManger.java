package grilex.newsplugin.Utils.CommandUtils;

import grilex.newsplugin.Commands.subcomands.create;


import java.util.ArrayList;

public class CommandManger {
    public ArrayList<SubCommand> getSubCommand()
    {
        return  subCommands;
    }
    private final ArrayList<SubCommand> subCommands = new ArrayList<>();
  public CommandManger(){
      subCommands.add(new create());
      
  }
}
