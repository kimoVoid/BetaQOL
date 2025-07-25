package me.kimovoid.betaqol.feature.gui.multiplayer;

import me.kimovoid.betaqol.feature.gui.CallbackButtonWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.locale.LanguageManager;
import org.lwjgl.input.Keyboard;

public class EditServerScreen extends Screen {
    private final ServerData server;
    private ButtonWidget button;
    private final MultiplayerScreen parent;
    private TextFieldWidget nameTextField;
    private TextFieldWidget ipTextField;
    private boolean showIp = true;
    private boolean ping = false;

    public EditServerScreen(MultiplayerScreen parent, ServerData server) {
        this.parent = parent;
        this.server = server;
    }

    public void tick() {
        this.nameTextField.tick();
        this.ipTextField.tick();
    }

    public void init() {
        Keyboard.enableRepeatEvents(true);
        LanguageManager translations = LanguageManager.getInstance();
        if (this.server != null) {
            this.showIp = this.server.showIp;
            this.ping = this.server.canPing;
        }
        this.buttons.add(new CallbackButtonWidget(this.width / 2 - 100, 140, 98, 20, translations.translate("multiplayer.showIp") + (this.showIp ? " ON" : " OFF"), button -> {
            this.showIp = !this.showIp;
            button.message = translations.translate("multiplayer.showIp") + (this.showIp ? " ON" : " OFF");
        }));
        this.buttons.add(new CallbackButtonWidget(this.width / 2 + 2, 140, 98, 20, translations.translate("multiplayer.ping") + (this.ping ? " ON" : " OFF"), button -> {
            this.ping = !this.ping;
            button.message = translations.translate("multiplayer.ping") + (this.ping ? " ON" : " OFF");
        }));
        this.buttons.add(this.button = new CallbackButtonWidget(this.width / 2 - 100, this.height / 4 + 120 + 12, this.server == null ? translations.translate("multiplayer.addServer") : translations.translate("multiplayer.edit"), button -> {
            if (this.server != null) {
                this.server.name = this.nameTextField.getText();
                this.server.ip = this.ipTextField.getText();
                this.server.showIp = this.showIp;
                this.server.canPing = this.ping;
            } else {
                this.parent.getServersList().add(new ServerData(this.nameTextField.getText(), this.ipTextField.getText(), this.showIp, this.ping));
            }

            this.parent.saveServers();
            this.minecraft.openScreen(this.parent);
        }));
        this.buttons.add(new CallbackButtonWidget(this.width / 2 - 100, this.height / 4 + 144 + 12, LanguageManager.getInstance().translate("gui.cancel"), button -> {
            this.minecraft.openScreen(this.parent);
        }));

        this.nameTextField = new TextFieldWidget(this, this.textRenderer, this.width / 2 - 100, 60, 200, 20, this.server == null ? "" : this.server.name);
        this.nameTextField.setMaxLength(32);
        this.ipTextField = new TextFieldWidget(this, this.textRenderer, this.width / 2 - 100, 106, 200, 20, this.server == null ? "" : this.server.ip);
        this.ipTextField.setMaxLength(32);
        this.updateButton();
    }

    private void updateButton() {
        this.button.active = !this.nameTextField.getText().trim().isEmpty() && !this.ipTextField.getText().trim().isEmpty();
    }

    public void removed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    protected void keyPressed(char character, int keyCode) {
        this.nameTextField.keyPressed(character, keyCode);
        this.ipTextField.keyPressed(character, keyCode);
        this.updateButton();
        if (character == Keyboard.KEY_RETURN) {
            this.buttonClicked(this.button);
        }
    }

    protected void mouseClicked(int mouseX, int mouseY, int varbutton) {
        super.mouseClicked(mouseX, mouseY, varbutton);
        this.nameTextField.mouseClicked(mouseX, mouseY, varbutton);
        this.ipTextField.mouseClicked(mouseX, mouseY, varbutton);
    }

    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        LanguageManager translations = LanguageManager.getInstance();

        this.drawCenteredString(this.textRenderer, (this.server == null ? translations.translate("multiplayer.addServer") : translations.translate("multiplayer.editServerInfo")), this.width / 2, 20, 16777215);
        this.drawString(this.textRenderer, translations.translate("multiplayer.serverName"), this.width / 2 - 100, 47, 10526880);
        this.drawString(this.textRenderer, translations.translate("multiplayer.serverAddress"), this.width / 2 - 100, 94, 10526880);
        this.nameTextField.render();
        this.ipTextField.render();

        if (this.ping) {
            drawCenteredString(this.minecraft.textRenderer, translations.translate("multiplayer.pingWarning"), this.width / 2, 170, 0x808080);
        }

        super.render(mouseX, mouseY, delta);
    }
}