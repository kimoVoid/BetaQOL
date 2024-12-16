package me.kimovoid.betaqol;

import me.kimovoid.betaqol.config.Config;
import me.kimovoid.betaqol.feature.chunkborders.ChunkBorderRenderer;
import me.kimovoid.betaqol.feature.debugscreen.DebugScreen;
import me.kimovoid.betaqol.feature.keybinding.KeybindHandler;
import me.kimovoid.betaqol.feature.keybinding.ThoroughKeybindHandler;
import me.kimovoid.betaqol.feature.networking.PlayerInfoListener;
import me.kimovoid.betaqol.feature.networking.PlayerInfoPayload;
import net.fabricmc.loader.api.FabricLoader;
import net.ornithemc.osl.entrypoints.api.client.ClientModInitializer;
import net.ornithemc.osl.lifecycle.api.MinecraftEvents;
import net.ornithemc.osl.networking.api.client.ClientPlayNetworking;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

import java.util.LinkedHashMap;

public class BetaQOL implements ClientModInitializer {

	public static BetaQOL INSTANCE;
	public static Config CONFIG;
	public static final Logger LOGGER = LogManager.getLogger("BetaQOL");

	public KeybindHandler keybinds;
	public LinkedHashMap<String, Integer> tabPlayers = new LinkedHashMap<>();
	public final ChunkBorderRenderer chunkBorderRenderer = new ChunkBorderRenderer();

	@Override
	public void initClient() {
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

		DebugScreen.INSTANCE.init();

		/* Networking */
		ClientPlayNetworking.registerListener("BetaQOL|PlayerInfo", PlayerInfoPayload::new, new PlayerInfoListener());

		/* Events */
		MinecraftEvents.READY_WORLD.register(mc -> this.tabPlayers.clear());
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

	public String getMcVersion() {
		return FabricLoader
				.getInstance()
				.getModContainer("minecraft")
				.get()
				.getMetadata()
				.getVersion()
				.getFriendlyString()
				.replace("1.0.0-beta", "1");
	}
}