package me.kimovoid.betaqol.mixin.feature.tablist;

import me.kimovoid.betaqol.BetaQOL;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GameGui;
import net.minecraft.client.gui.GuiElement;
import net.minecraft.client.render.TextRenderer;
import net.minecraft.client.render.Window;
import org.lwjgl.input.Keyboard;
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
            this.renderTabList();
        }
    }

    @Unique
    private void renderTabList() {
        Minecraft mc = this.minecraft;
        TextRenderer tr = mc.textRenderer;

        List<String> players = new ArrayList<>();
        int width = 0;

        for (Map.Entry<String, Integer> entry : BetaQOL.INSTANCE.tabPlayers.entrySet()) {
            int stringWidth = tr.getWidth(entry.getKey() + 14);
            players.add(entry.getKey());
            if (stringWidth > width) width = stringWidth;
        }

        Window scaled = new Window(mc.options, mc.width, mc.height);
        int minWidth = scaled.getWidth() / 2 - width / 2 - 2;
        int maxWidth = scaled.getWidth() / 2 + width / 2 - 2;

        this.fill(minWidth - 1, 9, maxWidth + 4, 10 + players.size() * 9, Integer.MIN_VALUE);

        for (int i = 0; i < players.size(); i++) {
            int ping = BetaQOL.INSTANCE.tabPlayers.get(players.get(i));
            byte texShift;
            if(ping < 0) {
                texShift = 5;
            } else if(ping < 150) {
                texShift = 0;
            } else if(ping < 300) {
                texShift = 1;
            } else if(ping < 600) {
                texShift = 2;
            } else if(ping < 1000) {
                texShift = 3;
            } else {
                texShift = 4;
            }

            int height = 10 + (9 * i);
            fill(minWidth, height, maxWidth + 3, 18 + (9 * i), 553648127);
            tr.drawWithShadow(players.get(i), minWidth, height, 16777215);

            mc.textureManager.bind(mc.textureManager.load("/assets/betaqol/tablist/icons.png"));

            this.drawOffset += 100.0F;
            this.drawTexture(maxWidth - 8, height, 0, 176 + texShift * 8, 10, 8);
            this.drawOffset -= 100.0F;
        }
    }
}
