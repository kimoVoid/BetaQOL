package me.kimovoid.betaqol.mixin.fixes.quitbutton;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MinecraftApplet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/* Fixes the quit button not showing up */
@Mixin(MinecraftApplet.class)
public class MinecraftAppletMixin {

    @Shadow private Minecraft minecraft;

    @Inject(method = "init", at = @At(value = "TAIL"), remap = false)
    public void setIsAppletToFalse(CallbackInfo ci) {
        this.minecraft.paused = false;
    }
}
