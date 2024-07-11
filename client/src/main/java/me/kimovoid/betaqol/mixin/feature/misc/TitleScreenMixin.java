package me.kimovoid.betaqol.mixin.feature.misc;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.render.TextRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/TitleScreen;drawString(Lnet/minecraft/client/render/TextRenderer;Ljava/lang/String;III)V", ordinal = 0))
	private void setMcString(TitleScreen instance, TextRenderer textRenderer, String s, int i1, int i2, int i3) {
		instance.drawString(textRenderer, "Minecraft Beta 1.7.3 (Fabric/QOL)", i1, i2, i3);
	}
}