package me.kimovoid.betaqol.rcon;

import net.minecraft.command.source.CommandSource;

public class Console implements CommandSource {
	public static final Console INSTANCE = new Console();
	private StringBuffer text = new StringBuffer();

	public Console() {
	}

	public void destroy() {
		this.text.setLength(0);
	}

	public String getTextAsString() {
		return this.text.toString();
	}

	public void sendMessage(String message) {
		this.text.append(message);
	}

	public String getSourceName() {
		return "Rcon";
	}
}
