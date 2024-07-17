package me.kimovoid.betaqol.mixin.access;

import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.server.network.handler.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerPlayNetworkHandler.class)
public interface NetworkHandlerAccessor {

    @Accessor("player")
    public ServerPlayerEntity getPlayer();
}
