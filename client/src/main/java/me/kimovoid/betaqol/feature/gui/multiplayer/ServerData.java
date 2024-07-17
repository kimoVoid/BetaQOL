package me.kimovoid.betaqol.feature.gui.multiplayer;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

import java.util.ArrayList;
import java.util.List;

public class ServerData {

    public String name;
    public String ip;
    public boolean showIp;
    public boolean canPing;
    public long ping;
    public boolean isLoaded;
    public String onlinePlayers;
    public String description;

    public NbtCompound save() {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("name", name);
        nbt.putString("ip", ip);
        nbt.putBoolean("showIp", showIp);
        nbt.putBoolean("ping", canPing);
        return nbt;
    }

    public ServerData(NbtCompound nbt) {
        this.name = nbt.getString("name");
        this.ip = nbt.getString("ip");
        this.showIp = nbt.getBoolean("showIp");
        this.canPing = nbt.getBoolean("ping");
    }

    public ServerData(String name, String ip, boolean showIp, boolean ping) {
        this.name = name;
        this.ip = ip;
        this.showIp = showIp;
        this.canPing = ping;
    }

    public static NbtList save(List<ServerData> servers) {
        NbtList nbt = new NbtList();
        for (ServerData server : servers) {
            nbt.add(server.save());
        }
        return nbt;
    }

    public static List<ServerData> load(NbtList nbt) {
        ArrayList<ServerData> servers = new ArrayList<>();
        for (int i = 0; i < nbt.size(); i++) {
            servers.add(new ServerData((NbtCompound) nbt.get(i)));
        }
        return servers;
    }
}