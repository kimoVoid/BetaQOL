package me.kimovoid.betaqol.event;

import net.minecraft.entity.living.player.PlayerEntity;
import net.ornithemc.osl.core.api.events.Event;

import java.util.function.Consumer;

public final class BetaQOLEvents {

    /**
     * Invoked when a player's skin is reloaded manually.
     */
    public static final Event<Consumer<PlayerEntity>> RELOAD_SKIN = Event.consumer();

    /**
     * Invoked when a player's skin is loaded for the first time.
     */
    public static final Event<Consumer<LoadSkinEvent>> LOAD_SKIN = Event.consumer();

    public static class LoadSkinEvent {
        private final PlayerEntity player;
        private String skinUrl;
        private String capeUrl;
        private boolean slim;

        public LoadSkinEvent(PlayerEntity player, String skinUrl, String capeUrl, boolean slim) {
            this.player = player;
            this.skinUrl = skinUrl;
            this.capeUrl = capeUrl;
            this.slim = slim;
        }

        public PlayerEntity getPlayer() {
            return this.player;
        }

        public String getSkinUrl() {
            return this.skinUrl;
        }

        public String getCapeUrl() {
            return this.capeUrl;
        }

        public boolean isSlim() {
            return this.slim;
        }

        public void setSkinUrl(String url) {
            this.skinUrl = url;
        }

        public void setCapeUrl(String url) {
            this.capeUrl = url;
        }

        public void setSlim(boolean slim) {
            this.slim = slim;
        }
    }
}