package me.kimovoid.betaqol.feature.fov;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.lwjgl.opengl.GL11;

public class FOVOptionSlider extends ButtonWidget {

    public float value;
    public boolean dragging = false;

    public FOVOptionSlider(int id, int x, int y, String string, float value) {
        super(id, x, y, 150, 20, string);
        this.value = value;
    }

    protected int getYImage(boolean hovered) {
        return 0;
    }

    protected void renderBackground(Minecraft minecraft, int i, int j) {
        if (!this.visible) {
            return;
        }
        if (this.dragging) {
            this.value = (float)(i - (this.x + 4)) / (float)(this.width - 8);
            if (this.value < 0.0f) {
                this.value = 0.0f;
            }
            if (this.value > 1.0f) {
                this.value = 1.0f;
            }
            FOVOption.fov = this.value;
            this.message = FOVOption.getLocalizedFov();
        }
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.drawTexture(this.x + (int)(this.value * (float)(this.width - 8)), this.y, 0, 66, 4, 20);
        this.drawTexture(this.x + (int)(this.value * (float)(this.width - 8)) + 4, this.y, 196, 66, 4, 20);
    }

    public boolean isMouseOver(Minecraft minecraft, int i, int j) {
        if (super.isMouseOver(minecraft, i, j)) {
            this.value = (float)(i - (this.x + 4)) / (float)(this.width - 8);
            if (this.value < 0.0f) {
                this.value = 0.0f;
            }
            if (this.value > 1.0f) {
                this.value = 1.0f;
            }
            FOVOption.fov = this.value;
            this.message = FOVOption.getLocalizedFov();

            this.dragging = true;
            return true;
        }
        return false;
    }

    public void mouseReleased(int mouseX, int mouseY) {
        this.dragging = false;
    }
}
