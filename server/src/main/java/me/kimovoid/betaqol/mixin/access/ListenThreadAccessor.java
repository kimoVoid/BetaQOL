package me.kimovoid.betaqol.mixin.access;

import net.minecraft.server.network.ListenThread;
import net.minecraft.server.network.handler.ServerLoginNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.net.ServerSocket;
import java.util.ArrayList;

@Mixin(ListenThread.class)
public interface ListenThreadAccessor {


    @Accessor("socket")
    public ServerSocket getSocket();

    @Accessor("connectionCounter")
    public int getConnectionCounter();

    @Accessor("connectionCounter")
    public void setConnectionCounter(int i);

    @Accessor("pendingConnections")
    public ArrayList<ServerLoginNetworkHandler> getPendingConnections();
}
