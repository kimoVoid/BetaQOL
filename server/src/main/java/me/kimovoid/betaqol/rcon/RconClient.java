package me.kimovoid.betaqol.rcon;

import me.kimovoid.betaqol.mixin.access.MinecraftServerAccessor;
import net.minecraft.command.PendingCommand;
import net.minecraft.server.MinecraftServer;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class RconClient extends RconBase {
	private boolean authenticated = true;
	private Socket socket;
	private final byte[] packetBuffer = new byte[1460];
	private final String password;
	private boolean skipClose = false;

	RconClient(MinecraftServer server, Socket socket) {
		super(server);
		this.socket = socket;
		this.password = server.properties.getString("rcon.password", "");
		this.info("Rcon connection from: " + socket.getInetAddress());
	}

	public void run() {
		while (true) {
			try {
				if (!this.running) {
					return;
				}

				try {
					BufferedInputStream var1 = new BufferedInputStream(this.socket.getInputStream());
					int var2 = var1.read(this.packetBuffer, 0, 1460);
					if (10 > var2) {
						return;
					}

					byte var3 = 0;
					int var4 = BufferHelper.getIntLE(this.packetBuffer, 0, var2);
					if (var4 == var2 - 4) {
						int var21 = var3 + 4;
						int var5 = BufferHelper.getIntLE(this.packetBuffer, var21, var2);
						var21 += 4;
						int var6 = BufferHelper.getIntLE(this.packetBuffer, var21);
						var21 += 4;
						switch (var6) {
							case 2:
								if (this.authenticated) {
									String var8 = BufferHelper.getString(this.packetBuffer, var21, var2);

									try {
										this.execute(var5, runRconCommand(var8));
									} catch (Exception var16) {
										this.execute(var5, "Error executing: " + var8 + " (" + var16.getMessage() + ")");
									}
									continue;
								}

								this.executeUnknown();
								continue;
							case 3:
								String var7 = BufferHelper.getString(this.packetBuffer, var21, var2);
								if (0 != var7.length() && var7.equals(this.password)) {
									this.authenticated = true;
									this.execute(var5, 2, "");
									this.skipClose = true;
									continue;
								}

								this.authenticated = false;
								this.executeUnknown();
								continue;
							default:
								this.execute(var5, String.format("Unknown request %s", Integer.toHexString(var6)));
						}
					}
				} catch (SocketTimeoutException ignored) {
					return;
				} catch (IOException var18) {
					if (this.running) {
						this.info("IO: " + var18.getMessage());
					}
					return;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				if (skipClose) {
					skipClose = false;
				} else {
					this.close();
					this.running = false;
				}
			}
			return;
		}
	}

	public String runRconCommand(String command) {
		Console.INSTANCE.destroy();
		((MinecraftServerAccessor) this.server).getCommandHandler().run(new PendingCommand(command, Console.INSTANCE));
		return Console.INSTANCE.getTextAsString();
	}

	private void execute(int stream1, int stream2, String text) throws IOException {
		ByteArrayOutputStream var4 = new ByteArrayOutputStream(1248);
		DataOutputStream var5 = new DataOutputStream(var4);
		var5.writeInt(Integer.reverseBytes(text.length() + 10));
		var5.writeInt(Integer.reverseBytes(stream1));
		var5.writeInt(Integer.reverseBytes(stream2));
		var5.writeBytes(text);
		var5.write(0);
		var5.write(0);
		this.socket.getOutputStream().write(var4.toByteArray());
	}

	private void executeUnknown() throws IOException {
		this.execute(-1, 2, "");
	}

	private void execute(int id, String name) throws IOException {
		int var3 = name.length();

		do {
			int var4 = Math.min(4096, var3);
			this.execute(id, 0, name.substring(0, var4));
			name = name.substring(var4);
			var3 = name.length();
		} while (0 != var3);

	}

	public void close() {
		if (this.socket != null) {
			try {
				this.socket.close();
			} catch (IOException var2) {
				this.warn("IO: " + var2.getMessage());
			}

			this.socket = null;
		}
	}
}
