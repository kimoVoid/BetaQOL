package me.kimovoid.betaqol.feature.networking;

import net.ornithemc.osl.networking.api.CustomPayload;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PlayerInfoPayload implements CustomPayload {

    public String username;
    public boolean isOnline;
    public int ping;

    @Override
    public void read(DataInputStream input) {
        try {
            this.username = this.readString(input, 16);
            this.isOnline = input.readByte() != 0;
            this.ping = input.readShort();
        } catch (IOException ignored) {}
    }

    @Override
    public void write(DataOutputStream output) {
    }

    private String readString(DataInputStream input, int maxLength) throws IOException {
        int n = input.readShort();
        if (n > maxLength) {
            throw new IOException("Received string length longer than maximum allowed (" + n + " > " + maxLength + ")");
        }
        if (n < 0) {
            throw new IOException("Received string length is less than zero! Weird string!");
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < n; ++i) {
            stringBuilder.append(input.readChar());
        }
        return stringBuilder.toString();
    }
}
