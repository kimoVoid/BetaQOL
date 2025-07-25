package me.kimovoid.betaqol.mixin.feature.multiplayer;

import me.kimovoid.betaqol.feature.gui.multiplayer.MultiplayerScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {

    @Redirect(method = "buttonClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;openScreen(Lnet/minecraft/client/gui/screen/Screen;)V", ordinal = 2))
    private void onNewGuiMainMenu(Minecraft minecraft, Screen screen) {
        minecraft.openScreen(new MultiplayerScreen(this));
    }
}