package me.kimovoid.betaqol.mixin.feature.multiplayer;

import me.kimovoid.betaqol.feature.gui.multiplayer.MultiplayerScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * This is a port of MojangFix for Babric.
 * All credits to js6pak and everyone involved in that project.
 * <a href="https://github.com/js6pak/mojangfix">View here</a>
 */
@Mixin({DisconnectedScreen.class, ConnectScreen.class})
public class ReturnToMainMenuMixin {

    @Redirect(method = "buttonClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;openScreen(Lnet/minecraft/client/gui/screen/Screen;)V"))
    private void redirectSetScreen(Minecraft minecraft, Screen screen) {
        minecraft.openScreen(new MultiplayerScreen());
    }
}