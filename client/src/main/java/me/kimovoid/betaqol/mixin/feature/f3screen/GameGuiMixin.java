package me.kimovoid.betaqol.mixin.feature.f3screen;

import me.kimovoid.betaqol.BetaQOL;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GameGui;
import net.minecraft.client.gui.GuiElement;
import net.minecraft.client.render.TextRenderer;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.text.DecimalFormat;
import java.util.Random;

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

    /* Adds fabric label and positions fps under it */
    @Redirect(method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/TextRenderer;drawWithShadow(Ljava/lang/String;III)V",
                    ordinal = 0,
                    remap = false
            )
    )
    public void modifyF3Label(TextRenderer renderer, String text, int x, int y, int color) {
        if (BetaQOL.CONFIG.modernF3.get()) {
            this.renderText("Minecraft Beta 1.7.3 (Fabric/QOL)", x, y, 9);
            this.renderText(this.minecraft.fpsDebugString, x, y + 10, 9);
            return;
        }
        this.renderText(text, x, y, 9);
    }

    /* Re-render content with plates */
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
        this.renderText(text, x, y + (BetaQOL.CONFIG.modernF3.get() ? 10 : 0), 9);
    }

    @Redirect(method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GameGui;drawString(Lnet/minecraft/client/render/TextRenderer;Ljava/lang/String;III)V"
            )
    )
    public void redirectMoreContent(GameGui instance, TextRenderer textRenderer, String text, int x, int y, int color) {
        if (x != 2) {
            this.renderText(text, x + 1, y, 9);
            return;
        }

        if (!BetaQOL.CONFIG.extendedF3.get()) {
            this.renderText(text, x, y, 7);
        }
    }

    /* Extend debug screen */
    @Inject(method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glPopMatrix()V",
                    shift = At.Shift.AFTER,
                    ordinal = 1,
                    remap = false
            )
    )
    public void renderExtendedF3(CallbackInfo ci) {
        if (!BetaQOL.CONFIG.extendedF3.get()) {
            return;
        }

        int shift = BetaQOL.CONFIG.modernF3.get() ? 10 : 0;
        int x = MathHelper.floor(this.minecraft.player.x);
        int y = MathHelper.floor(this.minecraft.player.y);
        int z = MathHelper.floor(this.minecraft.player.z);
        int chunkX = x >> 4;
        int chunkZ = z >> 4;
        long time = MathHelper.floor((double)(this.minecraft.world.getTime() / 24000L));
        String biome = this.minecraft.world.getBiomeSource().getBiome(x, z).name;
        float angle = (this.minecraft.world.getTimeOfDay(1.0F) * 24.0F + 12.0F) % 24.0F;
        int h = (int)Math.floor(angle);
        int m = (int)Math.floor(angle * 60.0F) - h * 60;
        int s = (int)Math.floor(angle * 3600.0F) - h * 3600 - m * 60;
        int f = (MathHelper.floor((double)(this.minecraft.player.yaw * 4.0f / 360.0f) + 0.5) & 3);
        String facing = f == 0 ? "west (Towards positive Z)"
                : f == 1 ? "north (Towards negative X)"
                : f == 2 ? "east (Towards negative Z)"
                : "south (Towards positive X)";

        this.renderText(String.format("XYZ: %s / %s / %s",
                this.format(this.minecraft.player.x),
                this.format(this.minecraft.player.y),
                this.format(this.minecraft.player.z)
        ), 2, 64 + shift, 9);
        this.renderText(String.format("Block: %s %s %s", x, y, z), 2, 74 + shift, 9);
        this.renderText("Facing: " + facing, 2, 84 + shift, 9);
        this.renderText("Chunk: " + String.format("%s, %s [%s, %s]", chunkX, chunkZ, x & 15, z & 15), 2, 94 + shift, 9);

        this.renderText("Light: " + this.minecraft.world.getLight(x, y, z), 2, 114 + shift, 9);
        this.renderText("Biome: " + (biome == null ? "Unknown" : biome), 2, 124 + shift, 9);
        this.renderText("Seed: " + this.minecraft.world.getSeed(), 2, 134 + shift, 9);
        this.renderText("Day: " + time, 2, 144 + shift, 9);
        this.renderText("Time: " + String.format("%02d:%02d:%02d", h, m, s), 2, 154 + shift, 9);
        this.renderText("Slime: " + this.isSlimeChunk(
                this.minecraft.world.getSeed(),
                this.minecraft.player.chunkX,
                this.minecraft.player.chunkZ
        ), 2, 164 + shift, 9);
    }

    @Unique
    private boolean isSlimeChunk(long seed, int x, int z) {
        Random rnd = new Random(
                seed +
                        (int) (x * x * 0x4c1906) +
                        (int) (x * 0x5ac0db) +
                        (int) (z * z) * 0x4307a7L +
                        (int) (z * 0x5f24f) ^ 0x3ad8025fL
        );

        return rnd.nextInt(10) == 0;
    }

    @Unique
    private void renderText(String text, int x, int y, int plateHeight) {
        if (BetaQOL.CONFIG.modernF3.get()) {
            this.fill(x - 1, y - 1, x + BetaQOL.mc.textRenderer.getWidth(text), y + plateHeight, -1873784752);
            BetaQOL.mc.textRenderer.draw(text, x, y, 0xFFFFFF);
        } else {
            BetaQOL.mc.textRenderer.drawWithShadow(text, x, y, 0xFFFFFF);
        }
    }

    @Unique
    private String format(double d) {
        DecimalFormat df = new DecimalFormat("#.###");
        return df.format(d);
    }
}
