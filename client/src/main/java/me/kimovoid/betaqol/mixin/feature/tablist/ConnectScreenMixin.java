package me.kimovoid.betaqol.mixin.feature.tablist;

import me.kimovoid.betaqol.BetaQOL;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.world.MultiplayerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ConnectScreen.class)
public class ConnectScreenMixin {

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/io/PrintStream;println(Ljava/lang/String;)V"))
    public void clearTab(CallbackInfo ci) {
        BetaQOL.INSTANCE.tabPlayers.clear();
    }
}