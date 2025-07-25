package me.kimovoid.betaqol.feature.gui;

import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;

import java.util.function.Consumer;

public class CallbackConfirmScreen extends ConfirmScreen {
    private final Consumer<Boolean> callback;

    public CallbackConfirmScreen(Screen screen, String title, String message, String yes, String no, Consumer<Boolean> callback) {
        super(screen, title, message, yes, no, -1);
        this.callback = callback;
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        this.callback.accept(button.id == 0);
    }
}
