package me.kimovoid.betaqol.mixin.feature.tpbutton;

import me.kimovoid.betaqol.BetaQOL;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TexturePackScreen;
import net.minecraft.client.gui.screen.options.OptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.locale.LanguageManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(OptionsScreen.class)
public abstract class OptionsScreenMixin extends Screen {

    @Redirect(method = "init", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 2))
    private boolean addTexturePacks(List<ButtonWidget> list, Object obj) {
        if (BetaQOL.CONFIG.texturePackButton.get()) {
            LanguageManager lang = LanguageManager.getInstance();
            boolean sc = FabricLoader.getInstance().isModLoaded("sound-categories");

            list.add(new ButtonWidget(99, this.width / 2 + (sc ? 5 : -100), this.height / 6 + (sc ? 0 : 76 + 12), sc ? 150 : 200, 20, lang.translate("options.texturePacks")));
            list.add(sc ? (ButtonWidget) obj : new ButtonWidget(101, this.width / 2 - 100, this.height / 6 + 98 + 12, lang.translate("options.video")));
            return true;
        }

        return list.add((ButtonWidget) obj);
    }

    @Inject(method = "buttonClicked", at = @At("HEAD"))
    private void openPacksScreen(ButtonWidget button, CallbackInfo ci) {
        if (button.active && button.id == 99) {
            this.minecraft.options.save();
            this.minecraft.openScreen(new TexturePackScreen(this));
        }
    }
}
