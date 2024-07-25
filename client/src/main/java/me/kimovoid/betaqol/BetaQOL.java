package me.kimovoid.betaqol;

import me.kimovoid.betaqol.config.Config;
import me.kimovoid.betaqol.feature.chunkborders.ChunkBorderRenderer;
import me.kimovoid.betaqol.feature.keybinding.KeybindHandler;
import me.kimovoid.betaqol.feature.keybinding.ThoroughKeybindHandler;
import me.kimovoid.betaqol.feature.networking.PlayerInfoListener;
import me.kimovoid.betaqol.feature.networking.PlayerInfoPayload;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.ornithemc.osl.entrypoints.api.ModInitializer;
import net.ornithemc.osl.networking.api.client.ClientPlayNetworking;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

import java.util.LinkedHashMap;

public class BetaQOL implements ModInitializer {

	public static BetaQOL INSTANCE;
	public static Config CONFIG;
	public static final Logger LOGGER = LogManager.getLogger("BetaQOL");

	public KeybindHandler keybinds;
	public LinkedHashMap<String, Integer> tabPlayers = new LinkedHashMap<>();
	public final ChunkBorderRenderer chunkBorderRenderer = new ChunkBorderRenderer();

	@Override
	public void init() {
		this.fixLogger();
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
		ClientPlayNetworking.registerListener("BetaQOL|PlayerInfo", PlayerInfoPayload::new, new PlayerInfoListener());
	}

	private void fixLogger() {
		LoggerContext context = (LoggerContext) LogManager.getContext(false);
		Configuration conf = context.getConfiguration();
		LoggerConfig loggerConfig = conf.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
		loggerConfig.setLevel(Level.INFO);
		context.updateLoggers();
	}

	public ChunkBorderRenderer getChunkBorderRenderer() {
		return this.chunkBorderRenderer;
	}
}