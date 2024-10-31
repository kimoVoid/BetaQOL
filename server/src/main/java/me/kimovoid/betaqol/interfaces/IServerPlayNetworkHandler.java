package me.kimovoid.betaqol.interfaces;

import net.minecraft.network.packet.KeepAlivePacket;

public interface IServerPlayNetworkHandler {

    void handleKeepAlive(KeepAlivePacket packet);
}