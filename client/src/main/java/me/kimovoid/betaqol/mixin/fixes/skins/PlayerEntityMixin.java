package me.kimovoid.betaqol.mixin.fixes.skins;

import com.github.steveice10.mc.auth.data.GameProfile;
import me.kimovoid.betaqol.feature.skinfix.interfaces.PlayerEntityAccessor;
import me.kimovoid.betaqol.feature.skinfix.SkinService;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This is a port of MojangFix for Babric.
 * All credits to js6pak and everyone involved in that project.
 * <a href="https://github.com/js6pak/mojangfix">View here</a>
 */
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements PlayerEntityAccessor {

    @Unique
    private GameProfile.TextureModel textureModel;

    public PlayerEntityMixin(World world) {
        super(world);
    }

    @Inject(method = "registerCloak", at = @At("HEAD"), cancellable = true)
    private void cancelUpdateCapeUrl(CallbackInfo ci) {
        ci.cancel();
    }

    @Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/living/player/PlayerEntity;texture:Ljava/lang/String;"))
    private void redirectTexture(PlayerEntity instance, String value) {
        this.setTextureModel(GameProfile.TextureModel.NORMAL);
    }

    @Unique
    public GameProfile.TextureModel getTextureModel() {
        return this.textureModel;
    }

    @Unique
    public void setTextureModel(GameProfile.TextureModel textureModel) {
        this.textureModel = textureModel;
        this.texture = textureModel == GameProfile.TextureModel.NORMAL ? SkinService.STEVE_TEXTURE : SkinService.ALEX_TEXTURE;
    }
}