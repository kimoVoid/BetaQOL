package me.kimovoid.betaqol.mixin.fixes.auth;

import me.kimovoid.betaqol.feature.skinfix.SkinService;
import net.minecraft.client.Session;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Session.class)
public abstract class SessionMixin {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(String username, String sessionId, CallbackInfo ci) {
        SkinService.getInstance().initSimple(username);
    }
}
