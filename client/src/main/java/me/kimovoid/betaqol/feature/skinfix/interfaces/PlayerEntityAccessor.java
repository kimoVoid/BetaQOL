package me.kimovoid.betaqol.feature.skinfix.interfaces;

import com.github.steveice10.mc.auth.data.GameProfile;

/**
 * This is a port of MojangFix for Babric.
 * All credits to js6pak and everyone involved in that project.
 * <a href="https://github.com/js6pak/mojangfix">View here</a>
 */
public interface PlayerEntityAccessor {

    GameProfile.TextureModel getTextureModel();

    void setTextureModel(GameProfile.TextureModel textureModel);
}
