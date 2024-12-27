package me.kimovoid.betaqol;

import me.kimovoid.betaqol.command.*;
import me.kimovoid.betaqol.interfaces.IServerPlayerEntity;
import me.kimovoid.betaqol.mixin.access.PacketInvoker;
import me.kimovoid.betaqol.networking.PlayerInfoPayload;
import me.kimovoid.betaqol.networking.ServerPingPacket;
import me.kimovoid.betaqol.rcon.RconServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.ornithemc.osl.entrypoints.api.server.ServerModInitializer;
import net.ornithemc.osl.lifecycle.api.MinecraftEvents;
import net.ornithemc.osl.networking.api.server.ServerPlayNetworking;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BetaQOL implements ServerModInitializer {

	public static BetaQOL INSTANCE;
	public static MinecraftServer SERVER;

	public static final Map<String, ICommand> commandsByName = new HashMap<>();
	public static List<String> opCommands = new ArrayList<>();

	public RconServer rcon;
	public List<String> instantBreak = new ArrayList<>();

	@Override
	public void initServer() {
		INSTANCE = this;
		this.fixLogger();
		registerCommands();

		/* Server init */
		MinecraftEvents.PREPARE_WORLD.register(server -> {
			SERVER = server;
			PacketInvoker.register(254, false, true, ServerPingPacket.class);
			if (server.properties.getBoolean("enable-rcon", false)) {
				server.sendMessage("Starting remote control listener");
				BetaQOL.INSTANCE.rcon = new RconServer(server);
				BetaQOL.INSTANCE.rcon.start();
			}
		});
	}

	public static void sendPlayerInfo(String username, boolean online, int ping) {
		ServerPlayNetworking.send("BetaQOL|PlayerInfo", new PlayerInfoPayload(username, online, ping));
	}

	public static void sendPlayerInfo(ServerPlayerEntity to) {
		for (ServerPlayerEntity on : SERVER.playerManager.players) {
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

	public static void registerCommands() {
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

	public static void registerCommand(ICommand command) {
		commandsByName.put(command.getName(), command);
		if (command.requiresOp()) opCommands.add(command.getName());
	}
}