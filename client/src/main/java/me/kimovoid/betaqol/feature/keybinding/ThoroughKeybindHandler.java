package me.kimovoid.betaqol.feature.keybinding;

import io.github.crazysmc.thrkbs.CustomKeyBinding;
import net.minecraft.client.options.KeyBinding;

import java.util.Arrays;

public class ThoroughKeybindHandler extends KeybindHandler {

    @Override
    public int getKeyFromCode(int code) {
        return CustomKeyBinding.getKeyCodeByOriginal(code);
    }

    @Override
    public KeyBinding[] registerKeybinding(KeyBinding[] arr, String name, int keyCode, String category) {
        int length = arr.length;
        arr = Arrays.copyOf(arr, length + 1);
        arr[length] = category.isEmpty() ? new KeyBinding(name, keyCode) : new CustomKeyBinding(name, keyCode, category);
        return arr;
    }
}