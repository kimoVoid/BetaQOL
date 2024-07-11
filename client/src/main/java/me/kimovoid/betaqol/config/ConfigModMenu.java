package me.kimovoid.betaqol.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import io.github.axolotlclient.AxolotlClientConfig.api.AxolotlClientConfig;
import io.github.axolotlclient.AxolotlClientConfig.api.manager.ConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.api.ui.ConfigUI;

public class ConfigModMenu implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return screen -> {
            ConfigManager manager = AxolotlClientConfig.getInstance().getConfigManager("betaqol");
            return ConfigUI.getInstance().getScreen(this.getClass().getClassLoader(),
                    manager.getRoot(), screen);
        };
    }
}