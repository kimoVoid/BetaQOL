package me.kimovoid.betaqol.mixin.feature.clipboardscreenshot;

import me.kimovoid.betaqol.BetaQOL;
import me.kimovoid.betaqol.feature.clipboardscreenshot.TransferableImage;
import net.minecraft.client.util.ScreenshotUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

@Mixin(ScreenshotUtils.class)
public class ScreenshotUtilsMixin {

    @Redirect(method = "saveScreenshot",
            at = @At(
                    value = "INVOKE",
                    target = "Ljavax/imageio/ImageIO;write(Ljava/awt/image/RenderedImage;Ljava/lang/String;Ljava/io/File;)Z"
            )
    )
    private static boolean copyScreenshotToClipboard(RenderedImage im, String formatName, File output) throws IOException {
        if (BetaQOL.CONFIG.screenshotToClipboard.get()) {
            try {
                TransferableImage trans = new TransferableImage((Image)im);
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(trans, null);
            } catch (Exception ignored) {}
        }

        return ImageIO.write(im, formatName, output);
    }
}
