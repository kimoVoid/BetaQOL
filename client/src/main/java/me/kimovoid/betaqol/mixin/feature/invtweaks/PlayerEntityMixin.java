package me.kimovoid.betaqol.mixin.feature.invtweaks;

import me.kimovoid.betaqol.BetaQOL;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.living.player.LocalPlayerEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This is a port of InventoryTweaks for Babric.
 * All credits to telvarost and everyone involved in that project.
 * <a href="https://github.com/telvarost/InventoryTweaks-StationAPI">View here</a>
 */
@Mixin({LocalPlayerEntity.class, PlayerEntity.class})
public class PlayerEntityMixin {

    @Inject(method = "dropItem()V", at = @At("HEAD"), cancellable = true)
    private void inventoryTweaks_dropSelectedItem(CallbackInfo ci) {
        if (!BetaQOL.CONFIG.ctrlDropStack.get()) {
            return;
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
            Minecraft.INSTANCE.interactionManager.clickSlot(0, 36 + Minecraft.INSTANCE.player.inventory.selectedSlot, 0, false, Minecraft.INSTANCE.player);
            Minecraft.INSTANCE.interactionManager.clickSlot(0, -999, 0, false, Minecraft.INSTANCE.player);
            ci.cancel();
        }
    }
}
