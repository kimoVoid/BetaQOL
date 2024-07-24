package me.kimovoid.betaqol.mixin.feature.keybinding;

import io.github.crazysmc.thrkbs.CustomKeyBinding;
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
        this.keyBindings = handler.registerKeybinding(this.keyBindings, new KeyBinding("key.playerlist", Keyboard.KEY_TAB));
        this.keyBindings = handler.registerKeybinding(this.keyBindings, new CustomKeyBinding("key.debug.chunkBorders", Keyboard.KEY_G, "key.categories.debug"));
        this.keyBindings = handler.registerKeybinding(this.keyBindings, new CustomKeyBinding("key.debug.itemDurability", Keyboard.KEY_H, "key.categories.debug"));
    }
}