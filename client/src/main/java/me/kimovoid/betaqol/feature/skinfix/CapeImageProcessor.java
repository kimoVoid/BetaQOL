package me.kimovoid.betaqol.feature.skinfix;

import net.minecraft.client.render.texture.HttpImageProcessor;

import java.awt.*;
import java.awt.image.BufferedImage;

public class CapeImageProcessor implements HttpImageProcessor {

    @Override
    public BufferedImage process(BufferedImage image) {
        if (image == null) {
            return null;
        } else {
            int width = 64;
            int height = 32;

            for (int i = image.getHeight(); width < image.getWidth() || height < i; height *= 2) {
                width *= 2;
            }

            BufferedImage bufferedimage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics graphics = bufferedimage.getGraphics();
            graphics.drawImage(image, 0, 0, null);
            graphics.dispose();
            return bufferedimage;
        }
    }
}