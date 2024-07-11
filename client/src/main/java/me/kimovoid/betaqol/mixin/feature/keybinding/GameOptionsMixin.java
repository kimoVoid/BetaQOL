package me.kimovoid.betaqol.mixin.feature.keybinding;

import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
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
        //this.keyBindings = append(this.keyBindings, BetaQOL.INSTANCE.registerKeys());
    }

    @Unique
    private <T> T[] append(T[] array, T[] newArray) {
        int length = array.length;
        array = Arrays.copyOf(array, length + newArray.length);
        System.arraycopy(newArray, 0, array, length, newArray.length);
        return array;
    }
}
