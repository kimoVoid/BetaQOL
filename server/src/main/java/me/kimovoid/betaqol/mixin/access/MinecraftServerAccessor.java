package me.kimovoid.betaqol.mixin.access;

import net.minecraft.command.handler.CommandHandler;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MinecraftServer.class)
public interface MinecraftServerAccessor {

	@Accessor("commandHandler")
	public CommandHandler getCommandHandler();
}
