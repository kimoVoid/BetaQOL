package me.kimovoid.betaqol.networking;

import net.ornithemc.osl.networking.api.CustomPayload;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PlayerInfoPayload implements CustomPayload {

    public String username;
    public boolean isOnline;
    public int ping;

    public PlayerInfoPayload(String name, boolean isConnected, int ping) {
        this.username = name;
        this.isOnline = isConnected;
        this.ping = ping;
    }

    @Override
    public void read(DataInputStream input) {
    }

    @Override
    public void write(DataOutputStream output) {
        try {
            writeString(this.username, output);
            output.writeByte(this.isOnline ? 1 : 0);
            output.writeShort(this.ping);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeString(String s, DataOutputStream output) throws IOException {
        if (s.length() > Short.MAX_VALUE) {
            throw new IOException("String too big");
        }
        output.writeShort(s.length());
        output.writeChars(s);
    }
}
