package me.kimovoid.betaqol.mixin.fixes.haspaidcheck;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/*
 * This has to be disabled since it no
 * longer works and it throws errors
 * in the console.
 */
@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;initTimerHackThread()V"))
    private void disableTimerCheck(Minecraft instance) {
    }
}