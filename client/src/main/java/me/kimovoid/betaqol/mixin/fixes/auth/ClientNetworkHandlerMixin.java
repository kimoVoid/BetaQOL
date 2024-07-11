package me.kimovoid.betaqol.mixin.fixes.auth;

import net.minecraft.client.network.handler.ClientNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.net.MalformedURLException;
import java.net.URL;

@Mixin(ClientNetworkHandler.class)
public class ClientNetworkHandlerMixin {

    @ModifyVariable(method = "handleHandshake", at = @At(value = "STORE"), ordinal = 0)
    public URL modify(URL url) {
        try {
            return new URL(url.toString().replace("www.minecraft.net", "session.minecraft.net"));
        } catch (MalformedURLException ex) {
            return url;
        }
    }
}