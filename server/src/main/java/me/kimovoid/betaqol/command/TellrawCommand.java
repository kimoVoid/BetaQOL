package me.kimovoid.betaqol.command;

import me.kimovoid.betaqol.BetaQOL;
import me.kimovoid.betaqol.command.exception.CommandException;
import me.kimovoid.betaqol.command.exception.IncorrectUsageException;
import net.minecraft.command.source.CommandSource;
import net.minecraft.network.packet.ChatMessagePacket;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;

public class TellrawCommand extends Command {

	@Override
	public String getName() {
		return "tellraw";
	}

	@Override
	public String getUsage(CommandSource source) {
		return "/tellraw [player] [text]";
	}

	@Override
	public void run(CommandSource source, String[] args) {
		if (args.length < 2) {
			throw new IncorrectUsageException(getUsage(source));
		}

		String message = String.join(" ", args).split(" ", 2)[1];

		if (args[0].equalsIgnoreCase("@a")) {
			for (Object pl : BetaQOL.SERVER.playerManager.players) {
				((ServerPlayerEntity)pl).networkHandler.sendPacket(new ChatMessagePacket(message));
			}
			return;
		}

		ServerPlayerEntity p = BetaQOL.SERVER.playerManager.get(args[0]);
		if (p == null) {
			throw new CommandException(String.format("'%s' is not online", args[0]));
		}
		p.networkHandler.sendPacket(new ChatMessagePacket(message));
	}
}
