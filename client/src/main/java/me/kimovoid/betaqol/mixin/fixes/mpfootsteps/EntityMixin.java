package me.kimovoid.betaqol.mixin.fixes.mpfootsteps;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Entity.class)
public class EntityMixin {

    @Shadow public World world;
    @Shadow public double x;
    @Shadow public double prevX;
    @Shadow public double z;
    @Shadow public double prevZ;

    @Shadow private int distanceOnNextBlock;
    @Shadow public float horizontalVelocity;

    @ModifyArg(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;sqrt(D)F"))
    private double fixMultiplayerVelocity(double orig) {
        if (this.world.isMultiplayer) {
            double x = this.x - this.prevX;
            double z = this.z - this.prevZ;
            return x * x + z * z;
        }
        return orig;
    }

    @Redirect(method = "move", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;distanceOnNextBlock:I", ordinal = 2))
    private void fixDistance(Entity instance, int value) {
        this.distanceOnNextBlock = MathHelper.floor(this.horizontalVelocity) + 1;
    }
}