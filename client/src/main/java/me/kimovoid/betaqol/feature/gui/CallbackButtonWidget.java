package me.kimovoid.betaqol.feature.gui;

import net.minecraft.client.gui.widget.ButtonWidget;

import java.util.function.Consumer;

public class CallbackButtonWidget extends ButtonWidget {

    private final Consumer<CallbackButtonWidget> onPress;

    public CallbackButtonWidget(int x, int y, String label, Consumer<CallbackButtonWidget> onPress) {
        this(x, y, 200, 20, label, onPress);
    }

    public CallbackButtonWidget(int x, int y, int width, int height, String label, Consumer<CallbackButtonWidget> onPress) {
        super(-1, x, y, width, height, label);
        this.onPress = onPress;
    }

    public void onPress() {
        this.onPress.accept(this);
    }
}