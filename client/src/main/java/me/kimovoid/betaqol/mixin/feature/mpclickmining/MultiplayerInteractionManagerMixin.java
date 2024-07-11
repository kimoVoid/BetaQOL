package me.kimovoid.betaqol.mixin.feature.mpclickmining;

import me.kimovoid.betaqol.BetaQOL;
import net.minecraft.client.interaction.MultiplayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerInteractionManager.class)
public class MultiplayerInteractionManagerMixin {

    @Shadow private int miningCooldown;

    @Inject(method = "stopMiningBlock", at = @At("TAIL"))
    private void resetDelay(CallbackInfo ci) {
        if (BetaQOL.CONFIG.enableMpClickMining.get()) {
            this.miningCooldown = 0;
        }
    }
}
