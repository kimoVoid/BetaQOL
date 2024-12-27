package me.kimovoid.betaqol;

import net.minecraft.server.MinecraftServer;

public class BetaQOLServerProperties {

    public boolean rconEnabled;
    public String serverMotd;
    public boolean onePlayerSleep;
    public boolean deathCoordinates;
    public int spawnProtection;

    public BetaQOLServerProperties(MinecraftServer server) {
        this.rconEnabled = server.properties.getBoolean("enable-rcon", false);
        this.serverMotd = server.properties.getString("motd", "A Minecraft Server");
        this.onePlayerSleep = server.properties.getBoolean("one-player-sleep", false);
        this.deathCoordinates = server.properties.getBoolean("death-coordinates", false);
        this.spawnProtection = server.properties.getInt("spawn-protection", 16);
    }
}