package me.kimovoid.betaqol.feature.skinfix;

import com.github.steveice10.mc.auth.data.GameProfile;
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
    private final Map<String, PlayerProfile> profiles = new HashMap<>();

    private static GameProfile.TextureModel getTextureModelForUUID(UUID uuid) {
        return (uuid.hashCode() & 1) != 0 ? GameProfile.TextureModel.SLIM : GameProfile.TextureModel.NORMAL;
    }

    private void updatePlayer(PlayerEntity player, PlayerProfile playerProfile) {
        if (playerProfile == null) return;

        PlayerEntityAccessor accessor = (PlayerEntityAccessor) player;
        accessor.setTextureModel(playerProfile.getModel());
        player.skin = playerProfile.getSkinUrl();
        player.cloak = player.cape = playerProfile.getCapeUrl();
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
        final GameProfile.TextureModel model = getTextureModelForUUID(uuid);
        accessor.setTextureModel(model);
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
                return;
            }

            this.profiles.put(name, prof);
        } finally {
            lock.unlock();
        }
    }
}