package me.kimovoid.betaqol.mixin.fixes.crashslab;

import net.minecraft.item.ItemStack;
import net.minecraft.item.SlabItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SlabItem.class)
public class SlabItemMixin {

    @Redirect(method = "getTranslationKey", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getMetadata()I"))
    public int fixCrashSlab(ItemStack instance) {
        if (instance.getMetadata() > 3) {
            return instance.getMetadata() - 4;
        }
        return instance.getMetadata();
    }
}