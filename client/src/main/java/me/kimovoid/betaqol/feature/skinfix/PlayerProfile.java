package me.kimovoid.betaqol.feature.skinfix;

import com.github.steveice10.mc.auth.data.GameProfile;

import java.time.Instant;

/*
 * This is a port of MojangFix for Babric.
 * All credits to js6pak and everyone involved in that project.
 * https://github.com/js6pak/mojangfix
 */
public class PlayerProfile {
    private String skinUrl;
    private String capeUrl;
    private GameProfile.TextureModel model;
    private Instant lastFetched = Instant.now();

    public PlayerProfile(String skinUrl, String capeUrl, GameProfile.TextureModel model) {
        this.skinUrl = skinUrl;
        this.capeUrl = capeUrl;
        this.model = model;
    }

    public GameProfile.TextureModel getModel() {
        return this.model;
    }

    public String getSkinUrl() {
        return this.skinUrl;
    }

    public String getCapeUrl() {
        return this.capeUrl;
    }
}
