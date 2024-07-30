package me.kimovoid.betaqol.mixin.feature.fovslider;

import me.kimovoid.betaqol.feature.fov.FOVOption;
import net.minecraft.client.options.GameOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.BufferedReader;
import java.io.PrintWriter;

@Mixin(GameOptions.class)
public abstract class GameOptionsMixin {

    @Shadow protected abstract float parseFloat(String s);

    @Inject(method = "load", at = @At(value = "INVOKE", target = "Ljava/lang/String;split(Ljava/lang/String;)[Ljava/lang/String;"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void loadFov(CallbackInfo ci, BufferedReader reader, String s) {
        String[] stringArray = s.split(":");
        if (stringArray[0].equals("fov")) {
            FOVOption.fov = parseFloat(stringArray[1]);
        }
    }

    @Inject(method = "save", at = @At(value = "INVOKE", target = "Ljava/io/PrintWriter;close()V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void saveFov(CallbackInfo ci, PrintWriter printWriter) {
        printWriter.println("fov:" + FOVOption.fov);
    }
}
