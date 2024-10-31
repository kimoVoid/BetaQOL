package me.kimovoid.betaqol.mixin.misc;

import me.kimovoid.betaqol.BetaQOL;
import me.kimovoid.betaqol.mixin.access.PacketInvoker;
import me.kimovoid.betaqol.networking.ServerPingPacket;
import me.kimovoid.betaqol.rcon.RconServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.logging.LogManager;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

	@Shadow public ServerProperties properties;

	@Inject(method = "init", at = @At("TAIL"))
	public void initRcon(CallbackInfoReturnable<Boolean> cir) {
		PacketInvoker.register(254, false, true, ServerPingPacket.class);

		if (this.properties.getBoolean("enable-rcon", false)) {
			LogManager.getLogManager().getLogger("Minecraft").info("Starting remote control listener");
			BetaQOL.INSTANCE.rcon = new RconServer(BetaQOL.server);
			BetaQOL.INSTANCE.rcon.start();
		}
	}

	@ModifyArg(
			method = "init()Z",
			at = @At(
					value = "INVOKE",
					target = "Ljava/util/logging/Logger;info(Ljava/lang/String;)V"
			), index = 0
	)
	private String initVersion(String message) {
		if (message.startsWith("Starting minecraft server version Beta ")) {
			BetaQOL.INSTANCE.mcVersion = "b" + message.replace("Starting minecraft server version Beta ", "");
		}
		return message;
	}
}