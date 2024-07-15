package me.kimovoid.betaqol.feature.gui.multiplayer;

import me.kimovoid.betaqol.feature.gui.CallbackButtonWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.locale.LanguageManager;
import org.lwjgl.input.Keyboard;

/**
 * This is a port of MojangFix for Babric.
 * All credits to js6pak and everyone involved in that project.
 * <a href="https://github.com/js6pak/mojangfix">View here</a>
 */
public class DirectConnectScreen extends Screen {
    private static final int DEFAULT_PORT = 25565;

    private final Screen parent;
    private TextFieldWidget addressField;
    private CallbackButtonWidget connectButton;

    public DirectConnectScreen(Screen parent) {
        this.parent = parent;
    }

    public static int parseIntWithDefault(String s, int def) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return def;
        }
    }

    public static void connect(Minecraft minecraft, String addressText) {
        String[] split = addressText.split(":");
        minecraft.openScreen(new ConnectScreen(minecraft, split[0], split.length > 1 ? parseIntWithDefault(split[1], DEFAULT_PORT) : DEFAULT_PORT));
    }

    @Override
    public void tick() {
        this.addressField.tick();
    }

    public void init() {
        Keyboard.enableRepeatEvents(true);

        LanguageManager translationStorage = LanguageManager.getInstance();
        this.buttons.add(connectButton = new CallbackButtonWidget(this.width / 2 - 100, this.height / 4 + 96 + 12, translationStorage.translate("multiplayer.connect"), button -> {
            String address = this.addressField.getText().trim();
            this.minecraft.options.lastServer = address.replaceAll(":", "_");
            this.minecraft.options.save();
            connect(this.minecraft, address);
        }));
        this.buttons.add(new CallbackButtonWidget(this.width / 2 - 100, this.height / 4 + 120 + 12, translationStorage.translate("gui.cancel"), button -> {
            this.minecraft.openScreen(this.parent);
        }));
        String lastServer = this.minecraft.options.lastServer.replaceAll("_", ":");
        connectButton.active = !lastServer.isEmpty();
        this.addressField = new TextFieldWidget(this, this.textRenderer, this.width / 2 - 100, this.height / 4 - 10 + 50 + 18, 200, 20, lastServer);
        this.addressField.focused = true;
        this.addressField.setMaxLength(128);
    }

    public void removed() {
        Keyboard.enableRepeatEvents(false);
    }

    protected void keyPressed(char character, int keyCode) {
        this.addressField.keyPressed(character, keyCode);
        if (keyCode == Keyboard.KEY_RETURN) {
            this.buttonClicked((ButtonWidget) this.buttons.get(0));
        }

        ((ButtonWidget) this.buttons.get(0)).active = this.addressField.getText().length() > 0;
    }

    protected void mouseClicked(int mouseX, int mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        this.addressField.mouseClicked(mouseX, mouseY, button);
    }

    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        this.drawCenteredString(this.textRenderer, LanguageManager.getInstance().translate("multiplayer.directConnect"), this.width / 2, this.height / 4 - 60 + 20, 16777215);
        this.drawCenteredString(this.textRenderer, LanguageManager.getInstance().translate("multiplayer.ipinfo"), this.width / 2, this.height / 4 - 60 + 60 + 36, 10526880);
        this.addressField.render();
        super.render(mouseX, mouseY, delta);
    }
}
