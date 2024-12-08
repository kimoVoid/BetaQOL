package me.kimovoid.betaqol.mixin.fixes.skins;

import me.kimovoid.betaqol.feature.skinfix.mixininterface.ModelPartAccessor;
import me.kimovoid.betaqol.feature.skinfix.PlayerEntityModel;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.client.render.model.entity.HumanoidModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

/**
 * This is a port of MojangFix for Babric.
 * All credits to js6pak and everyone involved in that project.
 * <a href="https://github.com/js6pak/mojangfix">View here</a>
 */
@Mixin(HumanoidModel.class)
public class HumanoidModelMixin {

    @Redirect(
            method = "<init>(FF)V",
            at = @At(value = "NEW", target = "net/minecraft/client/render/model/ModelPart"),
            slice = @Slice(
                    from = @At(
                            value = "FIELD",
                            target = "Lnet/minecraft/client/render/model/entity/HumanoidModel;deadmau5Ears:Lnet/minecraft/client/render/model/ModelPart;",
                            shift = Shift.AFTER
                    )
            )
    )
    private ModelPart onTexturedQuad(int u, int v) {
        ModelPart modelPart = new ModelPart(u, v);

        HumanoidModel self = (HumanoidModel) (Object) this;
        if (self instanceof PlayerEntityModel) {
            ((ModelPartAccessor)modelPart).setTextureHeight(64);
        }

        return modelPart;
    }
}
