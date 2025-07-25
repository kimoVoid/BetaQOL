package me.kimovoid.betaqol.mixin.fixes.skins;

import me.kimovoid.betaqol.feature.skinfix.SkinService;
import net.minecraft.client.entity.living.player.RemotePlayerEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RemotePlayerEntity.class)
public abstract class RemotePlayerEntityMixin extends PlayerEntity {

	public RemotePlayerEntityMixin(World world) {
		super(world);
	}

	@Inject(method = "<init>", at = @At("RETURN"))
	private void onInit(CallbackInfo ci) {
		SkinService.INSTANCE.init(this);
	}
}