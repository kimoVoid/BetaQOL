package me.kimovoid.betaqol.mixin.fixes.ghast;

import net.minecraft.entity.Entity;
import net.minecraft.entity.living.mob.FlyingEntity;
import net.minecraft.entity.living.mob.GhastEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GhastEntity.class)
public abstract class GhastEntityMixin extends FlyingEntity {

    public GhastEntityMixin(World world) {
        super(world);
    }

    @Redirect(method = "tickDespawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/living/mob/GhastEntity;canSee(Lnet/minecraft/entity/Entity;)Z"))
    private boolean fixGhasts(GhastEntity instance, Entity entity) {
        return this.canSee(entity) && !this.isInWall();
    }
}