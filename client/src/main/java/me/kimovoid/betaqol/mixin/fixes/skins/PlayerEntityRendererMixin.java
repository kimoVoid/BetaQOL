package me.kimovoid.betaqol.mixin.fixes.skins;

import me.kimovoid.betaqol.feature.skinfix.mixininterface.PlayerEntityRendererAccessor;
import me.kimovoid.betaqol.feature.skinfix.PlayerEntityModel;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.model.entity.HumanoidModel;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This is a port of MojangFix for Babric.
 * All credits to js6pak and everyone involved in that project.
 * <a href="https://github.com/js6pak/mojangfix">View here</a>
 */
@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer implements PlayerEntityRendererAccessor {

    @Shadow
    private HumanoidModel handmodel;

    public PlayerEntityRendererMixin(HumanoidModel model, float f) {
        super(model, f);
    }

    public void setThinArms(boolean thinArms) {
        this.model = this.handmodel = new PlayerEntityModel(0.0F, thinArms);
    }

    @Inject(method = "renderPlayerRightHandModel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/ModelPart;render(F)V"))
    private void fixFirstPerson$1(CallbackInfo ci) {
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

    @Inject(method = "renderPlayerRightHandModel", at = @At("RETURN"))
    private void fixFirstPerson$2(CallbackInfo ci) {
        ((PlayerEntityModel) handmodel).rightSleeve.render(0.0625F);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_CULL_FACE);
    }

    @Inject(method = "render(Lnet/minecraft/entity/living/player/PlayerEntity;DDDFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;render(Lnet/minecraft/entity/living/LivingEntity;DDDFF)V"))
    private void fixOuterLayer$1(CallbackInfo ci) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

    @Inject(method = "render(Lnet/minecraft/entity/living/player/PlayerEntity;DDDFF)V", at = @At("RETURN"))
    private void fixOuterLayer$2(CallbackInfo ci) {
        GL11.glDisable(GL11.GL_BLEND);
    }
}
