package me.kimovoid.betaqol.mixin.feature.chat;

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.SleepingChatScreen;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SleepingChatScreen.class)
public class SleepingChatScreenMixin extends ChatScreen {

    @Redirect(method = "init",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/input/Keyboard;enableRepeatEvents(Z)V",
                    remap = false
            )
    )
    private void redirectEnableInput(boolean enable) {
        super.init();
    }

    @Redirect(method = "removed",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/input/Keyboard;enableRepeatEvents(Z)V",
                    remap = false
            )
    )
    private void redirectDisableInput(boolean enable) {
        super.removed();
    }

    @ModifyConstant(method = "keyPressed", constant = @Constant(intValue = Keyboard.KEY_RETURN))
    private int ignoreEnter(int def) {
        return -1;
    }
}