package me.kimovoid.betaqol.mixin.feature.tablist;

import me.kimovoid.betaqol.BetaQOL;
import me.kimovoid.betaqol.feature.tablist.PlayerInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GameGui;
import net.minecraft.client.gui.GuiElement;
import net.minecraft.client.render.Window;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(GameGui.class)
public abstract class GameGuiMixin extends GuiElement {

    @Shadow private Minecraft minecraft;

    @Inject(method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glEnable(I)V",
                    shift = At.Shift.BEFORE,
                    ordinal = 6,
                    remap = false
            )
    )
    public void injectTab(CallbackInfo ci) {
        if (!this.minecraft.focussed) {
            return;
        }

        if (Keyboard.isKeyDown(BetaQOL.INSTANCE.keybinds.getKeyByName("key.playerlist"))) {
            if (BetaQOL.INSTANCE.tabPlayers.isEmpty()) {
                return;
            }
            this.renderTabList(this.minecraft);
        }
    }

    @Unique
    private void renderTabList(Minecraft mc) {
        Window scaled = new Window(mc.options, mc.width, mc.height);
        List<PlayerInfo> players = new ArrayList<>();
        int rowWidth = 0;
        for (Map.Entry<String, Integer> entry : BetaQOL.INSTANCE.tabPlayers.entrySet()) {
            int stringWidth = mc.textRenderer.getWidth(entry.getKey()) + 14;
            players.add(new PlayerInfo(entry.getKey(), entry.getValue()));
            if (stringWidth > rowWidth) rowWidth = stringWidth;
        }

        int playerCount;
        int size = playerCount = players.size();
        int columns = 1;
        while (size > 20) {
            size = (playerCount + ++columns - 1) / columns;
        }
        int x = (scaled.getWidth() - columns * rowWidth) / 2;
        int y = 10;
        this.fill(x - 1, y - 1, x + rowWidth * columns, y + 9 * size, Integer.MIN_VALUE);

        for (int i = 0; i < playerCount; ++i) {
            int n2 = x + i % columns * rowWidth;
            int n = y + i / columns * 9;
            this.fill(n2, n, n2 + rowWidth - 1, n + 8, 0x20FFFFFF);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GL11.glEnable(3008);
            if (i >= players.size()) continue;
            PlayerInfo playerInfo = players.get(i);
            mc.textRenderer.drawWithShadow(playerInfo.playerName, n2, n, 0xFFFFFF);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            mc.textureManager.bind(mc.textureManager.load("/assets/betaqol/tablist/icons.png"));
            int posY = playerInfo.ping < 0 ? 5 : (playerInfo.ping < 150 ? 0 : (playerInfo.ping < 300 ? 1 : (playerInfo.ping < 600 ? 2 : (playerInfo.ping < 1000 ? 3 : 4))));
            this.drawOffset += 100.0f;
            this.drawTexture(n2 + rowWidth - 12, n, 0, 176 + posY * 8, 10, 8);
            this.drawOffset -= 100.0f;
        }
    }
}