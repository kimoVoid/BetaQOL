package me.kimovoid.betaqol.mixin.feature.alwaysplaymusic;

import me.kimovoid.betaqol.BetaQOL;
import net.minecraft.client.sound.system.SoundEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(SoundEngine.class)
public class SoundEngineMixin {

    @Shadow private int musicCooldown;
    @Shadow private Random random;

    @Unique private boolean alwaysPlayMusic = false;

    @Inject(method = "tickMusic",
            at = @At(value = "HEAD")
    )
    private void setMusicCooldown(CallbackInfo ci) {
        boolean apm = BetaQOL.CONFIG.alwaysPlayMusic.get();
        if (apm != this.alwaysPlayMusic) {
            this.alwaysPlayMusic = apm;
            if (apm) this.resetCooldown();
        }
    }

    @Inject(method = "tickMusic",
            at = @At(
                    value = "INVOKE",
                    target = "Lpaulscode/sound/SoundSystem;backgroundMusic(Ljava/lang/String;Ljava/net/URL;Ljava/lang/String;Z)V",
                    shift = At.Shift.BEFORE,
                    remap = false
            )
    )
    private void tickMusicCooldown(CallbackInfo ci) {
        if (BetaQOL.CONFIG.alwaysPlayMusic.get()) {
            this.resetCooldown();
        }
    }

    @Unique
    private void resetCooldown() {
        this.musicCooldown = this.random.nextInt(400) + 200;
    }
}
