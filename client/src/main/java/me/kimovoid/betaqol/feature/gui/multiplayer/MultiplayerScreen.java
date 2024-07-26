package me.kimovoid.betaqol.feature.gui.multiplayer;

import me.kimovoid.betaqol.BetaQOL;
import me.kimovoid.betaqol.feature.gui.CallbackButtonWidget;
import me.kimovoid.betaqol.feature.gui.CallbackConfirmScreen;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.TextRenderer;
import net.minecraft.locale.LanguageManager;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.packet.Packet;
import org.lwjgl.input.Keyboard;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This is a port of MojangFix for Babric.
 * All credits to js6pak and everyone involved in that project.
 * <a href="https://github.com/js6pak/mojangfix">View here</a>
 */
public class MultiplayerScreen extends Screen {
    private final Screen parent;
    private String title;
    private boolean joining;

    private ServerData selectedServer;
    private List<ServerData> serversList;
    private MultiplayerServerListWidget serverListWidget;

    private ButtonWidget buttonEdit;
    private ButtonWidget buttonConnect;
    private ButtonWidget buttonDelete;

    public String tooltipText = null;
    public Object lock = new Object();
    public int serverCount = 0;

    public MultiplayerScreen() {
        this(new TitleScreen());
    }

    public MultiplayerScreen(Screen parent) {
        this.parent = parent;
    }

    public void init() {
        this.title = LanguageManager.getInstance().translate("multiplayer.title");
        this.loadServers();
        this.serverListWidget = new MultiplayerServerListWidget(this);
        this.initButtons();
    }

    private void loadServers() {
        this.selectedServer = null;

        try {
            File serversFile = new File(Minecraft.getRunDirectory(), "servers.dat");
            if (serversFile.exists()) {
                NbtCompound nbt = NbtIo.read(new DataInputStream(Files.newInputStream(serversFile.toPath())));
                this.serversList = ServerData.load(nbt.getList("servers"));
            } else {
                this.serversList = new ArrayList<>();
                this.saveServers();
            }
        } catch (IOException e) {
            BetaQOL.LOGGER.error("Unable to load servers.dat file!");
        }
    }

    public void initButtons() {
        LanguageManager translationStorage = LanguageManager.getInstance();
        this.buttons.add(this.buttonConnect = new CallbackButtonWidget(this.width / 2 - 150 - 4, this.height - 52, 100, 20, translationStorage.translate("multiplayer.connect"),
                button -> this.joinServer(this.selectedServer)));
        this.buttons.add(new CallbackButtonWidget(this.width / 2 - 50, this.height - 52, 100, 20, translationStorage.translate("multiplayer.directConnect"),
                button -> this.minecraft.openScreen(new DirectConnectScreen(this))));
        this.buttons.add(new CallbackButtonWidget(this.width / 2 + 50 + 4, this.height - 52, 100, 20, translationStorage.translate("multiplayer.addServer"),
                button -> this.minecraft.openScreen(new EditServerScreen(this, null))));
        this.buttons.add(this.buttonEdit = new CallbackButtonWidget(this.width / 2 - 154, this.height - 28, 70, 20, translationStorage.translate("multiplayer.edit"),
                button -> this.minecraft.openScreen(new EditServerScreen(this, this.selectedServer))));
        this.buttons.add(this.buttonDelete = new CallbackButtonWidget(this.width / 2 - 74, this.height - 28, 70, 20, translationStorage.translate("selectWorld.delete"), button -> {
            LanguageManager translate = LanguageManager.getInstance();
            this.minecraft.openScreen(new CallbackConfirmScreen(this,
                    translationStorage.translate("multiplayer.deleteConfirm"),
                    "'" + this.selectedServer.name + "' " + translate.translate("selectWorld.deleteWarning"),
                    translate.translate("selectWorld.deleteButton"),
                    translate.translate("gui.cancel"),
                    (result) -> {
                        if (result) {
                            this.deleteServer(this.selectedServer);
                        }

                        this.minecraft.openScreen(this);
                    }));
        }));
        this.buttons.add(new CallbackButtonWidget(this.width / 2 + 4, this.height - 28, 70, 20, translationStorage.translate("multiplayer.refresh"),
                button -> this.minecraft.openScreen(new MultiplayerScreen(this.parent))));
        this.buttons.add(new CallbackButtonWidget(this.width / 2 + 84, this.height - 28, 70, 20, translationStorage.translate("gui.cancel"),
                button -> this.minecraft.openScreen(this.parent)));
        this.buttonConnect.active = false;
        this.buttonEdit.active = false;
        this.buttonDelete.active = false;
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        this.serverListWidget.buttonClicked(button);
    }

    public void selectServer(int slot, boolean join) {
        selectedServer = serversList.get(slot);
        boolean selected = selectedServer != null;

        buttonEdit.active = selected;
        buttonDelete.active = selected;
        buttonConnect.active = selected;

        if (selected && join) {
            joinServer(selectedServer);
        }
    }

    public void joinServer(ServerData server) {
        this.minecraft.openScreen(null);
        if (!this.joining) {
            this.joining = true;
            DirectConnectScreen.connect(this.minecraft, server.ip);
        }
    }

