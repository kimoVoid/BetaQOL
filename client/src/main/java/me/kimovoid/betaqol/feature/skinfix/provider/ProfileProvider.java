package me.kimovoid.betaqol.feature.skinfix.provider;

import me.kimovoid.betaqol.feature.skinfix.PlayerProfile;
import java.util.concurrent.Future;

public interface ProfileProvider {
    Future<PlayerProfile> getProfile(String username);
}