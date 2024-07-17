package me.kimovoid.betaqol.mixin.access;

import net.minecraft.world.World;
import net.minecraft.world.WorldData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(World.class)
public interface WorldAccessor {

	@Accessor("data")
	WorldData getData();
}
