package me.kimovoid.betaqol.interfaces;

import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;

public interface IListenThread {

    public HashMap<InetAddress, Long> getAddresses();

    public void close(Socket socket);
}
