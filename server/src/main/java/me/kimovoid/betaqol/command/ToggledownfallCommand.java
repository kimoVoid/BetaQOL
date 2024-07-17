package me.kimovoid.betaqol.command;

import me.kimovoid.betaqol.BetaQOL;
import me.kimovoid.betaqol.mixin.access.WorldAccessor;
import net.minecraft.command.source.CommandSource;

public class ToggledownfallCommand extends Command {

	@Override
	public String getName() {
		return "toggledownfall";
	}

	@Override
	public String getUsage(CommandSource source) {
		return "/toggledownfall";
	}

	@Override
	public void run(CommandSource source, String[] args) {
		sendSuccess(source.getSourceName(), "Toggled downfall");
		((WorldAccessor) BetaQOL.server.getWorld(0)).getData().setRainTime(1);
		((WorldAccessor) BetaQOL.server.getWorld(0)).getData().setThunderTime(1);
	}
}