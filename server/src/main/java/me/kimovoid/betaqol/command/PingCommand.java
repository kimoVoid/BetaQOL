package me.kimovoid.betaqol.command;

import me.kimovoid.betaqol.BetaQOL;
import me.kimovoid.betaqol.command.exception.IncorrectUsageException;
import me.kimovoid.betaqol.interfaces.IServerPlayerEntity;
import net.minecraft.command.source.CommandSource;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;

public class PingCommand extends Command {

    @Override
    public String getName() {
        return "ping";
    }

    @Override
    public String getUsage(CommandSource source) {
        return "/ping [player]";
    }

    @Override
    public boolean requiresOp() {
        return false;
    }

    @Override
    public void run(CommandSource source, String[] args) {
        if (args.length < 1 && BetaQOL.server.playerManager.get(source.getSourceName()) == null)
            throw new IncorrectUsageException(String.format("%s. Sender must be a player.", getUsage(source)));

        ServerPlayerEntity target;

        if (args.length < 1) {
            target = BetaQOL.server.playerManager.get(source.getSourceName());
        } else {
            if (BetaQOL.server.playerManager.get(args[0]) == null)
                throw new IncorrectUsageException(String.format("%s. Player %s is not online.", getUsage(source), args[0]));

            target = BetaQOL.server.playerManager.get(args[0]);
        }

        int ping = ((IServerPlayerEntity)target).getPing();
        source.sendMessage((target.name.equals(source.getSourceName()) ? "Your" : target.name + "'s") + " ping is: " + ping + " ms");
        /*
        try {
            source.sendMessage("tcpNoDelay: " + ((ConnectionAccessor) target.networkHandler.connection).getSocket().getTcpNoDelay());
        } catch (Exception ignored) {}
         */
    }
}
