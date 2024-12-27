package me.kimovoid.betaqol.mixin.feature.spawnprotection;

import me.kimovoid.betaqol.BetaQOL;
import net.minecraft.server.network.handler.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {

    @ModifyConstant(
            method = {
                    "handlePlayerHandAction",
                    "handlePlayerUse"
            },
            constant = @Constant(intValue = 16)
    )
    private int setSpawnProtection(int orig) {
        return BetaQOL.INSTANCE.properties.spawnProtection;
    }
}