package me.kimovoid.betaqol.mixin.feature.debugscreen;

import me.kimovoid.betaqol.BetaQOL;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    @Shadow protected abstract void renderProfilerChart(long tickTime);

    @Redirect(method = "run",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Minecraft;renderProfilerChart(J)V"
            )
    )
    private void removeChart(Minecraft instance, long l) {
        if (!BetaQOL.CONFIG.disableChart.get()) {
            this.renderProfilerChart(l);
        }
    }
}
