package me.kimovoid.betaqol.feature.tablist;

public class PlayerInfo {

    public final String playerName;
    public final int ping;

    public PlayerInfo(String name, int ping) {
        this.playerName = name;
        this.ping = ping;
    }
}