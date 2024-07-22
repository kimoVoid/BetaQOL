package me.kimovoid.betaqol.mixin.fixes.leggingsriding;

import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.model.Model;
import net.minecraft.client.render.model.entity.HumanoidModel;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<PlayerEntity> {

    @Shadow private HumanoidModel model2;

    public PlayerEntityRendererMixin(Model model, float shadowSize) {
        super(model, shadowSize);
    }

    @Inject(method = "render(Lnet/minecraft/entity/living/player/PlayerEntity;DDDFF)V", at = @At("HEAD"))
    private void fixLeggingsModel(PlayerEntity pl, double d, double e, double f, float g, float h, CallbackInfo ci) {
        ItemStack stack = pl.inventory.armorSlots[1];
        if (stack != null && stack.getItem() instanceof ArmorItem) {
            this.model2.hasVehicle = this.model.hasVehicle;
        }
    }
}
