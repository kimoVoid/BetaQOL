package me.kimovoid.betaqol.mixin;

import me.kimovoid.betaqol.BetaQOL;
import me.kimovoid.betaqol.interfaces.IServerPlayNetworkHandler;
import me.kimovoid.betaqol.interfaces.IServerPlayerEntity;
import net.minecraft.command.source.CommandSource;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketHandler;
import net.minecraft.network.packet.KeepAlivePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.server.network.handler.ServerPlayNetworkHandler;
import net.ornithemc.osl.networking.api.server.ServerPlayNetworking;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.logging.LogManager;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin implements CommandSource, IServerPlayNetworkHandler {

	@Shadow private ServerPlayerEntity player;
	@Shadow private MinecraftServer server;
	@Shadow public Connection connection;
	@Unique private int currentTick;
	@Unique private long keepAliveTimeSent;
	@Unique private boolean receivedKeepAlive = true;
	@Unique private int lastPlayerInfoPacket;
	@Unique private boolean initialPlayerInfo = false;

	@Redirect(
			method = "<init>",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/network/Connection;setListener(Lnet/minecraft/network/PacketHandler;)V"
			)
	)
	private void redirect(Connection connection, PacketHandler handler) {
		if (connection != null) connection.setListener(handler);
	}

	@Inject(method = "runCommand", at = @At("HEAD"), cancellable = true)
	private void onCommand(String cmd, CallbackInfo ci) {
		String commandName = cmd.toLowerCase().substring(1).split(" ", 2)[0];
		if (BetaQOL.commandsByName.containsKey(commandName)
				&& !BetaQOL.opCommands.contains(commandName)
				&& !this.server.playerManager.isOp(this.player.name)) {
			server.addCommand(cmd.substring(1), this);
			LogManager.getLogManager().getLogger("Minecraft").info(this.player.name + " issued server command: " + cmd.substring(1));
			ci.cancel();
		}
	}

	@Inject(method = "tick", at = @At("HEAD"))
	public void injectTick(CallbackInfo ci) {
		this.currentTick++;
		this.lastPlayerInfoPacket++;

		if (ServerPlayNetworking.canSend(this.player, "BetaQOL|PlayerInfo") && !this.initialPlayerInfo) {
			BetaQOL.sendPlayerInfo(this.player);
			this.initialPlayerInfo = true;
		}

		if (this.lastPlayerInfoPacket >= 200) {
			BetaQOL.sendPlayerInfo(this.player.name, true, ((IServerPlayerEntity)this.player).getPing());
			this.lastPlayerInfoPacket = 0;
		}

		if (this.currentTick >= 20) {
			this.currentTick = 0;
			if (!this.receivedKeepAlive) {
				return;
			}

			this.receivedKeepAlive = false;
			this.keepAliveTimeSent = System.currentTimeMillis();
			this.connection.send(new KeepAlivePacket());
		}
	}

	@Inject(method = "onDisconnect(Ljava/lang/String;[Ljava/lang/Object;)V", at = @At("HEAD"))
	public void removeFromTab(CallbackInfo ci) {
		BetaQOL.sendPlayerInfo(this.player.name, false, 0);
	}

	public void handleKeepAlive(KeepAlivePacket packet) {
		int ping = (int) (System.currentTimeMillis() - this.keepAliveTimeSent);
		((IServerPlayerEntity)this.player).setPing(Math.max(ping, 0));
		this.receivedKeepAlive = true;
	}
}