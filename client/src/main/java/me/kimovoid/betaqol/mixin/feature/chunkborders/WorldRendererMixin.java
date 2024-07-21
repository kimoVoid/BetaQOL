package me.kimovoid.betaqol.mixin.feature.chunkborders;

import me.kimovoid.betaqol.BetaQOL;
import net.minecraft.client.render.world.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    @Inject(method = "render(ID)V", at = @At("TAIL"))
    private void renderChunkBorders(int lastTick, double tickDelta, CallbackInfo ci) {
        BetaQOL.INSTANCE.getChunkBorderRenderer().render((float)tickDelta);
    }
}