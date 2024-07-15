package me.kimovoid.betaqol.mixin.fixes.crashslab;

import net.minecraft.block.StoneSlabBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SlabItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Redirect(method = "getTranslationKey", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;getTranslationKey(Lnet/minecraft/item/ItemStack;)Ljava/lang/String;"))
    public String fixCrashSlab(Item instance, ItemStack itemStack) {
        if (instance instanceof SlabItem && itemStack.getMetadata() > 3) {
            return "tile.doubleSlab." + StoneSlabBlock.VARIANTS[itemStack.getMetadata() - 4];
        }
        return instance.getTranslationKey(itemStack);
    }
}