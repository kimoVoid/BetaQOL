package me.kimovoid.betaqol.command;

import me.kimovoid.betaqol.BetaQOL;
import me.kimovoid.betaqol.interfaces.IMinecraftServer;
import net.minecraft.command.source.CommandSource;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class StatusCommand extends Command {

	@Override
	public String getName() {
		return "status";
	}

	@Override
	public String getUsage(CommandSource source) {
		return "/status";
	}

	@Override
	public boolean requiresOp() {
		return false;
	}

	@Override
	public void run(CommandSource source, String[] args) {
		double mspt = this.round(2, this.average(((IMinecraftServer) BetaQOL.server).getTickTimes()) * 1.0E-6D);
		double tps = this.round(2, 1000.0D / mspt);

		if (tps > 20.0D) {
			tps = 20.0D;
		}

		Runtime runtime = Runtime.getRuntime();

		int mb = 1048576;
		String used = "  Used: " + (runtime.totalMemory() - runtime.freeMemory()) / (long)mb + " MB / " + runtime.totalMemory() / (long)mb + " MB";
		String free = "  Free: " + runtime.freeMemory() / (long)mb + " MB";
		String max = "  Max: " + runtime.maxMemory() / (long)mb + " MB";

		source.sendMessage(String.format("TPS: %.1f MSPT: %.2f", tps, mspt));
		source.sendMessage("Memory:");
		source.sendMessage(used);
		source.sendMessage(free);
		source.sendMessage(max);
	}

	public double round(int places, double value) {
		return (new BigDecimal(value)).setScale(places, RoundingMode.HALF_UP).doubleValue();
	}

	public double average(long[] times) {
		long avg = 0L;
        for (long time : times) {
			avg += time;
        }
		return (double)avg / (double)times.length;
	}
}
