package me.kimovoid.betaqol.mixin.fixes.disconnectspam;

import net.minecraft.server.network.handler.ServerLoginNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.logging.Logger;

@Mixin(ServerLoginNetworkHandler.class)
public class ServerLoginNetworkHandlerMixin {

    // this is annoying tf out of me
    @Redirect(
            method = "onDisconnect",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/logging/Logger;info(Ljava/lang/String;)V",
                    remap = false
            )
    )
    private void removeDisconnectSpam(Logger instance, String msg) {
    }
}