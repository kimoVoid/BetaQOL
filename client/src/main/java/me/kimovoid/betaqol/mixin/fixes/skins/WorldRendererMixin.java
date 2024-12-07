package me.kimovoid.betaqol.mixin.fixes.skins;

import me.kimovoid.betaqol.feature.skinfix.interfaces.PlayerEntityAccessor;
import me.kimovoid.betaqol.feature.skinfix.interfaces.SkinImageProcessorAccessor;
import me.kimovoid.betaqol.feature.skinfix.CapeImageProcessor;
import net.minecraft.client.entity.living.player.InputPlayerEntity;
import net.minecraft.client.render.texture.HttpImageProcessor;
import net.minecraft.client.render.world.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This is a port of MojangFix for Babric.
 * All credits to js6pak and everyone involved in that project.
 * <a href="https://github.com/js6pak/mojangfix">View here</a>
 */
@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    @Inject(method = "onEntityRemoved", at = @At("HEAD"), cancellable = true)
    private void dontUnloadLocalPlayerSkin(Entity entity, CallbackInfo ci) {
        if (entity instanceof InputPlayerEntity) {
            ci.cancel();
        }
    }

    @Unique
    private Entity currentEntity; // I hate this but there is no way to get it from @ModifyArg

    @Inject(method = "onEntityAdded", at = @At("HEAD"))
    private void getEntity(Entity entity, CallbackInfo ci) {
        currentEntity = entity;
    }

    @ModifyArg(
        method = "onEntityAdded", index = 1,
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/texture/TextureManager;getHttpTexture(Ljava/lang/String;Lnet/minecraft/client/render/texture/HttpImageProcessor;)Lnet/minecraft/client/render/texture/HttpTexture;",ordinal = 0)
    )
    private HttpImageProcessor redirectSkinProcessor(HttpImageProcessor processor) {
        if (currentEntity instanceof PlayerEntity) {
            PlayerEntityAccessor playerEntityAccessor = (PlayerEntityAccessor) currentEntity;
            SkinImageProcessorAccessor skinImageProcessorAccessor = (SkinImageProcessorAccessor) processor;
            skinImageProcessorAccessor.setSlim(playerEntityAccessor.isSlim());
        }
        return processor;
    }

    @ModifyArg(
        method = "onEntityAdded", index = 1,
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/texture/TextureManager;getHttpTexture(Ljava/lang/String;Lnet/minecraft/client/render/texture/HttpImageProcessor;)Lnet/minecraft/client/render/texture/HttpTexture;", ordinal = 1)
    )
    private HttpImageProcessor redirectCapeProcessor(HttpImageProcessor processor) {
        return new CapeImageProcessor();
    }
}
