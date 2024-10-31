package me.kimovoid.betaqol.mixin.command;

import me.kimovoid.betaqol.BetaQOL;
import net.minecraft.command.source.CommandSource;
import net.minecraft.network.Connection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.server.network.handler.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.logging.LogManager;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin implements CommandSource {

	@Shadow private ServerPlayerEntity player;
	@Shadow private MinecraftServer server;
	@Shadow public Connection connection;

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
}