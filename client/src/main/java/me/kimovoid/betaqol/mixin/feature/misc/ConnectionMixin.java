package me.kimovoid.betaqol.mixin.feature.misc;

import net.minecraft.network.Connection;
import net.minecraft.network.PacketHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PingHostPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Connection.class)
public abstract class ConnectionMixin {

    @Shadow public abstract void send(Packet packet);

    @Redirect(method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/packet/Packet;handle(Lnet/minecraft/network/PacketHandler;)V"
            )
    )
    private void handleKeepAlive(Packet packet, PacketHandler packetHandler) {
        if (packet instanceof PingHostPacket) {
            this.send(new PingHostPacket());
            return;
        }
        packet.handle(packetHandler);
    }
}