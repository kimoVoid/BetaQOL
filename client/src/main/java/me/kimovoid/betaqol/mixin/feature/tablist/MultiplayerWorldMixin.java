package me.kimovoid.betaqol.mixin.feature.tablist;

import me.kimovoid.betaqol.BetaQOL;
import net.minecraft.client.world.MultiplayerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerWorld.class)
public class MultiplayerWorldMixin {

    @Inject(method = "disconnect", at = @At("HEAD"))
    public void clearTab(CallbackInfo ci) {
        BetaQOL.INSTANCE.tabPlayers.clear();
    }
}
