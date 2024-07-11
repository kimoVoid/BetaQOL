package me.kimovoid.betaqol.mixin.fixes.entityjitter;

import net.minecraft.entity.Entity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author calmilamsy
 * Ported from <a href="https://github.com/calmilamsy/MPEntityPhysicsFix/blob/master/src/main/java/net/glasslauncher/mpentityphysicsfix/mixin/MixinEntityBase.java">here</a>
 */
@Mixin(Entity.class)
public class EntityMixin {

    @Redirect(method = "move", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;x:D", opcode = 181))
    private void fixX(Entity entity, double value) {
        if (!entity.world.isMultiplayer || entity instanceof PlayerEntity || !(entity instanceof LivingEntity)) {
            entity.x = value;
        }
    }

    @Redirect(method = "move", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;y:D", opcode = 181))
    private void fixY(Entity entity, double value) {
        if (!entity.world.isMultiplayer || entity instanceof PlayerEntity || !(entity instanceof LivingEntity)) {
            entity.y = value;
        }
    }

    @Redirect(method = "move", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;z:D", opcode = 181))
    private void fixZ(Entity entity, double value) {
        if (!entity.world.isMultiplayer || entity instanceof PlayerEntity || !(entity instanceof LivingEntity)) {
            entity.z = value;
        }
    }
}
