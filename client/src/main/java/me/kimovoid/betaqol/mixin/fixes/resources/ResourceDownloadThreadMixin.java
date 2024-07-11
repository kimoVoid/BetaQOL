package me.kimovoid.betaqol.mixin.fixes.resources;

import net.minecraft.client.resource.ResourceDownloadThread;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import java.net.MalformedURLException;
import java.net.URL;

/* Fixes resources with the BetaCraft proxy */
@Mixin(ResourceDownloadThread.class)
public class ResourceDownloadThreadMixin {

    @ModifyConstant(method = "run", constant = @Constant(stringValue = "http://s3.amazonaws.com/MinecraftResources/"), remap = false)
    private String getResourcesUrl(String def) {
        try {
            return this.replaceHost(new URL(def), "betacraft.uk", 11705).toString();
        } catch (MalformedURLException ignored) {
            return def;
        }
    }

    private URL replaceHost(URL url, String hostName, int port) {
        try {
            return new URL(url.getProtocol(), hostName, port, url.getFile());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
