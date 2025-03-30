package grilex.newsplugin.Utils.ChatUtils;

import net.md_5.bungee.api.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ChatUtils {
    private static final Pattern HEX_PATTERN = Pattern.compile("#([A-Fa-f0-9]){6}");
    private ArrayList<String> list;
    public List<?> hexColorList(ArrayList<?> list)
    {
        if (list== null || list.isEmpty()) {
            return list;
        }
        this.list = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            String line = (String) list.get(i);
            String coloredLine = hexColorString(line);
            this.list.add(coloredLine);
        }
        return this.list;
    }
    public  String hexColorString(String message)
    {
        if (message == null || message.isEmpty()) {
            return message;
        }
        return ChatColor.translateAlternateColorCodes('&', HEX_PATTERN.matcher(message).replaceAll("&x&$1&$2&$3&$4&$5&$6"));
    }
}
