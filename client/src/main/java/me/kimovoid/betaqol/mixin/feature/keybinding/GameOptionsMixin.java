package me.kimovoid.betaqol.mixin.feature.keybinding;

import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@Mixin(GameOptions.class)
public class GameOptionsMixin {

    @Shadow public KeyBinding[] keyBindings;

    /* Used for registering keys */
    @Inject(method = "load", at = @At(value = "HEAD"))
    private void preLoadOptions(CallbackInfo ci) {
        this.registerKeybinding("key.playerlist", Keyboard.KEY_TAB);
    }

    @Unique
    private void registerKeybinding(String name, int keyCode) {
        this.keyBindings = append(
                this.keyBindings,
                new KeyBinding(name, keyCode)
        );
    }

    @Unique
    private <T> T[] append(T[] array, T obj) {
        int length = array.length;
        array = Arrays.copyOf(array, length + 1);
        array[length] = obj;
        return array;
    }
}