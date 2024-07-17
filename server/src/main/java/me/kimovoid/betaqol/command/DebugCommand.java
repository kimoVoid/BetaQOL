package me.kimovoid.betaqol.command;

import me.kimovoid.betaqol.BetaQOL;
import me.kimovoid.betaqol.command.exception.IncorrectUsageException;
import me.kimovoid.betaqol.mixin.access.WorldAccessor;
import net.minecraft.command.source.CommandSource;
import net.minecraft.world.World;
import net.minecraft.world.WorldData;

public class DebugCommand extends Command {

	@Override
	public String getName() {
		return "debug";
	}

	@Override
	public String getUsage(CommandSource source) {
		return "/debug [type]";
	}

	@Override
	public boolean requiresOp() {
		return false;
	}

	@Override
	public void run(CommandSource source, String[] args) {
		String types = "weather, seed, spawn, version, day";

		if (args.length != 1) {
			throw new IncorrectUsageException(getUsage(source) + ". Valid types: §r" + types);
		}

		String type = args[0].toLowerCase();
		World world = BetaQOL.server.getWorld(0);
		WorldData data = ((WorldAccessor)world).getData();

		switch (type) {
			case "weather":
				source.sendMessage("rainTime: " + data.getRainTime() + " ");
				source.sendMessage("thunderTime: " + data.getThunderTime());
				break;
			case "seed":
				source.sendMessage("seed: " + data.getSeed());
				break;
			case "spawn":
				source.sendMessage("spawn: " + data.getSpawnX() + ", " + data.getSpawnY() + ", " + data.getSpawnZ());
				break;
			case "version":
				source.sendMessage("MC version: " + BetaQOL.INSTANCE.mcVersion);
				source.sendMessage("world version: " + data.getVersion());
				break;
			case "day":
				source.sendMessage("total time: " + data.getTime() + String.format(" ticks (day %.2f)", data.getTime() / 24000f));
				break;
			default:
				throw new IncorrectUsageException(getUsage(source) + ". Valid types: §r" + types);
		}
	}
}
