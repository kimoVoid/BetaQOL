package me.kimovoid.betaqol.mixin.feature.misc;

import me.kimovoid.betaqol.feature.gui.CallbackButtonWidget;
import me.kimovoid.betaqol.mixin.access.ScreenInvoker;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Screen.class)
public class ScreenMixin {

    @Redirect(method = "*", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;buttonClicked(Lnet/minecraft/client/gui/widget/ButtonWidget;)V"))
    private void onActionPerformed(Screen screen, ButtonWidget button) {
        if (button instanceof CallbackButtonWidget) {
            CallbackButtonWidget buttonWidget = (CallbackButtonWidget) button;
            buttonWidget.onPress();
            return;
        }
        ((ScreenInvoker)screen).callButtonClicked(button);
    }
}
