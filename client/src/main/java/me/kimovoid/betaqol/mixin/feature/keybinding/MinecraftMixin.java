package me.kimovoid.betaqol.mixin.feature.keybinding;

import me.kimovoid.betaqol.BetaQOL;
import me.kimovoid.betaqol.feature.itemdurability.ItemDurability;
import me.kimovoid.betaqol.feature.keybinding.KeybindHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Shadow public Screen screen;

    @Inject(method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/entity/living/player/InputPlayerEntity;m_3741133(IZ)V",
                    shift = At.Shift.AFTER
            )
    )
    public void handleKeys(CallbackInfo ci) {
        if (this.screen != null || !Keyboard.getEventKeyState()) {
            return;
        }

        KeybindHandler keybinding = BetaQOL.INSTANCE.keybinds;

        /* F3 + G (chunk borders) */
        if (Keyboard.getEventKey() == keybinding.getKeyFromCode(Keyboard.KEY_G)
                && (Keyboard.isKeyDown(keybinding.getKeyFromCode(Keyboard.KEY_F3)))) {
            BetaQOL.INSTANCE.getChunkBorderRenderer().toggleVisibility();
        }

        /* F3 + H (durability) */
        if (Keyboard.getEventKey() == keybinding.getKeyFromCode(Keyboard.KEY_H)
                && (Keyboard.isKeyDown(keybinding.getKeyFromCode(Keyboard.KEY_F3)))) {
            ItemDurability.debugItemDurability = !ItemDurability.debugItemDurability;
        }
    }
}
