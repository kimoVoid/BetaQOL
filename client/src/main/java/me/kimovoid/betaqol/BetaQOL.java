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
import org.lwjgl.opengl.Display;

import java.util.LinkedHashMap;
import java.util.logging.Logger;

public class BetaQOL implements ModInitializer {

	public static BetaQOL INSTANCE;
	public static Config CONFIG;
	public static Minecraft mc;
	public KeybindHandler keybinds;
	public LinkedHashMap<String, Integer> tabPlayers = new LinkedHashMap<>();
	public final Logger logger = Logger.getLogger("BetaQOL");

	@Override
	public void init() {
		INSTANCE = this;
		System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s %n");
		this.log("Hello from Beta QOL! :)");

		CONFIG = new Config();
		CONFIG.init();

		this.keybinds = new KeybindHandler();
		if (FabricLoader.getInstance().isModLoaded("thorough-keybindings")) {
			this.keybinds = new ThoroughKeybindHandler();
			this.log("Found thorough keybindings mod!");
		}

		/* Networking */
		ClientPlayNetworking.registerListener("BetaQOL|PlayerInfo", PlayerInfoPayload::new, new PlayerInfoHandler());
	}

	public void mcInit() {
		Display.setTitle("Minecraft Beta 1.7.3 (QOL)");
	}

	public void log(String log) {
		this.logger.info("(BetaQOL) " + log);
	}

	public void warn(String log) {
		this.logger.warning("(BetaQOL) " + log);
	}
}