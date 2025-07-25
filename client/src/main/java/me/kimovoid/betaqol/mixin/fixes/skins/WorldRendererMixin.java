package me.kimovoid.betaqol.mixin.fixes.skins;

import me.kimovoid.betaqol.feature.skinfix.CapeImageProcessor;
import net.minecraft.client.render.texture.HttpImageProcessor;
import net.minecraft.client.render.world.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    @ModifyArg(
        method = "onEntityAdded", index = 1,
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/texture/TextureManager;getHttpTexture(Ljava/lang/String;Lnet/minecraft/client/render/texture/HttpImageProcessor;)Lnet/minecraft/client/render/texture/HttpTexture;", ordinal = 1)
    )
    private HttpImageProcessor redirectCapeProcessor(HttpImageProcessor processor) {
        return new CapeImageProcessor();
    }
}