package me.kimovoid.betaqol.mixin.feature.tps;

import me.kimovoid.betaqol.interfaces.IMinecraftServer;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin implements IMinecraftServer {

	@Shadow int ticks;
	@Unique private long[] averageTickTimes = new long[100];
	@Unique private long previousTime;

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