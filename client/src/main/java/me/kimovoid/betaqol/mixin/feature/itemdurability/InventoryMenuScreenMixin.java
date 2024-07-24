package me.kimovoid.betaqol.mixin.feature.itemdurability;

import me.kimovoid.betaqol.feature.itemdurability.ItemDurability;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.menu.InventoryMenuScreen;
import net.minecraft.client.render.TextRenderer;
import net.minecraft.inventory.slot.InventorySlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(InventoryMenuScreen.class)
public abstract class InventoryMenuScreenMixin extends Screen {

    @Shadow protected abstract boolean isMouseOverSlot(InventorySlot invSlot, int mouseX, int mouseY);

    @Unique private InventorySlot hoveredSlot;

    @Inject(method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/inventory/menu/InventoryMenuScreen;isMouseOverSlot(Lnet/minecraft/inventory/slot/InventorySlot;II)Z"),
            locals = LocalCapture.CAPTURE_FAILSOFT)
    private void captureSlot(int mouseX, int mouseY, float tickDelta, CallbackInfo ci, int var4, int var5, Object var6, int var7, InventorySlot var8) {
        if (this.isMouseOverSlot(var8, mouseX, mouseY)) {
            this.hoveredSlot = var8;
        }
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/inventory/menu/InventoryMenuScreen;fillGradient(IIIIII)V", ordinal = 1))
    private void removeBackground(InventoryMenuScreen instance, int x, int y, int x2, int y2, int col, int col2) {
        if (!ItemDurability.debugItemDurability) {
            instance.fillGradient(x, y, x2, y2, col, col2);
        }
    }

    @Redirect(method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/TextRenderer;drawWithShadow(Ljava/lang/String;III)V"
            )
    )
    private void replaceTooltip(TextRenderer instance, String str, int x, int y, int color) {
        if (!ItemDurability.debugItemDurability) {
            instance.drawWithShadow(str, x, y, color);
            return;
        }
        ItemDurability.renderDetailedTooltip(this, this.textRenderer, this.hoveredSlot.getStack(), x, y);
    }
}