    public void deleteServer(ServerData server) {
        this.serversList.remove(server);
        this.saveServers();
    }

    public void saveServers() {
        try {
            NbtCompound compound = new NbtCompound();
            compound.put("servers", ServerData.save(serversList));
            NbtIo.write(compound, new DataOutputStream(Files.newOutputStream(new File(Minecraft.getRunDirectory(), "servers.dat").toPath())));
        } catch (IOException e) {
            BetaQOL.LOGGER.error("Unable to save servers.dat file!");
        }
    }

    public List<ServerData> getServersList() {
        return this.serversList;
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.tooltipText = null;
        this.serverListWidget.render(mouseX, mouseY, delta);
        this.drawCenteredString(this.textRenderer, this.title, this.width / 2, 20, 16777215);
        super.render(mouseX, mouseY, delta);
        if (this.tooltipText != null) {
            this.drawTooltip(this.tooltipText, mouseX, mouseY);
        }
    }

    @Override
    protected void keyPressed(char chr, int key) {
        super.keyPressed(chr, key);

        /* Refresh */
        if (key == Keyboard.KEY_F5) {
            this.minecraft.openScreen(new MultiplayerScreen(this.parent));
        }

        /* Move servers with SHIFT + (UP/DOWN) */
        if ((key == Keyboard.KEY_UP || key == Keyboard.KEY_DOWN)
                && Keyboard.isKeyDown(BetaQOL.INSTANCE.keybinds.getKeyFromCode(Keyboard.KEY_LSHIFT))
                && this.selectedServer != null) {
            int i = this.serversList.indexOf(this.selectedServer);
            int pos = key == Keyboard.KEY_UP ? -1 : 1;
            if (pos == -1 && i > 0 || pos == 1 && i+pos < this.serversList.size()) {
                Collections.swap(this.serversList, i, i+pos);
                this.saveServers();
            }
        }
    }

    protected void drawTooltip(String string, int i, int j) {
        if (string == null) {
            return;
        }
        int n = i + 12;
        int n2 = j - 12;
        int n3 = this.textRenderer.getWidth(string);
        this.fillGradient(n - 3, n2 - 3, n + n3 + 3, n2 + 8 + 3, -1073741824, -1073741824);
        this.textRenderer.drawWithShadow(string, n, n2, -1);
    }

    public Minecraft getMinecraft() {
        return this.minecraft;
    }

    public TextRenderer getFontRenderer() {
        return this.textRenderer;
    }

    public ServerData getSelectedServer() {
        return this.selectedServer;
    }

    public void pingServer(ServerData server) throws IOException {
        String string = server.ip;
        String[] stringArray = string.split(":");
        if (stringArray.length > 2) {
            stringArray = new String[]{string};
        }
        String address = stringArray[0];
        int port = stringArray.length > 1 ? this.getPort(stringArray[1]) : 25565;
        DataInputStream filterInputStream = null;
        FilterOutputStream filterOutputStream = null;
        Socket sock = new Socket();

        try {
            sock.setSoTimeout(3000);
            sock.setTcpNoDelay(true);
            sock.setTrafficClass(18);
            sock.connect(new InetSocketAddress(address, port), 3000);
            filterInputStream = new DataInputStream(sock.getInputStream());
            filterOutputStream = new DataOutputStream(sock.getOutputStream());
            filterOutputStream.write(254);
            if (filterInputStream.read() != 255) {
                throw new IOException("Bad message");
            }

            String resp = Packet.readString(filterInputStream, 64);
            char[] cArray = resp.toCharArray();
            for (int i = 0; i < cArray.length; i++) {
                if (cArray[i] == '§' || SharedConstants.VALID_CHAT_CHARACTERS.indexOf(cArray[i]) >= 0) continue;
                cArray[i] = 63;
            }

            String motd = new String(cArray);
            stringArray = motd.split("§");
            motd = stringArray[0];

            int players = -1;
            int maxPlayers = -1;
            try {
                players = Integer.parseInt(stringArray[1]);
                maxPlayers = Integer.parseInt(stringArray[2]);
            } catch (Exception exception) {
                // empty catch block
            }

            server.description = "§7" + translateColor(motd);
            server.onlinePlayers = players >= 0 && maxPlayers > 0 ? "§7" + players + "§8/§7" + maxPlayers : "§8???";
        } finally {
            try {
                if (filterInputStream != null) {
                    filterInputStream.close();
                }
            } catch (Throwable ignored) {}
            try {
                if (filterOutputStream != null) {
                    filterOutputStream.close();
                }
            } catch (Throwable ignored) {}
            try {
                (sock).close();
            } catch (Throwable ignored) {}
        }
    }

    private int getPort(String string) {
        try {
            return Integer.parseInt(string.trim());
        } catch (Exception exception) {
            return 25565;
        }
    }

    private String translateColor(String text) {
        char[] b = text.toCharArray();
        for (int i = 0; i < b.length - 1; i++) {
            if (b[i] == '&' && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i+1]) > -1) {
                b[i] = '§';
                b[i+1] = Character.toLowerCase(b[i+1]);
            }
        }
        return new String(b);
    }
}