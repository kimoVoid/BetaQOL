package me.kimovoid.betaqol.mixin.feature.deathscreen;

import me.kimovoid.betaqol.BetaQOL;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.render.TextRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DeathScreen.class)
public class DeathScreenMixin {

    @Redirect(method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/DeathScreen;drawCenteredString(Lnet/minecraft/client/render/TextRenderer;Ljava/lang/String;III)V",
                    ordinal = 1
            )
    )
    private void replaceDeathMessage(DeathScreen instance, TextRenderer textRenderer, String text, int x, int y, int color) {
        String msg = BetaQOL.CONFIG.deathScreenMsg.get().replace("{score}", "" + BetaQOL.mc.player.getScore());
        instance.drawCenteredString(textRenderer, msg, x, y, color);
    }
}