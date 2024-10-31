package me.kimovoid.betaqol.mixin.fixes.connection;

import me.kimovoid.betaqol.BetaQOL;
import me.kimovoid.betaqol.interfaces.IListenThread;
import me.kimovoid.betaqol.interfaces.IServerPlayNetworkHandler;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketHandler;
import net.minecraft.network.packet.KeepAlivePacket;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.handler.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.Socket;
import java.net.SocketException;

@Mixin(Connection.class)
public class ConnectionMixin {

    @Shadow private Socket socket;

    @Shadow private boolean open;

    @Redirect(
            method = "tick()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/packet/Packet;handle(Lnet/minecraft/network/PacketHandler;)V"
            )
    )
    public void handlePacket(Packet packet, PacketHandler handler) {
        if (packet instanceof KeepAlivePacket && handler instanceof ServerPlayNetworkHandler) {
            ServerPlayNetworkHandler networkHandler = (ServerPlayNetworkHandler) handler;
            ((IServerPlayNetworkHandler)networkHandler).handleKeepAlive((KeepAlivePacket) packet);
            return;
        }
        packet.handle(handler);
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
            ((IListenThread) BetaQOL.server.connections).close(socket);
        }
    }

    @ModifyConstant(method = "tick", constant = @Constant(intValue = 1200))
    private int setTimeoutTime(int time) {
        return time * 50;
    }
}
