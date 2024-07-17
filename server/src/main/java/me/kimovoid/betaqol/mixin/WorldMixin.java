package me.kimovoid.betaqol.mixin;

import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldData;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.storage.WorldStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(World.class)
public class WorldMixin {

	@Shadow
	private boolean allPlayersSleeping;

	@Shadow
	public List players;

	@Shadow
	public boolean isMultiplayer;

	@Shadow
	protected WorldData data;

	@Shadow
	public Dimension dimension;

	public void updateSleepingPlayers() {
		this.allPlayersSleeping = !this.players.isEmpty();

		for (Object player : this.players) {
			PlayerEntity var2 = (PlayerEntity) player;
			if (var2.isSleptEnough()) {
				this.allPlayersSleeping = true;
				break;
			}
		}
	}

	@Inject(method = "canSkipNight()Z", at = @At("RETURN"), cancellable = true)
	public boolean canSkipNightInject(CallbackInfoReturnable<Boolean> cir) {
		for (Object playerEntity : this.players) {
			PlayerEntity pl = (PlayerEntity) playerEntity;
			if (pl.isSleptEnough()) {
				return true;
			}
		}
		return false;
	}

	@ModifyArg(
			method = "tick()V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/NaturalSpawner;m_2442438(Lnet/minecraft/world/World;Ljava/util/List;)Z"
			), index = 1
	)
	private List injected(List<? extends PlayerEntity> oldList) {
		List<PlayerEntity> newList = new ArrayList<PlayerEntity>();
		for (PlayerEntity obj : oldList) {
			if ((obj).isSleeping()) newList.add(obj);
		}
		return newList;
	}

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

		for (Object player : w.players) {
			PlayerEntity p = (PlayerEntity) player;
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
	private void fixSave(WorldStorage storage, WorldData data, List players) {
		if (this.dimension.id == 0) storage.saveData(data, players);
	}
}
