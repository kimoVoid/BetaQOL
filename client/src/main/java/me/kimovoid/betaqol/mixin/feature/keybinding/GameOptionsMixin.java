package me.kimovoid.betaqol.mixin.feature.keybinding;

import me.kimovoid.betaqol.BetaQOL;
import me.kimovoid.betaqol.feature.keybinding.KeybindHandler;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameOptions.class)
public class GameOptionsMixin {

    @Shadow public KeyBinding[] keyBindings;

    /* Used for registering keys */
    @Inject(method = "load", at = @At(value = "HEAD"))
    private void preLoadOptions(CallbackInfo ci) {
        KeybindHandler handler = BetaQOL.INSTANCE.keybinds;
        this.keyBindings = handler.registerKeybinding(this.keyBindings, "key.playerlist", Keyboard.KEY_TAB, "");
        this.keyBindings = handler.registerKeybinding(this.keyBindings, "key.debug.chunkBorders", Keyboard.KEY_G, "key.categories.debug");
        this.keyBindings = handler.registerKeybinding(this.keyBindings, "key.debug.itemDurability", Keyboard.KEY_H, "key.categories.debug");
        this.keyBindings = handler.registerKeybinding(this.keyBindings, "key.debug.reloadSkins", Keyboard.KEY_R, "key.categories.debug");
    }
}