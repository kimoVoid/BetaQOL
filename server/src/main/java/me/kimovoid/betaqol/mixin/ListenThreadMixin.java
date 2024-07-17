package me.kimovoid.betaqol.mixin;

import me.kimovoid.betaqol.interfaces.IListenThread;
import me.kimovoid.betaqol.mixin.access.ListenThreadAccessor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ListenThread;
import net.minecraft.server.network.handler.ServerLoginNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;

@Mixin(ListenThread.class)
public class ListenThreadMixin implements IListenThread {

    @Shadow private Thread thread;
    @Shadow public MinecraftServer server;
    @Unique private final HashMap<InetAddress, Long> addresses = new HashMap<>();

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/lang/Thread;start()V"))
    public void replaceThread(Thread instance) {
        this.thread = new Thread("Listen thread") {
            public void run() {
                while (server.connections.listening) {
                    try {
                        Socket socket = ((ListenThreadAccessor)server.connections).getSocket().accept();
                        if (socket == null) continue;
                        Object object = addresses;
                        synchronized (object) {
                            InetAddress inetAddress = socket.getInetAddress();
                            if (addresses.containsKey(inetAddress) && System.currentTimeMillis() - addresses.get(inetAddress) < 5000L) {
                                addresses.put(inetAddress, System.currentTimeMillis());
                                socket.close();
                                continue;
                            }
                            addresses.put(inetAddress, System.currentTimeMillis());
                        }
                        ((ListenThreadAccessor)server.connections).setConnectionCounter(((ListenThreadAccessor)server.connections).getConnectionCounter() + 1);
                        object = new ServerLoginNetworkHandler(server, socket, "Connection #" + ((ListenThreadAccessor)server.connections).getConnectionCounter());
                        addPendingConnection(server.connections, (ServerLoginNetworkHandler)object);
                    } catch (IOException iOException) {
                        iOException.printStackTrace();
                    }
                }
            }
        };
        this.thread.start();
    }

    private void addPendingConnection(ListenThread lt, ServerLoginNetworkHandler connection) {
        if (connection == null) {
            throw new IllegalArgumentException("Got null pendingconnection!");
        }
        ((ListenThreadAccessor)lt).getPendingConnections().add(connection);
    }

    @Override
    public void close(Socket socket) {
        System.out.println("removing " + socket.getInetAddress().toString());
        InetAddress inetAddress = socket.getInetAddress();
        addresses.remove(inetAddress);
    }

    @Override
    public HashMap<InetAddress, Long> getAddresses() {
        return this.addresses;
    }
}
