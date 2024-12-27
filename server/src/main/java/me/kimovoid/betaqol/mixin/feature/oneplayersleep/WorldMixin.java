package me.kimovoid.betaqol.mixin.feature.oneplayersleep;

import me.kimovoid.betaqol.BetaQOL;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.List;

@Mixin(World.class)
public class WorldMixin {

	@Shadow public List<PlayerEntity> players;

	@Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;canSkipNight()Z"))
	public boolean doOnePlayerSleep(World instance) {
		if (BetaQOL.SERVER.properties.getBoolean("one-player-sleep", false)) {
			for (PlayerEntity pl : this.players) {
				if (pl.isSleptEnough()) {
					return true;
				}
			}
			return false;
		}

		return instance.canSkipNight();
	}

	@ModifyArg(
			method = "tick()V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/NaturalSpawner;m_2442438(Lnet/minecraft/world/World;Ljava/util/List;)Z"
			), index = 1
	)
	private List<PlayerEntity> getSleepingList(List<PlayerEntity> oldList) {
		if (!BetaQOL.SERVER.properties.getBoolean("one-player-sleep", false)) {
			return oldList;
		}

		List<PlayerEntity> newList = new ArrayList<PlayerEntity>();
		for (PlayerEntity obj : oldList) {
			if ((obj).isSleeping()) newList.add(obj);
		}
		return newList;
	}
}
