package me.kimovoid.betaqol.mixin.fixes.connection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(targets = "net.minecraft.network.Connection$3")
public class Connection$3Mixin {

    @ModifyConstant(method = "run", constant = @Constant(longValue = 100L))
    public long decreasePacketDelay(long constant) {
        return 0L;
    }
}