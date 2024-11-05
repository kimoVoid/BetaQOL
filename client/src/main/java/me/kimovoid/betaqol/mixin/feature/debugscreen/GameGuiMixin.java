package me.kimovoid.betaqol.mixin.feature.debugscreen;

import me.kimovoid.betaqol.BetaQOL;
import me.kimovoid.betaqol.feature.debugscreen.DebugScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GameGui;
import net.minecraft.client.gui.GuiElement;
import net.minecraft.client.render.TextRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameGui.class)
public class GameGuiMixin extends GuiElement {

    @Shadow private Minecraft minecraft;

    /* This always renders the version string */
    @Inject(method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glDisable(I)V",
                    ordinal = 2,
                    shift = At.Shift.AFTER,
                    remap = false
            )
    )
    public void alwaysRenderLabel(CallbackInfo ci) {
        if (!this.minecraft.options.debugProfilerEnabled && BetaQOL.CONFIG.showLabel.get()) {
            this.minecraft.textRenderer.drawWithShadow("Minecraft Beta 1.7.3", 2, 2, 0xFFFFFF);
        }
    }

    @Redirect(method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/TextRenderer;drawWithShadow(Ljava/lang/String;III)V"
            ),
            slice = @Slice(
                    from = @At(value = "INVOKE",
                            target = "Lorg/lwjgl/opengl/GL11;glPushMatrix()V",
                            ordinal = 1,
                            remap = false),
                    to = @At(value = "INVOKE",
                            target = "Lorg/lwjgl/opengl/GL11;glPopMatrix()V",
                            ordinal = 1,
                            remap = false)
            )
    )
    public void redirectContent(TextRenderer renderer, String text, int x, int y, int color) {
        if (BetaQOL.CONFIG.modernF3.get()) {
            return;
        }
        renderer.drawWithShadow(text, x, y, color);
    }

    @Redirect(method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GameGui;drawString(Lnet/minecraft/client/render/TextRenderer;Ljava/lang/String;III)V"
            )
    )
    public void redirectRightSideContent(GameGui instance, TextRenderer textRenderer, String text, int x, int y, int color) {
        if (BetaQOL.CONFIG.modernF3.get()) {
            return;
        }
        instance.drawString(textRenderer, text, x, y, color);
    }

    /* Modernize debug screen */
    @Inject(method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glPopMatrix()V",
                    shift = At.Shift.AFTER,
                    ordinal = 1,
                    remap = false
            )
    )
    public void renderModernizedF3(CallbackInfo ci) {
        if (!BetaQOL.CONFIG.modernF3.get()) {
            return;
        }
        DebugScreen.INSTANCE.renderLeftSideDebug(this.minecraft);
        DebugScreen.INSTANCE.renderRightSideDebug(this.minecraft);
    }
}