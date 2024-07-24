package me.kimovoid.betaqol.mixin.feature.itemdurability;

import me.kimovoid.betaqol.feature.itemdurability.ItemDurability;
import net.minecraft.client.options.GameOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.BufferedReader;
import java.io.PrintWriter;

@Mixin(GameOptions.class)
public abstract class GameOptionsMixin {

    @Inject(method = "load", at = @At(value = "INVOKE", target = "Ljava/lang/String;split(Ljava/lang/String;)[Ljava/lang/String;"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void loadFov(CallbackInfo ci, BufferedReader reader, String s) {
        String[] stringArray = s.split(":");
        if (stringArray[0].equals("itemDurability")) {
            ItemDurability.debugItemDurability = stringArray[1].equals("true");
        }
    }

    @Inject(method = "save", at = @At(value = "INVOKE", target = "Ljava/io/PrintWriter;close()V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void clientsideEssentials_saveOptions(CallbackInfo ci, PrintWriter printWriter) {
        printWriter.println("itemDurability:" + ItemDurability.debugItemDurability);
    }
}
