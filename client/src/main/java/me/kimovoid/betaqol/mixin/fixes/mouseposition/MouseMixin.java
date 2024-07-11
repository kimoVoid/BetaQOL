package me.kimovoid.betaqol.mixin.fixes.mouseposition;

import net.minecraft.client.Mouse;
import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/* Fixes cursor position not being centered in fullscreen */
@Mixin(Mouse.class)
public class MouseMixin {

    @Redirect(method = "release()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/input/Mouse;setCursorPosition(II)V",
                    remap = false
            )
    )
    private void fixCursor(int new_x, int new_y) {
        org.lwjgl.input.Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2);
    }
}