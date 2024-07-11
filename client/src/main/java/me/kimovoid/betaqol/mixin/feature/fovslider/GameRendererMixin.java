package me.kimovoid.betaqol.mixin.feature.fovslider;

import me.kimovoid.betaqol.BetaQOL;
import me.kimovoid.betaqol.feature.fov.FOVOption;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.living.LivingEntity;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Shadow private Minecraft minecraft;

    @Shadow private float viewDistance;

    @Unique public boolean zooming = false;

    @Unique
    public float getFovMultiplier(float f, boolean isHand) {
        LivingEntity entity = this.minecraft.camera;
        float fov = FOVOption.getFovInDegrees();

        if (isHand) {
            fov = 70F;
        }

        if (entity.isSubmergedIn(Material.WATER)) {
            fov *= 60.0F / 70.0F;
        }

        /* Handle OF zoom if it is installed */
        int zoomCode = BetaQOL.INSTANCE.keybinds.getKeyByName("Zoom");
        if (zoomCode != -1 && Keyboard.isKeyDown(zoomCode)) {
            if (!this.zooming) {
                this.zooming = true;
                this.minecraft.options.smoothCamera = true;
            }

            fov /= 4.0F;
        } else if (this.zooming) {
            this.zooming = false;
            this.minecraft.options.smoothCamera = false;
        }

        if (entity.health <= 0) {
            float deathTimeFov = (float) entity.deathTime + f;
            fov /= (1.0F - 500F / (deathTimeFov + 500F)) * 2.0F + 1.0F;
        }

        return fov;
    }

    @Unique
    public float getFovMultiplier(float f) {
        return getFovMultiplier(f, false);
    }

    @Redirect(method = "setupCamera", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;getFov(F)F"))
    public float redirectToCustomFov(GameRenderer instance, float value) {
        return getFovMultiplier(value);
    }

    @Inject(method = "renderItemInHand", at = @At(value = "HEAD"))
    public void adjustHandFov(float f, int i, CallbackInfo ci) {
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GLU.gluPerspective(getFovMultiplier(f, true), (float) minecraft.width / (float) minecraft.height, 0.05F, viewDistance * 2.0F);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
    }
}
