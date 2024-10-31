package me.kimovoid.betaqol.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.living.mob.hostile.HostileEntity;
import net.minecraft.entity.living.mob.hostile.SkeletonEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SkeletonEntity.class)
public abstract class SkeletonEntityMixin extends HostileEntity {

    public SkeletonEntityMixin(World world) {
        super(world);
    }

    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/Entity;Ljava/lang/String;FF)V"))
    private void fixBowSound(World world, Entity entity, String sound, float volume, float pitch) {
        this.world.doEvent(1002, MathHelper.floor(this.x), MathHelper.floor(this.y - (double)this.height), MathHelper.floor(this.z), 0);
    }
}