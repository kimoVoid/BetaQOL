package me.kimovoid.betaqol.mixin.fixes.worldjitter;

import net.minecraft.client.render.world.RenderChunkStorage;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderChunkStorage.class)
public class RenderChunkStorageMixin {

    @Shadow private int regionX;
    @Shadow private int regionY;
    @Shadow private int regionZ;

    @Unique private double cameraXD;
    @Unique private double cameraYD;
    @Unique private double cameraZD;

    @Redirect(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glTranslatef(FFF)V",
                    remap = false
            )
    )
    private void fixWorldJitter(float x, float y, float z) {
        GL11.glTranslated(this.regionX - this.cameraXD, this.regionY - this.cameraYD, this.regionZ - this.cameraZD);
    }

    @Inject(
            method = "setPositions",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/render/world/RenderChunkStorage;cameraX:F"
            ),
            cancellable = true
    )
    private void setDoublePositions(int regionX, int regionY, int regionZ, double cameraX, double cameraY, double cameraZ, CallbackInfo ci) {
        this.cameraXD = cameraX;
        this.cameraYD = cameraY;
        this.cameraZD = cameraZ;
        ci.cancel();
    }
}