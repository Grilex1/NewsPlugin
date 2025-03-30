package grilex.newsplugin.Utils.ConfigUtils;

import java.util.ArrayList;

public class ConfigManager {
    public ArrayList<ConfigUtils> getConfigs() {
    return configs;
}

    private final ArrayList<ConfigUtils> configs = new ArrayList<>();

    public void addConfig(ConfigUtils config){
        configs.add(config);
    }
    public ConfigManager(){}

}
