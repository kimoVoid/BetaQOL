package me.kimovoid.betaqol.mixin.fixes.skins;

import me.kimovoid.betaqol.feature.skinfix.interfaces.PlayerEntityAccessor;
import me.kimovoid.betaqol.feature.skinfix.interfaces.PlayerEntityRendererAccessor;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

/**
 * This is a port of MojangFix for Babric.
 * All credits to js6pak and everyone involved in that project.
 * <a href="https://github.com/js6pak/mojangfix">View here</a>
 */
@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {

    @Shadow private Map<Class<? extends Entity>, EntityRenderer> renderers;
    @Unique private PlayerEntityRenderer steveRenderer;
    @Unique private PlayerEntityRenderer alexRenderer;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        PlayerEntityRenderer renderer = new PlayerEntityRenderer();
        ((PlayerEntityRendererAccessor)renderer).setThinArms(false);
        renderer.setEntityRenderDispatcher((EntityRenderDispatcher) (Object) this);
        this.steveRenderer = renderer;

        renderer = new PlayerEntityRenderer();
        ((PlayerEntityRendererAccessor)renderer).setThinArms(true);
        renderer.setEntityRenderDispatcher((EntityRenderDispatcher) (Object) this);
        this.alexRenderer = renderer;

        this.renderers.put(PlayerEntity.class, steveRenderer);
    }

    @Inject(method = "getRenderer(Lnet/minecraft/entity/Entity;)Lnet/minecraft/client/render/entity/EntityRenderer;", at = @At("HEAD"), cancellable = true)
    private void onGet(Entity entity, CallbackInfoReturnable<EntityRenderer> cir) {
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            cir.setReturnValue(((PlayerEntityAccessor) player).isSlim() ? this.alexRenderer : this.steveRenderer);
        }
    }
}
