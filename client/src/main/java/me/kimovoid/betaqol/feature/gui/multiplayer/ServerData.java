package me.kimovoid.betaqol.feature.gui.multiplayer;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a port of MojangFix for Babric.
 * All credits to js6pak and everyone involved in that project.
 * <a href="https://github.com/js6pak/mojangfix">View here</a>
 */
public class ServerData {

    private String name;
    private String ip;
    private boolean showIp;

    public NbtCompound save() {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("name", name);
        nbt.putString("ip", ip);
        nbt.putBoolean("showIp", showIp);
        return nbt;
    }

    public ServerData(NbtCompound nbt) {
        this.name = nbt.getString("name");
        this.ip = nbt.getString("ip");
        this.showIp = nbt.getBoolean("showIp");
    }

    public ServerData(String name, String ip, boolean showIp) {
        this.name = name;
        this.ip = ip;
        this.showIp = showIp;
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

    public String getName() {
        return this.name;
    }

    public String getIp() {
        return this.ip;
    }

    public boolean isShowIp() {
        return this.showIp;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setShowIp(boolean b) {
        this.showIp = b;
    }
}