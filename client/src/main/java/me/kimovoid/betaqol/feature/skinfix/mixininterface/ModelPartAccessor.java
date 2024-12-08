package me.kimovoid.betaqol.feature.skinfix.mixininterface;

/**
 * This is a port of MojangFix for Babric.
 * All credits to js6pak and everyone involved in that project.
 * <a href="https://github.com/js6pak/mojangfix">View here</a>
 */
public interface ModelPartAccessor {
    int getTextureWidth();

    void setTextureWidth(int textureWidth);

    int getTextureHeight();

    void setTextureHeight(int textureHeight);
}
