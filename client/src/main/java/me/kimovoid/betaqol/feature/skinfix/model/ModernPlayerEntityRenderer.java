package me.kimovoid.betaqol.feature.skinfix.model;

import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.model.entity.HumanoidModel;
import net.minecraft.entity.living.player.PlayerEntity;
import org.lwjgl.opengl.GL11;

public class ModernPlayerEntityRenderer extends PlayerEntityRenderer {

    public ModernPlayerEntityRenderer(EntityRenderDispatcher dispatcher, boolean slim) {
        super();
        setEntityRenderDispatcher(dispatcher);
        this.model = new ModernHumanoidModel(0.0F, slim);
        this.handmodel = (HumanoidModel) this.model;
    }

    @Override
    public void render(PlayerEntity playerEntity, double d, double e, double f, float g, float h) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        super.render(playerEntity, d, e, f, g, h);
        GL11.glDisable(GL11.GL_BLEND);
    }

    @Override
    public void renderPlayerRightHandModel() {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        super.renderPlayerRightHandModel();
        ((ModernHumanoidModel)this.handmodel).rightArmLayer.render(0.0625F);
        GL11.glDisable(GL11.GL_BLEND);
    }
}