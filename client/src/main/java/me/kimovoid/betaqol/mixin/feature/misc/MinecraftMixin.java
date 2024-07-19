package me.kimovoid.betaqol.mixin.feature.misc;

import me.kimovoid.betaqol.BetaQOL;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    @Shadow private static Minecraft INSTANCE;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onMcInit(CallbackInfo ci) {
        BetaQOL.mc = INSTANCE;
    }
}