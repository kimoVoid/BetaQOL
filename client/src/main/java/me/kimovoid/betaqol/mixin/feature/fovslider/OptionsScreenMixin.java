package me.kimovoid.betaqol.mixin.feature.fovslider;

import me.kimovoid.betaqol.feature.fov.FOVOption;
import me.kimovoid.betaqol.feature.fov.FOVOptionSlider;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.options.OptionsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OptionsScreen.class)
public abstract class OptionsScreenMixin extends Screen {

    /**
     * I need to remind myself to re-order the
     * entire options menu. It's a mess.
     */

    @Inject(method = "init", at = @At("TAIL"))
    private void addFovButton(CallbackInfo ci) {
        int y = FabricLoader.getInstance().isModLoaded("sound-categories") ? 0 : 24 * (5 >> 1);
        this.buttons.add(new FOVOptionSlider(
                69,
                this.width / 2 - 155 + 160,
                this.height / 6 + y,
                FOVOption.getLocalizedFov(),
                FOVOption.fov));
    }
}