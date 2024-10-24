package me.kimovoid.betaqol.mixin.feature.keybinding;

import me.kimovoid.betaqol.BetaQOL;
import me.kimovoid.betaqol.feature.itemdurability.ItemDurability;
import me.kimovoid.betaqol.feature.keybinding.KeybindHandler;
import me.kimovoid.betaqol.feature.skinfix.SkinService;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GameGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.player.PlayerEntity;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Shadow public Screen screen;

    @Shadow public GameGui gui;

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
        int key = Keyboard.getEventKey();

        /* F3 + G (chunk borders) */
        if (key == keybinding.getKeyFromCode(Keyboard.KEY_G) && Keyboard.isKeyDown(keybinding.getKeyFromCode(Keyboard.KEY_F3))) {
            this.addDebugMessage("Chunk borders: " + (BetaQOL.INSTANCE.getChunkBorderRenderer().toggleVisibility() ? "shown" : "hidden"));
        }

        /* F3 + H (durability) */
        if (key == keybinding.getKeyFromCode(Keyboard.KEY_H) && Keyboard.isKeyDown(keybinding.getKeyFromCode(Keyboard.KEY_F3))) {
            ItemDurability.debugItemDurability = !ItemDurability.debugItemDurability;
            this.addDebugMessage("Advanced tooltips: " + (ItemDurability.debugItemDurability ? "shown" : "hidden"));
        }

        /* F3 + R (reload skins) */
        if (key == keybinding.getKeyFromCode(Keyboard.KEY_R) && Keyboard.isKeyDown(keybinding.getKeyFromCode(Keyboard.KEY_F3))) {
            this.addDebugMessage("Reloading skins...");
            SkinService.getInstance().profiles.clear();

            /* World entities */
            if (Minecraft.INSTANCE.world != null) {
                for (Entity e : Minecraft.INSTANCE.world.getEntities()) {
                    if (!(e instanceof PlayerEntity)) continue;
                    PlayerEntity p = ((PlayerEntity)e);
                    p.skin = null;
                    if (!SkinService.getInstance().hasOfCape(p)) {
                        p.cape = p.cloak = null;
                    }
                    SkinService.getInstance().init(p);
                }
            }
        }

        /* F3 + S message */
        if (key == keybinding.getKeyFromCode(Keyboard.KEY_S) && Keyboard.isKeyDown(keybinding.getKeyFromCode(Keyboard.KEY_F3))) {
            this.addDebugMessage("Reloading resources...");
        }
    }

    @Unique
    private void addDebugMessage(String s) {
        this.gui.addChatMessage("§e[DEBUG]§f: " + s);
    }
}
