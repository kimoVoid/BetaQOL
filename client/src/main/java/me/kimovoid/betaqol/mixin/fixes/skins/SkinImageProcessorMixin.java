package me.kimovoid.betaqol.mixin.fixes.skins;

import net.minecraft.client.render.texture.SkinImageProcessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

@Mixin(SkinImageProcessor.class)
public abstract class SkinImageProcessorMixin {

    @Shadow private int width;
    @Shadow private int height;
    @Shadow private int[] data;
    @Shadow protected abstract void setOpaque(int uMin, int vMin, int uMax, int vMax);
    @Shadow protected abstract void setTransparent(int uMin, int vMin, int uMax, int vMax);

    /**
     * @author kimoVoid
     * @reason handle modern skins
     */
    @Overwrite
    public BufferedImage process(BufferedImage image) {
        if (image == null) {
            return null;
        }
        this.width = 64;
        this.height = 64;
        BufferedImage bufferedImage = new BufferedImage(this.width, this.height, 2);
        Graphics graphics = bufferedImage.getGraphics();
        graphics.drawImage(image, 0, 0, null);
        if (image.getHeight() == 32) {
            graphics.drawImage(bufferedImage, 24, 48, 20, 52, 4, 16, 8, 20, null);
            graphics.drawImage(bufferedImage, 28, 48, 24, 52, 8, 16, 12, 20, null);
            graphics.drawImage(bufferedImage, 20, 52, 16, 64, 8, 20, 12, 32, null);
            graphics.drawImage(bufferedImage, 24, 52, 20, 64, 4, 20, 8, 32, null);
            graphics.drawImage(bufferedImage, 28, 52, 24, 64, 0, 20, 4, 32, null);
            graphics.drawImage(bufferedImage, 32, 52, 28, 64, 12, 20, 16, 32, null);
            graphics.drawImage(bufferedImage, 40, 48, 36, 52, 44, 16, 48, 20, null);
            graphics.drawImage(bufferedImage, 44, 48, 40, 52, 48, 16, 52, 20, null);
            graphics.drawImage(bufferedImage, 36, 52, 32, 64, 48, 20, 52, 32, null);
            graphics.drawImage(bufferedImage, 40, 52, 36, 64, 44, 20, 48, 32, null);
            graphics.drawImage(bufferedImage, 44, 52, 40, 64, 40, 20, 44, 32, null);
            graphics.drawImage(bufferedImage, 48, 52, 44, 64, 52, 20, 56, 32, null);
        }
        graphics.dispose();
        this.data = ((DataBufferInt)bufferedImage.getRaster().getDataBuffer()).getData();
        this.setOpaque(0, 0, 32, 16);
        this.setTransparent(32, 0, 64, 32);
        this.setOpaque(0, 16, 64, 32);
        this.setTransparent(0, 32, 16, 48);
        this.setTransparent(16, 32, 40, 48);
        this.setTransparent(40, 32, 56, 48);
        this.setTransparent(0, 48, 16, 64);
        this.setOpaque(16, 48, 48, 64);
        this.setTransparent(48, 48, 64, 64);
        return bufferedImage;
    }
}