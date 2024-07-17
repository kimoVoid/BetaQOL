package me.kimovoid.betaqol.command;

import me.kimovoid.betaqol.BetaQOL;
import me.kimovoid.betaqol.command.exception.CommandException;
import net.minecraft.command.source.CommandSource;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;

public class IbCommand extends Command {

	@Override
	public String getName() {
		return "ib";
	}

	@Override
	public String getUsage(CommandSource source) {
		return "/ib";
	}

	@Override
	public void run(CommandSource source, String[] args) {
		if (source.getSourceName().equalsIgnoreCase("console")) {
			throw new CommandException("This command can only be used by players");
		}

		ServerPlayerEntity player = BetaQOL.server.playerManager.get(source.getSourceName());
		if (BetaQOL.INSTANCE.instantBreak.contains(player.name)) {
			BetaQOL.INSTANCE.instantBreak.remove(player.name);
			sendSuccess(player.name, "No longer mining blocks instantly");
		} else {
			BetaQOL.INSTANCE.instantBreak.add(player.name);
			sendSuccess(player.name, "Now mining blocks instantly");
		}
	}
}
