package me.kimovoid.betaqol.feature.skinfix;

import me.kimovoid.betaqol.feature.skinfix.mixininterface.ModelPartAccessor;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.client.render.model.entity.HumanoidModel;

/**
 * This is a port of MojangFix for Babric.
 * All credits to js6pak and everyone involved in that project.
 * <a href="https://github.com/js6pak/mojangfix">View here</a>
 */
public class PlayerEntityModel extends HumanoidModel {
    public final ModelPart leftSleeve;
    public final ModelPart rightSleeve;
    public final ModelPart leftPantLeg;
    public final ModelPart rightPantLeg;
    public final ModelPart jacket;

    public PlayerEntityModel(float scale, boolean thinArms) {
        super(scale);
        if (thinArms) {
            this.leftArm = this.createModelPart(32, 48);
            this.leftArm.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, scale);
            this.leftArm.setPivot(5.0F, 2.5F, 0.0F);
            this.rightArm = this.createModelPart(40, 16);
            this.rightArm.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, scale);
            this.rightArm.setPivot(-5.0F, 2.5F, 0.0F);
            this.leftSleeve = this.createModelPart(48, 48);
            this.leftSleeve.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, scale + 0.25F);
            this.leftSleeve.setPivot(5.0F, 2.5F, 0.0F);
            this.rightSleeve = this.createModelPart(40, 32);
            this.rightSleeve.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, scale + 0.25F);
            this.rightSleeve.setPivot(-5.0F, 2.5F, 10.0F);
        } else {
            this.leftArm = this.createModelPart(32, 48);
            this.leftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, scale);
            this.leftArm.setPivot(5.0F, 2.0F, 0.0F);
            this.leftSleeve = this.createModelPart(48, 48);
            this.leftSleeve.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, scale + 0.25F);
            this.leftSleeve.setPivot(5.0F, 2.0F, 0.0F);
            this.rightSleeve = this.createModelPart(40, 32);
            this.rightSleeve.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, scale + 0.25F);
            this.rightSleeve.setPivot(-5.0F, 2.0F, 10.0F);
        }

        this.leftLeg = this.createModelPart(16, 48);
        this.leftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, scale);
        this.leftLeg.setPivot(2.0F, 12.0F, 0.0F);
        this.leftPantLeg = this.createModelPart(0, 48);
        this.leftPantLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, scale + 0.25F);
        this.leftPantLeg.setPivot(1.9F, 12.0F, 0.0F);
        this.rightPantLeg = this.createModelPart(0, 32);
        this.rightPantLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, scale + 0.25F);
        this.rightPantLeg.setPivot(-1.9F, 12.0F, 0.0F);
        this.jacket = this.createModelPart(16, 32);
        this.jacket.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, scale + 0.25F);
        this.jacket.setPivot(0.0F, 0.0F, 0.0F);
    }

    private ModelPart createModelPart(int x, int y) {
        ModelPart modelRenderer = new ModelPart(x, y);
        ((ModelPartAccessor) modelRenderer).setTextureHeight(64);
        return modelRenderer;
    }

    @Override
    public void render(float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch, float scale) {
        super.render(limbAngle, limbDistance, animationProgress, headYaw, headPitch, scale);

        this.leftPantLeg.render(scale);
        this.rightPantLeg.render(scale);
        this.leftSleeve.render(scale);
        this.rightSleeve.render(scale);
        this.jacket.render(scale);
    }

    public void copyPositionAndRotation(ModelPart from, ModelPart to) {
        to.setPivot(from.pivotX, from.pivotY, from.pivotZ);
        to.rotationX = from.rotationX;
        to.rotationY = from.rotationY;
        to.rotationZ = from.rotationZ;
    }

    @Override
    public void setAngles(float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch, float scale) {
        super.setAngles(limbAngle, limbDistance, animationProgress, headYaw, headPitch, scale);
        this.copyPositionAndRotation(this.leftLeg, this.leftPantLeg);
        this.copyPositionAndRotation(this.rightLeg, this.rightPantLeg);
        this.copyPositionAndRotation(this.leftArm, this.leftSleeve);
        this.copyPositionAndRotation(this.rightArm, this.rightSleeve);
        this.copyPositionAndRotation(this.body, this.jacket);
        this.copyPositionAndRotation(this.head, this.hat);
    }
}
