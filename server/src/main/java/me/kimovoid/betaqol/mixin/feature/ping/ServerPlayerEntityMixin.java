package me.kimovoid.betaqol.mixin.feature.ping;

import me.kimovoid.betaqol.interfaces.IServerPlayerEntity;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin implements IServerPlayerEntity {

	@Unique
	private int ping;

	public int getPing() {
		return this.ping;
	}

	public void setPing(int ping) {
		this.ping = ping;
	}
}