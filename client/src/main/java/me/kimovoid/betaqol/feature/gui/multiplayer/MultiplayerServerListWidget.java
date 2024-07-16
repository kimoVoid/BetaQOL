package me.kimovoid.betaqol.feature.gui.multiplayer;

import com.mojang.blaze3d.vertex.BufferBuilder;
import me.kimovoid.betaqol.BetaQOL;
import net.minecraft.client.gui.widget.ListWidget;
import net.minecraft.locale.LanguageManager;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * This is a port of MojangFix for Babric.
 * All credits to js6pak and everyone involved in that project.
 * <a href="https://github.com/js6pak/mojangfix">View here</a>
 */
public class MultiplayerServerListWidget extends ListWidget {

    private final MultiplayerScreen parent;
    private int mouseX;
    private int mouseY;

    public MultiplayerServerListWidget(MultiplayerScreen parent) {
        super(parent.getMinecraft(), parent.width, parent.height, 32, parent.height - 64, 36);
        this.parent = parent;
    }

    @Override
    public void render(int mouseX, int mouseY, float tickDelta) {
        super.render(mouseX, mouseY, tickDelta);
        this.mouseX = mouseX;
        this.mouseY = mouseY;
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
        this.parent.drawString(this.parent.getFontRenderer(), server.name, x + 2, y + 1, 0xffffff);
        if (!server.showIp) {
            LanguageManager translations = LanguageManager.getInstance();
            String hideIp = "(" + translations.translate("multiplayer.hidden") + ")";
            this.parent.drawString(this.parent.getFontRenderer(), hideIp, x + 2, y + 23, 0x303030);
        } else {
            this.parent.drawString(this.parent.getFontRenderer(), server.ip, x + 2, y + 23, 0x303030);
        }

        if (server.canPing) {
            Object lock = parent.lock;
            synchronized (lock) {
                if (parent.serverCount < 5 && !server.isLoaded) {
                    server.isLoaded = true;
                    server.ping = -2L;
                    server.description = "";
                    server.onlinePlayers = "";
                    parent.serverCount++;
                    new Thread(() -> {
                        try {
                            server.description = "§8Polling...";
                            long currTime = System.nanoTime();
                            parent.pingServer(server);
                            long afterTime = System.nanoTime();
                            server.ping = (afterTime - currTime) / 1000000L;
                        } catch (UnknownHostException unknownHostException) {
                            server.ping = -1L;
                            server.description = "§4Can't resolve hostname";
                        } catch (SocketTimeoutException | ConnectException socketTimeoutException) {
                            server.ping = -1L;
                            server.description = "§4Can't reach server";
                        } catch (IOException iOException) {
                            server.ping = -1L;
                            server.description = "§4Communication error";
                        } catch (Exception exception) {
                            server.ping = -1L;
                            server.description = "ERROR: " + exception.getClass();
                        } finally {
                            Object lock1 = parent.lock;
                            synchronized (lock1) {
                                parent.serverCount--;
                            }
                        }
                    }).start();
                }
            }

            this.parent.drawString(this.parent.getFontRenderer(), server.description, x + 2, y + 12, 0x808080);
            this.parent.drawString(this.parent.getFontRenderer(), server.onlinePlayers, x + 215 - this.parent.getFontRenderer().getWidth(server.onlinePlayers), y + 12, 0x808080);

            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            BetaQOL.mc.textureManager.bind(BetaQOL.mc.textureManager.load("/assets/betaqol/tablist/icons.png"));

            int shiftX = 0;
            int shiftY;
            String tooltipText;

            if (server.isLoaded && server.ping != -2L) {
                shiftY = server.ping < 0L ? 5 : (server.ping < 150L ? 0 : (server.ping < 300L ? 1 : (server.ping < 600L ? 2 : (server.ping < 1000L ? 3 : 4))));
                tooltipText = server.ping < 0L ? "(no connection)" : server.ping + "ms";
            } else {
                shiftX = 1;
                shiftY = (int)(System.currentTimeMillis() / 100L + (index * 2L) & 7L);
                if (shiftY > 4) {
                    shiftY = 8 - shiftY;
                }
                tooltipText = "Polling...";
            }

            BetaQOL.mc.gui.drawTexture(x + 205, y, shiftX * 10, 176 + shiftY * 8, 10, 8);
            if (this.mouseX >= x + 205 - 4 && this.mouseY >= y - 4 && this.mouseX <= x + 205 + 10 + 4 && this.mouseY <= y + 8 + 4) {
                parent.tooltipText = tooltipText;
            }
        }
    }
}