package me.kimovoid.betaqol.mixin;

import me.kimovoid.betaqol.BetaQOL;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.server.ServerPlayerInteractionManager;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerInteractionManager.class)
public class InteractionManagerMixin {

	@Shadow
	public PlayerEntity player;

	@Inject(method = "startMiningBlock(IIII)V", at = @At("HEAD"), cancellable = true)
	public void onStartMining(int x, int y, int z, int face, CallbackInfo ci) {
		if (player instanceof ServerPlayerEntity) {
			ServerPlayerEntity pl = (ServerPlayerEntity) player;
			if (BetaQOL.INSTANCE.instantBreak.contains(pl.name)) {
				pl.interactionManager.tryMineBlock(x, y, z);
				ci.cancel();
			}
		}
	}
}
