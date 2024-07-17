package me.kimovoid.betaqol.mixin;

import me.kimovoid.betaqol.BetaQOL;
import me.kimovoid.betaqol.command.*;
import me.kimovoid.betaqol.command.exception.CommandException;
import me.kimovoid.betaqol.command.exception.IncorrectUsageException;
import net.minecraft.command.PendingCommand;
import net.minecraft.command.handler.CommandHandler;
import net.minecraft.command.source.CommandSource;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CommandHandler.class)
public class CommandHandlerMixin {

	@Inject(method = "<init>(Lnet/minecraft/server/MinecraftServer;)V", at = @At("RETURN"))
	public void onInit(MinecraftServer server, CallbackInfo ci) {
		BetaQOL.server = server;
	}

	@Inject(method = "run", at = @At("HEAD"), cancellable = true)
	public void injectRun(PendingCommand pendingCommand, CallbackInfo ci) {
		String cmd = pendingCommand.command;
		CommandSource source = pendingCommand.source;

		String commandName = cmd.toLowerCase().split(" ", 2)[0];
		if (BetaQOL.commandsByName.containsKey(commandName)) {
			runCommand(source, cmd);
			ci.cancel();
		}
	}

	@Unique
	private void runCommand(CommandSource source, String cmd) {
		String commandName = cmd.toLowerCase().split(" ", 2)[0];
		String[] args = cmd.contains(" ") ? cmd.split(" ", 2)[1].split(" ") : new String[]{};
		ICommand command = BetaQOL.commandsByName.get(commandName);

		try {
			command.run(source, args);
		} catch (IncorrectUsageException ex) {
			source.sendMessage("§cUsage: " + String.format(ex.getMessage(), ex.getArgs()));
		} catch (CommandException ex) {
			source.sendMessage("§c" + String.format(ex.getMessage(), ex.getArgs()));
		} catch (Throwable ex) {
			source.sendMessage("§cThere was an internal error while running the command.");
			ex.printStackTrace();
		}
	}
}
