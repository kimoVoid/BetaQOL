package me.kimovoid.betaqol.mixin.fixes.mpfootsteps;

import net.minecraft.block.Block;
import net.minecraft.client.entity.living.player.RemotePlayerEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RemotePlayerEntity.class)
public abstract class RemotePlayerEntityMixin extends PlayerEntity {

    @Unique private int distanceOnNextBlock2;

    public RemotePlayerEntityMixin(World world) {
        super(world);
    }

    @Inject(
            method = "tickAi",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/entity/living/player/RemotePlayerEntity;prevStrideDistance:F",
                    ordinal = 0
            )
    )
    private void fixRemotePlayerFootsteps(CallbackInfo ci) {
        boolean sneaking = this.isSneaking();
        double veloX = this.x - this.prevX;
        double veloZ = this.z - this.prevZ;

        if (this.canClimb() && !sneaking && this.vehicle == null) {
            this.horizontalVelocity = (float)((double)this.horizontalVelocity + (double)MathHelper.sqrt(veloX * veloX + veloZ * veloZ) * 0.6D);
            int x = MathHelper.floor(this.x);
            int y = MathHelper.floor(this.y - (double)0.2F - (double)this.eyeHeight);
            int z = MathHelper.floor(this.z);
            int blockId = this.world.getBlock(x, y, z);
            if (this.world.getBlock(x, y - 1, z) == Block.FENCE.id) {
                blockId = this.world.getBlock(x, y - 1, z);
            }

            if (this.horizontalVelocity > (float)this.distanceOnNextBlock2 && blockId > 0) {
                this.distanceOnNextBlock2 = MathHelper.floor(this.horizontalVelocity) + 1;
                Block.Sound sound = Block.BY_ID[blockId].sound;
                if (this.world.getBlock(x, y + 1, z) == Block.SNOW_LAYER.id) {
                    sound = Block.SNOW_LAYER.sound;
                    this.world.playSound(this, sound.getStepSound(), sound.getVolume() * 0.15F, sound.getPitch());
                } else if (!Block.BY_ID[blockId].material.isLiquid()) {
                    this.world.playSound(this, sound.getStepSound(), sound.getVolume() * 0.15F, sound.getPitch());
                }

                Block.BY_ID[blockId].onSteppedOn(this.world, x, y, z, this);
            }
        }
    }
}