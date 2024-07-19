package me.kimovoid.betaqol.mixin.fixes.connection;

import net.minecraft.network.Connection;
import net.minecraft.network.PacketHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PingHostPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.Socket;
import java.net.SocketException;
import java.util.List;

@Mixin(Connection.class)
public abstract class ConnectionMixin {

    @Shadow public abstract void send(Packet packet);

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/net/Socket;setTrafficClass(I)V", shift = At.Shift.AFTER))
    private void enableTcpNoDelay(Socket socket, String address, PacketHandler handler, CallbackInfo ci) {
        try {
            socket.setTcpNoDelay(true);
        } catch (SocketException ignored) {
        }
    }

    @Redirect(method = "read", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
    private boolean instantReadPing(List<Packet> queue, Object p) {
        if (p instanceof PingHostPacket) {
            this.send(new PingHostPacket());
            return true;
        }
        return queue.add((Packet)p);
    }
}