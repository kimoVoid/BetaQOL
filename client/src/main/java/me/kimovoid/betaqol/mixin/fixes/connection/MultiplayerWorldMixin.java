package me.kimovoid.betaqol.mixin.fixes.connection;

import net.minecraft.client.network.handler.ClientNetworkHandler;
import net.minecraft.client.world.MultiplayerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MultiplayerWorld.class)
public class MultiplayerWorldMixin {

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/handler/ClientNetworkHandler;tick()V"))
    private void removeUnnecessaryTick(ClientNetworkHandler instance) {
    }
}
