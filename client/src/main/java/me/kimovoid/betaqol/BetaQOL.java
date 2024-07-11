package me.kimovoid.betaqol;

import me.kimovoid.betaqol.config.Config;
import me.kimovoid.betaqol.feature.keybind.KeybindHandler;
import me.kimovoid.betaqol.feature.keybind.ThoroughKeybindHandler;
import me.kimovoid.betaqol.feature.networking.PlayerInfoHandler;
import me.kimovoid.betaqol.feature.networking.PlayerInfoPayload;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.ornithemc.osl.entrypoints.api.ModInitializer;
import net.ornithemc.osl.networking.api.client.ClientPlayNetworking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;

public class BetaQOL implements ModInitializer {

	public static BetaQOL INSTANCE;
	public static Config CONFIG;
	public static final Logger LOGGER = LoggerFactory.getLogger("BetaQOL");
	public static Minecraft mc;
	public KeybindHandler keybinds;
	public LinkedHashMap<String, Integer> tabPlayers = new LinkedHashMap<>();

	@Override
	public void init() {
		INSTANCE = this;
		LOGGER.info("Hello from Beta QOL! :)");

		CONFIG = new Config();
		CONFIG.init();

		this.keybinds = new KeybindHandler();
		if (FabricLoader.getInstance().isModLoaded("thorough-keybindings")) {
			this.keybinds = new ThoroughKeybindHandler();
			LOGGER.info("Found thorough keybindings mod!");
		}

		/* Networking */
		ClientPlayNetworking.registerListener("BetaQOL|PlayerInfo", PlayerInfoPayload::new, new PlayerInfoHandler());
	}
}