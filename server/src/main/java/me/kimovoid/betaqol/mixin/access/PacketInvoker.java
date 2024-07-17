package me.kimovoid.betaqol.mixin.access;

import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Packet.class)
public interface PacketInvoker {

    @Invoker("register")
    public static void register(int id, boolean s2c, boolean c2s, Class type) {
        throw new AssertionError();
    }
}