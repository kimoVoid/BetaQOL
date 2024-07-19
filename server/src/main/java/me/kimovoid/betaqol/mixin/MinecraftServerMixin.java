package me.kimovoid.betaqol.mixin;

import me.kimovoid.betaqol.BetaQOL;
import me.kimovoid.betaqol.interfaces.IMinecraftServer;
import me.kimovoid.betaqol.mixin.access.PacketInvoker;
import me.kimovoid.betaqol.networking.ServerPingPacket;
import me.kimovoid.betaqol.rcon.RconServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerProperties;
import net.minecraft.server.network.ListenThread;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.logging.LogManager;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin implements IMinecraftServer {

	@Shadow
	public ServerProperties properties;

	@Shadow
	public ListenThread connections;

	@Shadow
	int ticks;

	@Unique
	private long[] averageTickTimes = new long[100];

	@Unique
	private long previousTime;

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

	/*
	 * Move connection ticks outside the game ticks
	 * Saves 50ms of latency
	 */
	@Inject(
			method = "run()V", at = @At(value = "INVOKE", target = "Ljava/lang/Thread;sleep(J)V", shift = At.Shift.BEFORE)
	)
	private void tickConnections(CallbackInfo ci) {
		this.connections.tick();
	}

	/* Avoid unnecessary connection ticks */
	@Redirect(
			method = "tick()V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ListenThread;tick()V")
	)
	private void removeConnectionTick(ListenThread thread) {}

	/* Measure TPS */
	@Inject(
			method = "tick()V",
			at = @At("HEAD")
	)
	private void getPreviousTime(CallbackInfo ci) {
		this.previousTime = System.nanoTime();
	}

	@Inject(
			method = "tick()V",
			at = @At("TAIL")
	)
	private void measureTPS(CallbackInfo ci) {
		this.averageTickTimes[this.ticks % 100] = System.nanoTime() - this.previousTime;
	}

	@Override
	public long[] getTickTimes() {
		return this.averageTickTimes;
	}
}
