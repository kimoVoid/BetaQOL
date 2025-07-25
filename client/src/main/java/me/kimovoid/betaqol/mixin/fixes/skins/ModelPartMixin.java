package me.kimovoid.betaqol.mixin.fixes.skins;

import me.kimovoid.betaqol.feature.skinfix.mixininterface.ModelPartAccessor;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.client.render.model.Quad;
import net.minecraft.client.render.model.Vertex;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ModelPart.class)
public abstract class ModelPartMixin implements ModelPartAccessor {

    @Unique private int textureWidth = 64;
    @Unique private int textureHeight = 32;

    @Redirect(method = "addBox(FFFIIIF)V", at = @At(value = "NEW", target = "([Lnet/minecraft/client/render/model/Vertex;IIII)Lnet/minecraft/client/render/model/Quad;"))
    private Quad redirectQuad(Vertex[] vertexs, int i, int j, int k, int l) {
        Quad quad = new Quad(vertexs);

        vertexs[0] = vertexs[0].withTextureCoords((float) k / textureWidth, (float) j / textureHeight);
        vertexs[1] = vertexs[1].withTextureCoords((float) i / textureWidth, (float) j / textureHeight);
        vertexs[2] = vertexs[2].withTextureCoords((float) i / textureWidth, (float) l / textureHeight);
        vertexs[3] = vertexs[3].withTextureCoords((float) k / textureWidth, (float) l / textureHeight);

        return quad;
    }

    @Override
    public int getTextureWidth() {
        return this.textureHeight;
    }

    @Override
    public void setTextureWidth(int textureWidth) {
        this.textureWidth = textureWidth;
    }

    @Override
    public int getTextureHeight() {
        return this.textureHeight;
    }

    @Override
    public void setTextureHeight(int textureHeight) {
        this.textureHeight = textureHeight;
    }
}