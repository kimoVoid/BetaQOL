package me.kimovoid.betaqol.mixin;

import me.kimovoid.betaqol.BetaQOL;
import me.kimovoid.betaqol.interfaces.IServerPlayerEntity;
import net.minecraft.entity.Entities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.network.packet.ChatMessagePacket;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements IServerPlayerEntity {

	@Unique
	private int ping;

	public ServerPlayerEntityMixin(World world) {
		super(world);
	}

	@Inject(method = "onKilled(Lnet/minecraft/entity/Entity;)V", at = @At("TAIL"))
	public void injectOnKilled(Entity entity, CallbackInfo ci) {
		String msg = this.name + " died";

		if (entity != null) {
			String entityName = Entities.getKey(entity);
			if (entity instanceof PlayerEntity) entityName = ((PlayerEntity)entity).name;
			msg = this.name + " was slain by " + entityName;
		}

		BetaQOL.server.playerManager.sendPacket(new ChatMessagePacket(msg));
		BetaQOL.server.sendMessage(msg);

		if (BetaQOL.server.properties.getBoolean("death-coordinates", false)) {
			BetaQOL.server.playerManager.sendMessageToPlayer(
					this.name,
					String.format("Death coordinates: %.1f, %.1f, %.1f", this.x, this.y, this.z)
			);
		}
	}

	public int getPing() {
		return this.ping;
	}

	public void setPing(int ping) {
		this.ping = ping;
	}
}
