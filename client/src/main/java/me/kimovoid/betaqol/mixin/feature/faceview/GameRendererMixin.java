package me.kimovoid.betaqol.mixin.feature.faceview;

import me.kimovoid.betaqol.BetaQOL;
import me.kimovoid.betaqol.feature.faceview.IMinecraft;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.GameRenderer;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Shadow private Minecraft minecraft;

    @ModifyVariable(method = "transformCamera", at = @At("STORE"), ordinal = 3)
    private float setPitch(float pitch) {
        if (((IMinecraft)this.minecraft).getThirdPersonMode() == 2
                && BetaQOL.CONFIG.frontPerspective.get()) {
            return pitch + 180.0F;
        }
        return pitch;
    }

    @Inject(method = "transformCamera",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glRotatef(FFFF)V",
                    ordinal = 6,
                    shift = At.Shift.BEFORE,
                    remap = false
            )
    )
    public void rotateCamera(CallbackInfo ci) {
        if (((IMinecraft)this.minecraft).getThirdPersonMode() == 2
                && BetaQOL.CONFIG.frontPerspective.get()) {
            GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
        }
    }
}
