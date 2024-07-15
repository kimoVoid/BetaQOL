package me.kimovoid.betaqol.feature.gui.multiplayer;

import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.gui.widget.ListWidget;
import net.minecraft.locale.LanguageManager;

/**
 * This is a port of MojangFix for Babric.
 * All credits to js6pak and everyone involved in that project.
 * <a href="https://github.com/js6pak/mojangfix">View here</a>
 */
public class MultiplayerServerListWidget extends ListWidget {

    private final MultiplayerScreen parent;

    public MultiplayerServerListWidget(MultiplayerScreen parent) {
        super(parent.getMinecraft(), parent.width, parent.height, 32, parent.height - 64, 36);
        this.parent = parent;
    }

    @Override
    protected int size() {
        return this.parent.getServersList().size();
    }

    @Override
    protected void entryClicked(int slot, boolean doubleClick) {
        this.parent.selectServer(slot, doubleClick);
    }

    @Override
    protected boolean isEntrySelected(int i) {
        return i == this.parent.getServersList().indexOf(this.parent.getSelectedServer());
    }

    @Override
    protected int getHeight() {
        return this.parent.getServersList().size() * 36;
    }

    @Override
    protected void renderBackground() {
        this.parent.renderBackground();
    }

    @Override
    protected void renderEntry(int index, int x, int y, int l, BufferBuilder arg) {
        ServerData server = this.parent.getServersList().get(index);
        this.parent.drawString(this.parent.getFontRenderer(), server.getName(), x + 2, y + 1, 0xffffff);
        if (!server.isShowIp()) {
            LanguageManager translations = LanguageManager.getInstance();
            String hideIp = "(" + translations.translate("multiplayer.hidden") + ")";
            this.parent.drawString(this.parent.getFontRenderer(), hideIp, x + 2, y + 12, 0x808080);
        } else {
            this.parent.drawString(this.parent.getFontRenderer(), server.getIp(), x + 2, y + 12, 0x808080);
        }
    }
}