package me.kimovoid.betaqol.networking;

import me.kimovoid.betaqol.BetaQOL;
import net.minecraft.network.PacketHandler;
import net.minecraft.network.packet.DisconnectPacket;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.ServerProperties;
import net.minecraft.server.network.handler.ServerLoginNetworkHandler;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class ServerPingPacket extends Packet {

    @Override
    public void read(DataInputStream input) {
    }

    @Override
    public void write(DataOutputStream output) {
    }

    @Override
    public void handle(PacketHandler handler) {
        ServerProperties props = BetaQOL.SERVER.properties;
        String s = props.getString("motd", "A Minecraft Ornithe Server")
                + "§" + BetaQOL.SERVER.playerManager.players.size()
                + "§" + props.getInt("max-players", 20);
        ServerLoginNetworkHandler nh = (ServerLoginNetworkHandler)handler;

        nh.connection.send(new DisconnectPacket(s));
        nh.connection.close();
        nh.disconnected = true;
    }

    @Override
    public int getSize() {
        return 0;
    }
}