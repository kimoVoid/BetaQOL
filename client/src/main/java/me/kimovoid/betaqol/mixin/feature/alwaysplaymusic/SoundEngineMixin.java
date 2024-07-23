package me.kimovoid.betaqol.mixin.feature.alwaysplaymusic;

import me.kimovoid.betaqol.BetaQOL;
import net.minecraft.client.sound.system.SoundEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(SoundEngine.class)
public class SoundEngineMixin {

    @Shadow private int musicCooldown;
    @Shadow private Random random;

    /* this mixin is super lazy but i'm tired and i want to go to bed */
    @Inject(method = "tickMusic",
            at = @At(
                    value = "INVOKE",
                    target = "Lpaulscode/sound/SoundSystem;backgroundMusic(Ljava/lang/String;Ljava/net/URL;Ljava/lang/String;Z)V",
                    shift = At.Shift.BEFORE,
                    remap = false
            )
    )
    private void setMusicCooldown(CallbackInfo ci) {
        if (BetaQOL.CONFIG.alwaysPlayMusic.get()) {
            this.musicCooldown = this.random.nextInt(1200) + 200;
        }
    }
}
