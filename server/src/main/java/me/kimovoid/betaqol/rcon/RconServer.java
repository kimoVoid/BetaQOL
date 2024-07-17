package me.kimovoid.betaqol.rcon;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerProperties;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;

public class RconServer extends RconBase {
	private int port;
	private String hostname;
	private ServerSocket listener;
	private final String password;
	public HashMap<SocketAddress, RconClient> clientBySocket;

	public RconServer(MinecraftServer server) {
		super(server);
		ServerProperties prop = server.properties;
		this.port = prop.getInt("rcon.port", 0);
		this.password = prop.getString("rcon.password", "");
		this.hostname = prop.getString("server-ip", "0.0.0.0");
		int serverPort = prop.getInt("server-port", 25565);
		if (0 == this.port) {
			this.port = serverPort + 10;
			this.info("Setting default rcon port to " + this.port);

			prop.save();
		}

		if (0 == this.hostname.length()) {
			this.hostname = "0.0.0.0";
		}

		this.initMap();
		this.listener = null;
	}

	private void initMap() {
		this.clientBySocket = new HashMap<>();
	}

	private void removeStoppedClients() {
		this.clientBySocket.entrySet().removeIf(var2 -> !var2.getValue().isRunning());
	}

	public void run() {
		this.info("RCON running on " + this.hostname + ":" + this.port);

		try {
			while(this.running) {
				try {
					Socket var1 = this.listener.accept();
					var1.setSoTimeout(500);
					RconClient var2 = new RconClient(this.server, var1);
					var2.start();
					this.clientBySocket.put(var1.getRemoteSocketAddress(), var2);
					this.removeStoppedClients();
				} catch (SocketTimeoutException var7) {
					this.removeStoppedClients();
				} catch (IOException var8) {
					if (this.running) {
						this.info("IO: " + var8.getMessage());
					}
				}
			}
		} finally {
			closeSocket(this.listener);
		}
	}

	public void start() {
		if (0 == this.password.length()) {
			this.warn("No rcon password set in 'server.properties', rcon disabled!");
		} else if (this.port > 0 && this.port < 65535) {
			if (!this.running) {
				try {
					this.listener = new ServerSocket(this.port, 0, InetAddress.getByName(this.hostname));
					this.listener.setSoTimeout(500);
					super.start();
				} catch (IOException var2) {
					this.warn("Unable to initialise rcon on " + this.hostname + ":" + this.port + " : " + var2.getMessage());
				}

			}
		} else {
			this.warn("Invalid rcon port " + this.port + " found in 'server.properties', rcon disabled!");
		}
	}
}
