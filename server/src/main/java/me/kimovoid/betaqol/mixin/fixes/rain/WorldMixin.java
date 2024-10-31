package me.kimovoid.betaqol.mixin.fixes.rain;

import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldData;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.storage.WorldStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(World.class)
public class WorldMixin {

	@Shadow private boolean allPlayersSleeping;
	@Shadow protected WorldData data;
	@Final @Shadow public Dimension dimension;

	@Redirect(
			method = "tick()V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/World;wakeSleepingPlayers()V"
			)
	)
	protected void fixWeather(World w) {
		this.allPlayersSleeping = false;
		boolean onlyBedBot = true;

		for (PlayerEntity p : w.players) {
			if (p.isSleeping()) {
				if (!p.name.equalsIgnoreCase("bedbot")) onlyBedBot = false;
				p.wakeUp(false, false, true);
			}
		}

		if (!onlyBedBot) {
			this.data.setRainTime(0);
			this.data.setRaining(false);
			this.data.setThunderTime(0);
			this.data.setThundering(false);
		}
	}

	@Redirect(
			method = "saveData()V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/storage/WorldStorage;saveData(Lnet/minecraft/world/WorldData;Ljava/util/List;)V"
			)
	)
	private void fixSave(WorldStorage storage, WorldData data, List<PlayerEntity> players) {
		if (this.dimension.id == 0) storage.saveData(data, players);
	}
}
