package me.kimovoid.betaqol.feature.keybind;

import me.kimovoid.betaqol.BetaQOL;
import net.minecraft.client.options.KeyBinding;

public class KeybindHandler {

    public int getKeyFromCode(int code) {
        return code;
    }

    /* Returns -1 if key wasn't found */
    public int getKeyByName(String name) {
        int code = -1;
        for (KeyBinding key : BetaQOL.mc.options.keyBindings) {
            if (key.name.equals(name)) code = key.keyCode;
        }
        return code;
    }
}