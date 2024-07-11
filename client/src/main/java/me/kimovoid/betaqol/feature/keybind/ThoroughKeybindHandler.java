package me.kimovoid.betaqol.feature.keybind;

import io.github.crazysmc.thrkbs.CustomKeyBinding;

public class ThoroughKeybindHandler extends KeybindHandler {

    @Override
    public int getKeyFromCode(int code) {
        return CustomKeyBinding.getKeyCodeByOriginal(code);
    }
}