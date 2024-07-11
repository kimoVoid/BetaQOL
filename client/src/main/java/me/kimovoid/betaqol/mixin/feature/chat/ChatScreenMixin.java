package me.kimovoid.betaqol.mixin.feature.chat;

import me.kimovoid.betaqol.mixin.access.TextFieldWidgetAccessor;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.TextRenderer;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static me.kimovoid.betaqol.feature.chat.ChatScreenVariables.*;

/**
 * This is a port of MojangFix for Babric.
 * All credits to js6pak and everyone involved in that project.
 * <a href="https://github.com/js6pak/mojangfix">View here</a>
 */
@Mixin(ChatScreen.class)
public class ChatScreenMixin extends Screen {

    @Shadow
    protected String lastChatMessage;

    @Unique
    private int focusedTicks;

    @Inject(method = "init", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        textField = new TextFieldWidget(this, textRenderer, 2, height - 14, width - 2, height - 2, "");
        textField.setFocused(true);
        textField.setMaxLength(1000);
        chatHistoryPosition = 0;
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void onTick(CallbackInfo ci) {
        if (textField.focused) {
            this.focusedTicks++;
        } else {
            this.focusedTicks = 0;
        }

        textField.tick();
        ci.cancel();
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ChatScreen;drawString(Lnet/minecraft/client/render/TextRenderer;Ljava/lang/String;III)V"))
    private void redirectDrawString(ChatScreen instance, TextRenderer textRenderer, String s, int x, int y, int color) {
        boolean caretVisible = textField.focused && this.focusedTicks / 6 % 2 == 0;
        instance.drawString(textRenderer, "> " + ((TextFieldWidgetAccessor)textField).getText() + (caretVisible ? "_" : ""), x, y, color);
    }

    @Redirect(method = "*", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/ChatScreen;lastChatMessage:Ljava/lang/String;", opcode = 180))
    private String getMessage(ChatScreen chatScreen) {
        return textField.getText();
    }

    @Inject(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/living/player/InputPlayerEntity;sendChat(Ljava/lang/String;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void onSendChatMessage(char character, int keyCode, CallbackInfo ci, String var3, String message) {
        int size = CHAT_HISTORY.size();
        if (size > 0 && CHAT_HISTORY.get(size - 1).equals(message)) {
            return;
        }

        CHAT_HISTORY.add(message);
    }

    @Unique
    private void setTextFromHistory() {
        textField.setText(CHAT_HISTORY.get(CHAT_HISTORY.size() + chatHistoryPosition));
        this.lastChatMessage = textField.getText();
    }

    @Inject(method = "keyPressed", at = @At(value = "JUMP", opcode = 160, ordinal = 2), cancellable = true)
    private void onKeyPressedEntry(char character, int keyCode, CallbackInfo ci) {
        if (keyCode == Keyboard.KEY_UP && chatHistoryPosition > -CHAT_HISTORY.size()) {
            --chatHistoryPosition;
            setTextFromHistory();
            ci.cancel();
        } else if (keyCode == Keyboard.KEY_DOWN && chatHistoryPosition < -1) {
            ++chatHistoryPosition;
            setTextFromHistory();
            ci.cancel();
        }
    }

    @Inject(method = "keyPressed", at = @At("TAIL"))
    private void onKeyPressedTail(char character, int keyCode, CallbackInfo ci) {
        textField.keyPressed(character, keyCode);
    }
}
