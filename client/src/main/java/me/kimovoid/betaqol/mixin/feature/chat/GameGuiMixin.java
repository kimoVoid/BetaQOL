package me.kimovoid.betaqol.mixin.feature.chat;

import me.kimovoid.betaqol.BetaQOL;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatMessage;
import net.minecraft.client.gui.GameGui;
import net.minecraft.client.gui.screen.ChatScreen;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * Parts of this were ported from HudTweaks for Babric.
 * All credits to telvarost.
 * <a href="https://github.com/telvarost/HudTweaks-StationAPI">View here</a>
 */
@Mixin(GameGui.class)
public class GameGuiMixin {

    @Shadow private Minecraft minecraft;
    @Shadow private List<ChatMessage> chatMessages;

    @Unique private int chatOffset = 0;

    @Inject(method = "render", at = @At("HEAD"))
    private void chatScroll(float f, boolean bl, int i, int j, CallbackInfo ci) {
        int chatRangeTop = 20;

        if (this.minecraft.screen instanceof ChatScreen) {
            int currentWheelDegrees = Mouse.getDWheel();
            int numberOfTurns = Math.round((float) currentWheelDegrees / 120.0f);
            this.chatOffset = this.chatOffset + numberOfTurns;

            if (this.chatOffset < 0) {
                this.chatOffset = 0;
            }

            if (chatRangeTop < this.chatMessages.size()) {
                if (this.chatMessages.size() <= (chatRangeTop + this.chatOffset)) {
                    this.chatOffset = this.chatMessages.size() - chatRangeTop;
                }
            } else {
                this.chatOffset = 0;
            }
        } else {
            this.chatOffset = 0;
        }
    }

    @Redirect(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;get(I)Ljava/lang/Object;"
            )
    )
    private Object chatOffset(List<ChatMessage> instance, int i) {
        return instance.get(i + this.chatOffset);
    }

    @Inject(
            method = "addChatMessage",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;add(ILjava/lang/Object;)V",
                    shift = At.Shift.AFTER
            ),
            cancellable = true
    )
    private void removeChatHistoryLimit(CallbackInfo ci) {
        if (BetaQOL.CONFIG.unlimitedChatHistory.get()) {
            ci.cancel();
        }
    }
}