package me.kimovoid.betaqol.mixin.fixes.connection;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Inject(method = "run",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Box;resetCache()V",
                    shift = At.Shift.BEFORE,
                    ordinal = 0
            )
    )
    private void tickNetwork(CallbackInfo ci) {
        if (Minecraft.INSTANCE.isMultiplayer()) {
            Minecraft.INSTANCE.getNetworkHandler().tick();
        }
    }
}
