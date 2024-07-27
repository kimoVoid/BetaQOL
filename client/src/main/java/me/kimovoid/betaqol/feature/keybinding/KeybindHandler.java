package me.kimovoid.betaqol.feature.keybinding;

import me.kimovoid.betaqol.BetaQOL;
import net.minecraft.client.Minecraft;
import net.minecraft.client.options.KeyBinding;
import org.spongepowered.asm.mixin.Unique;

import java.util.Arrays;

public class KeybindHandler {

    public int getKeyFromCode(int code) {
        return code;
    }

    /* Returns -1 if key wasn't found */
    public int getKeyByName(String name) {
        int code = -1;
        for (KeyBinding key : Minecraft.INSTANCE.options.keyBindings) {
            if (key.name.equals(name)) code = key.keyCode;
        }
        return code;
    }

    public KeyBinding[] registerKeybinding(KeyBinding[] arr, String name, int keyCode, String category) {
        if (!category.isEmpty()) {
            return arr; // Only add category keybindings with Thorough Keybindings mod
        }
        int length = arr.length;
        arr = Arrays.copyOf(arr, length + 1);
        arr[length] = new KeyBinding(name, keyCode);
        return arr;
    }
}