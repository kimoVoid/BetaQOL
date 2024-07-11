package me.kimovoid.betaqol.mixin.feature.misc;

import me.kimovoid.betaqol.BetaQOL;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    @Shadow private int attackCooldown;

    @Shadow private static Minecraft INSTANCE;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onMcInit(CallbackInfo ci) {
        BetaQOL.mc = INSTANCE;
        BetaQOL.INSTANCE.mcInit();
    }

    @Inject(method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Minecraft;m_7097703(IZ)V",
                    shift = At.Shift.BEFORE
            )
    )
    private void removeCooldown(CallbackInfo ci) {
        this.attackCooldown = 0;
    }

    @Inject(method = "run",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Box;resetCache()V",
                    shift = At.Shift.BEFORE,
                    ordinal = 0
            )
    )
    private void tickNetwork(CallbackInfo ci) {
        if (BetaQOL.mc.isMultiplayer()) {
            BetaQOL.mc.getNetworkHandler().tick();
        }
    }
}