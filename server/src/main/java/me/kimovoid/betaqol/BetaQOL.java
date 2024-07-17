package me.kimovoid.betaqol;

import me.kimovoid.betaqol.command.*;
import me.kimovoid.betaqol.interfaces.IServerPlayerEntity;
import me.kimovoid.betaqol.networking.PlayerInfoPayload;
import me.kimovoid.betaqol.rcon.RconServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.ornithemc.osl.entrypoints.api.ModInitializer;
import net.ornithemc.osl.networking.api.server.ServerPlayNetworking;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BetaQOL implements ModInitializer {

	public static BetaQOL INSTANCE;
	public static final Logger LOGGER = LogManager.getLogger("BetaQOL");
	public static MinecraftServer server;
	public RconServer rcon;
	public String mcVersion = "UNKNOWN";

	public final Map<String, ICommand> commandsByName = new HashMap<>();
	public List<String> opCommands = new ArrayList<>();
	public List<String> instantBreak = new ArrayList<>();

	@Override
	public void init() {
		INSTANCE = this;
		this.fixLogger();
		LOGGER.info("Hello from BetaQOL! :)");
		this.registerCommands();
	}

	public static void sendPlayerInfo(String username, boolean online, int ping) {
		ServerPlayNetworking.send("BetaQOL|PlayerInfo", new PlayerInfoPayload(username, online, ping));
	}

	public static void sendPlayerInfo(ServerPlayerEntity to) {
		for (ServerPlayerEntity on : server.playerManager.players) {
			ServerPlayNetworking.send(to, "BetaQOL|PlayerInfo", new PlayerInfoPayload(
					on.name,
					true,
					((IServerPlayerEntity)on).getPing()));
		}
	}

	private void fixLogger() {
		LoggerContext context = (LoggerContext) LogManager.getContext(false);
		Configuration conf = context.getConfiguration();
		LoggerConfig loggerConfig = conf.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
		loggerConfig.setLevel(Level.INFO);
		context.updateLoggers();
	}

	public void registerCommands() {
		registerCommand(new TellrawCommand());
		registerCommand(new StatusCommand());
		registerCommand(new ToggledownfallCommand());
		registerCommand(new DebugCommand());
		registerCommand(new ClearCommand());
		registerCommand(new GiveCommand());
		registerCommand(new SetBlockCommand());
		registerCommand(new IbCommand());
		registerCommand(new SummonCommand());
		registerCommand(new PingCommand());
	}

	public void registerCommand(ICommand command) {
		this.commandsByName.put(command.getName(), command);
		if (command.requiresOp()) this.opCommands.add(command.getName());
	}
}