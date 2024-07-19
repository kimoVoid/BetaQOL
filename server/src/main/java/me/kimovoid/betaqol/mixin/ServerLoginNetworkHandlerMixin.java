package me.kimovoid.betaqol.mixin;

import me.kimovoid.betaqol.BetaQOL;
import net.minecraft.network.packet.KeepAlivePacket;
import net.minecraft.network.packet.LoginPacket;
import net.minecraft.server.network.handler.ServerLoginNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLoginNetworkHandler.class)
public class ServerLoginNetworkHandlerMixin {

    @Inject(
            method = "acceptLogin",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/entity/living/player/ServerPlayerEntity;initMenu()V",
                    shift = At.Shift.AFTER
            )
    )
    public void injectLogin(LoginPacket packet, CallbackInfo ci) {
        BetaQOL.server.playerManager.sendPacket(packet.username, new KeepAlivePacket());
        BetaQOL.sendPlayerInfo(packet.username, true, 0);
    }
}
