package me.kimovoid.betaqol.mixin.feature.faceview;

import me.kimovoid.betaqol.BetaQOL;
import me.kimovoid.betaqol.feature.faceview.IMinecraft;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.options.GameOptions;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin implements IMinecraft {

    @Unique
    public int thirdPersonMode = 0;

    @Shadow
    public Screen screen;

    @Shadow public GameOptions options;

    @Inject(method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/entity/living/player/InputPlayerEntity;m_3741133(IZ)V",
                    shift = At.Shift.AFTER
            )
    )
    public void handlePerspectiveKey(CallbackInfo ci) {
        if (this.screen != null
                || !Keyboard.getEventKeyState()
                || !BetaQOL.CONFIG.frontPerspective.get()) {
            return;
        }

        if (Keyboard.getEventKey() == BetaQOL.INSTANCE.keybinds.getKeyFromCode(Keyboard.KEY_F5)) {
            this.thirdPersonMode++;
            this.options.debugEnabled = false;
            if (thirdPersonMode > 2) {
                this.thirdPersonMode = 0;
                this.options.debugEnabled = true;
            }
        }
    }

    @Override
    public int getThirdPersonMode() {
        return this.thirdPersonMode;
    }
}