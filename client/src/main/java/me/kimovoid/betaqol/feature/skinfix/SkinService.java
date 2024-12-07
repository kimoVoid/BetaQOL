package me.kimovoid.betaqol.feature.skinfix;

import me.kimovoid.betaqol.BetaQOL;
import me.kimovoid.betaqol.feature.skinfix.interfaces.PlayerEntityAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.living.player.PlayerEntity;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This is a port of MojangFix for Babric.
 * All credits to js6pak and everyone involved in that project.
 * <a href="https://github.com/js6pak/mojangfix">View here</a>
 */
public class SkinService {
    public static final String STEVE_TEXTURE = "/assets/betaqol/mob/steve.png";
    public static final String ALEX_TEXTURE = "/assets/betaqol/mob/alex.png";

    private static final SkinService INSTANCE = new SkinService();

    public static SkinService getInstance() {
        return INSTANCE;
    }

    private final ConcurrentMap<String, ReentrantLock> locks = new ConcurrentHashMap<>();
    public final Map<String, PlayerProfile> profiles = new HashMap<>();

    private boolean isSlim(UUID uuid) {
        return (uuid.hashCode() & 1) != 0;
    }

    private void updatePlayer(PlayerEntity player, PlayerProfile playerProfile) {
        if (playerProfile == null) return;

        PlayerEntityAccessor accessor = (PlayerEntityAccessor) player;
        accessor.setSlim(playerProfile.isSlim());
        player.skin = playerProfile.getSkinUrl();
        if (!this.hasOfCape(player)) {
            player.cloak = player.cape = playerProfile.getCapeUrl();
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

            PlayerProfile prof;

            try {
                prof = new ProfileProvider().getProfile(name).get();
            } catch (Exception e) {
                BetaQOL.LOGGER.warn("Lookup for profile {} failed!", name);
                //BetaQOL.LOGGER.warn(e.getMessage());
                return;
            }

            this.profiles.put(name, prof);
        } finally {
            lock.unlock();
        }
    }

    public boolean hasOfCape(PlayerEntity player) {
        return (player.cloak != null && player.cloak.contains("optifine.net/capes/"))
                || (player.cape != null && player.cape.contains("optifine.net/capes/"));
    }
}