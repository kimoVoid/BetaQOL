package me.kimovoid.betaqol.mixin.feature.fovslider;

import me.kimovoid.betaqol.feature.fov.FOVOption;
import me.kimovoid.betaqol.feature.fov.FOVOptionSlider;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.options.OptionsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OptionsScreen.class)
public abstract class OptionsScreenMixin extends Screen {

    @Inject(method = "init", at = @At("TAIL"))
    private void addFovButton(CallbackInfo ci) {
        this.buttons.add(new FOVOptionSlider(
                69,
                this.width / 2 - 155 + 160,
                this.height / 6 + 24 * (5 >> 1),
                FOVOption.getLocalizedFov(),
                FOVOption.fov));
    }
}
