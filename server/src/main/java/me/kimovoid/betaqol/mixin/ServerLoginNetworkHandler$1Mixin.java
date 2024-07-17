package me.kimovoid.betaqol.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.net.MalformedURLException;
import java.net.URL;

@Mixin(targets = "net.minecraft.server.network.handler.ServerLoginNetworkHandler$1")
public class ServerLoginNetworkHandler$1Mixin {

	@ModifyVariable(method = "run()V", at = @At(value = "STORE"), ordinal = 0)
	public URL modify(URL url) {
		try {
			return new URL(url.toString().replace("http://www.minecraft.net/game/checkserver.jsp?user=", "http://session.minecraft.net/game/checkserver.jsp?user="));
		} catch (MalformedURLException ex) {
			return url;
		}
	}
}