package me.kimovoid.betaqol.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BowItem.class)
public class BowItemMixin {

    @Redirect(method = "startUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/Entity;Ljava/lang/String;FF)V"))
    private void fixBowSound(World world, Entity entity, String sound, float volume, float pitch) {
        world.doEvent((PlayerEntity) entity, 1002, MathHelper.floor(entity.x), MathHelper.floor(entity.y - (double)entity.height), MathHelper.floor(entity.z), 0);
    }
}