package me.kimovoid.betaqol.mixin.fixes.ghosttnt;

import net.minecraft.block.TntBlock;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TntBlock.class)
public class TntBlockMixin {

    @Inject(method = "onExploded", at = @At("HEAD"), cancellable = true)
    private void fixGhostTnt(World world, int x, int y, int z, CallbackInfo ci) {
        if (world.isMultiplayer) {
            ci.cancel();
        }
    }
}