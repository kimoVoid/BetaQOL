package me.kimovoid.betaqol.mixin.fixes.connection;

import me.kimovoid.betaqol.BetaQOL;
import me.kimovoid.betaqol.interfaces.IListenThread;
import me.kimovoid.betaqol.interfaces.IServerPlayNetworkHandler;
import me.kimovoid.betaqol.networking.ServerPingPacket;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketHandler;
import net.minecraft.network.packet.KeepAlivePacket;
import net.minecraft.server.network.handler.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.Socket;
import java.net.SocketException;
import java.util.List;

@SuppressWarnings("unchecked")
@Mixin(Connection.class)
public class ConnectionMixin {

    @Shadow private Socket socket;

    @Shadow private boolean open;

    @Shadow private PacketHandler listener;

    @Redirect(method = "read", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", remap = false))
    private boolean doAsyncPackets(List list, Object obj) {
        if (obj instanceof KeepAlivePacket) {
            ServerPlayNetworkHandler networkHandler = (ServerPlayNetworkHandler) this.listener;
            ((IServerPlayNetworkHandler)networkHandler).handleKeepAlive((KeepAlivePacket) obj);
            return false;
        }
        if (obj instanceof ServerPingPacket) {
            ((ServerPingPacket)obj).handle(this.listener);
            return false;
        }
        return list.add(obj);
    }

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/net/Socket;setSoTimeout(I)V"))
    public void setTcpNoDelay(Socket socket, String name, PacketHandler listener, CallbackInfo ci) {
        try {
            socket.setTcpNoDelay(true);
        } catch (SocketException ignored) {}
    }

    @Inject(method = "disconnect", at = @At("HEAD"))
    public void removeAddress(CallbackInfo ci) {
        if (open) {
            ((IListenThread) BetaQOL.SERVER.connections).close(socket);
        }
    }
}
