package me.kimovoid.betaqol.feature.skinfix;

import java.time.Instant;

public class PlayerProfile {
    private String skinUrl;
    private String capeUrl;
    private boolean slim;
    private Instant lastFetched = Instant.now();

    public PlayerProfile(String skinUrl, String capeUrl, boolean slim) {
        this.skinUrl = skinUrl;
        this.capeUrl = capeUrl;
        this.slim = slim;
    }

    public boolean isSlim() {
        return this.slim;
    }

    public String getSkinUrl() {
        return this.skinUrl;
    }

    public String getCapeUrl() {
        return this.capeUrl;
    }
}
