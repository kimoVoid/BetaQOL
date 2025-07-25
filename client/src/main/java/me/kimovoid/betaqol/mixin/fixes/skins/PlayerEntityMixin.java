package me.kimovoid.betaqol.mixin.fixes.skins;

import me.kimovoid.betaqol.feature.skinfix.mixininterface.PlayerEntityAccessor;
import me.kimovoid.betaqol.feature.skinfix.SkinService;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements PlayerEntityAccessor {

    @Unique
    private boolean slim;

    public PlayerEntityMixin(World world) {
        super(world);
    }

    @Inject(method = "registerCloak", at = @At("HEAD"), cancellable = true)
    private void cancelRegisterCloak(CallbackInfo ci) {
        ci.cancel();
    }

    @Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/living/player/PlayerEntity;texture:Ljava/lang/String;"))
    private void redirectTexture(PlayerEntity instance, String value) {
        this.setSlim(false);
        this.texture = SkinService.STEVE_TEXTURE;
    }

    @Unique
    public boolean isSlim() {
        return this.slim;
    }

    @Unique
    public void setSlim(boolean slim) {
        this.slim = slim;
        this.texture = slim ? SkinService.ALEX_TEXTURE : SkinService.STEVE_TEXTURE;
    }
}