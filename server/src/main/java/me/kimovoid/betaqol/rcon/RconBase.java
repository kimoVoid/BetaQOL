package me.kimovoid.betaqol.rcon;

import net.minecraft.server.MinecraftServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public abstract class RconBase implements Runnable {
	protected boolean running = false;
	protected MinecraftServer server;
	protected Thread rconThread;
	protected List<ServerSocket> closeableSockets = new ArrayList<>();

	RconBase(MinecraftServer server) {
		this.server = server;
	}

	public synchronized void start() {
		this.rconThread = new Thread(this);
		this.rconThread.start();
		this.running = true;
	}

	public boolean isRunning() {
		return this.running;
	}

	protected void info(String message) {
		this.server.sendMessage(message);
	}

	protected void warn(String message) {
		this.server.warn(message);
	}

	protected void closeSocket(ServerSocket socket) {
		if (socket != null) {
			try {
				if (!socket.isClosed()) {
					socket.close();
				}
			} catch (IOException var5) {
				this.warn("IO: " + var5.getMessage());
			}

			this.closeableSockets.remove(socket);
		}
	}
}
