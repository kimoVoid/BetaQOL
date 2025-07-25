package me.kimovoid.betaqol.feature.skinfix;

import me.kimovoid.betaqol.BetaQOL;
import me.kimovoid.betaqol.event.BetaQOLEvents;
import me.kimovoid.betaqol.feature.skinfix.mixininterface.PlayerEntityAccessor;
import me.kimovoid.betaqol.feature.skinfix.model.ModernPlayerEntityRenderer;
import me.kimovoid.betaqol.feature.skinfix.provider.BedrockProfileProvider;
import me.kimovoid.betaqol.feature.skinfix.provider.MinetoolsProfileProvider;
import me.kimovoid.betaqol.feature.skinfix.provider.MojangProfileProvider;
import me.kimovoid.betaqol.feature.skinfix.provider.ProfileProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.living.player.PlayerEntity;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

public class SkinService {

    public static final String STEVE_TEXTURE = "/assets/betaqol/mob/steve.png";
    public static final String ALEX_TEXTURE = "/assets/betaqol/mob/alex.png";
    public static final ModernPlayerEntityRenderer STEVE_MODEL = new ModernPlayerEntityRenderer(EntityRenderDispatcher.INSTANCE, false);
    public static final ModernPlayerEntityRenderer ALEX_MODEL = new ModernPlayerEntityRenderer(EntityRenderDispatcher.INSTANCE, true);

    public static final SkinService INSTANCE = new SkinService();

    private final ConcurrentMap<String, ReentrantLock> locks = new ConcurrentHashMap<>();
    public final Map<String, PlayerProfile> profiles = new HashMap<>();

    private boolean isSlim(UUID uuid) {
        return (uuid.hashCode() & 1) != 0;
    }

    private void updatePlayer(PlayerEntity player, PlayerProfile playerProfile) {
        if (playerProfile == null) return;

        String skin = playerProfile.getSkinUrl();
        String cape = playerProfile.getCapeUrl();
        boolean slim = playerProfile.isSlim();

        /* Events system so other mods can customize skins/capes */
        BetaQOLEvents.LoadSkinEvent event = new BetaQOLEvents.LoadSkinEvent(player, skin, cape, slim);
        BetaQOLEvents.LOAD_SKIN.invoker().accept(event);

        skin = event.getSkinUrl();
        cape = event.getCapeUrl();
        slim = event.isSlim();

        PlayerEntityAccessor accessor = (PlayerEntityAccessor) player;
        accessor.setSlim(slim);
        player.skin = skin;
        if (player.cloak == null || player.cape == null) {
            player.cloak = player.cape = cape;
        }

        Minecraft.INSTANCE.worldRenderer.onEntityAdded(player);
    }

    private boolean updatePlayer(PlayerEntity player) {
        if (profiles.containsKey(player.name)) {
            PlayerProfile profile = profiles.get(player.name);
            updatePlayer(player, profile);
            return true;
        }

        return false;
    }

    private void initOffline(PlayerEntity player) {
        UUID uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + player.name).getBytes(StandardCharsets.UTF_8));
        PlayerEntityAccessor accessor = (PlayerEntityAccessor) player;
        final boolean slim = isSlim(uuid);
        accessor.setSlim(slim);
    }

    public void init(PlayerEntity player) {
        if (updatePlayer(player)) return;

        initOffline(player);

        (new Thread(() -> {
            initSimple(player.name);
            updatePlayer(player);
        })).start();
    }

    public void initSimple(String name) {
        if (profiles.containsKey(name)) return;

        ReentrantLock lock;
        if (locks.containsKey(name)) {
            lock = locks.get(name);
        } else {
            locks.put(name, lock = new ReentrantLock());
        }

        lock.lock();

        try {
            if (profiles.containsKey(name)) return;

            PlayerProfile prof = null;

            /* Bedrock provider */
            if (name.startsWith(".")) {
                try {
                    BedrockProfileProvider provider = new BedrockProfileProvider();
                    prof = provider.getProfile(name).get();
                } catch (Exception ignored) {
                    BetaQOL.LOGGER.warn("Failed to fetch bedrock profile {}", name);
                }
            }

            /* Default providers */
            else {
                ProfileProvider[] javaProviders = new ProfileProvider[]{new MojangProfileProvider(), new MinetoolsProfileProvider()};
                for (ProfileProvider provider : javaProviders) {
                    try {
                        prof = provider.getProfile("kimoVoid").get();
                        break;
                    } catch (Exception ignored) {
                        BetaQOL.LOGGER.warn("Failed to fetch profile {} with provider {}", name, provider.getClass().getSimpleName());
                    }
                }
            }

            this.profiles.put(name, prof);
        } finally {
            lock.unlock();
        }
    }

    public boolean hasVanillaCape(PlayerEntity player) {
        return (player.cloak != null && player.cloak.contains("textures.minecraft.net"))
                || (player.cape != null && player.cape.contains("textures.minecraft.net"));
    }
}