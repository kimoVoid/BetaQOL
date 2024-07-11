package me.kimovoid.betaqol.mixin.fixes.skins;

import me.kimovoid.betaqol.feature.skinfix.interfaces.ModelPartAccessor;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.client.render.model.Quad;
import net.minecraft.client.render.model.Vertex;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * This is a port of MojangFix for Babric.
 * All credits to js6pak and everyone involved in that project.
 * <a href="https://github.com/js6pak/mojangfix">View here</a>
 */
@Mixin(ModelPart.class)
public abstract class ModelPartMixin implements ModelPartAccessor {

    @Unique
    private int textureWidth = 64;

    @Unique
    private int textureHeight = 32;

    @Redirect(method = "addBox(FFFIIIF)V", at = @At(value = "NEW", target = "([Lnet/minecraft/client/render/model/Vertex;IIII)Lnet/minecraft/client/render/model/Quad;"))
    private Quad redirectQuad(Vertex[] vertices, int u1, int v1, int u2, int v2) {
        Quad quad = new Quad(vertices);

        vertices[0] = vertices[0].withTextureCoords((float) u2 / textureWidth, (float) v1 / textureHeight);
        vertices[1] = vertices[1].withTextureCoords((float) u1 / textureWidth, (float) v1 / textureHeight);
        vertices[2] = vertices[2].withTextureCoords((float) u1 / textureWidth, (float) v2 / textureHeight);
        vertices[3] = vertices[3].withTextureCoords((float) u2 / textureWidth, (float) v2 / textureHeight);

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
