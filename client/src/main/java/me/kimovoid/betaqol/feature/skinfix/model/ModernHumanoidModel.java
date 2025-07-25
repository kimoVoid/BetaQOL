package me.kimovoid.betaqol.feature.skinfix.model;

import me.kimovoid.betaqol.feature.skinfix.mixininterface.ModelPartAccessor;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.client.render.model.entity.HumanoidModel;

public class ModernHumanoidModel extends HumanoidModel {

    public final ModelPart leftArmLayer;
    public final ModelPart rightArmLayer;
    public final ModelPart leftLegLayer;
    public final ModelPart rightLegLayer;
    public final ModelPart bodyLayer;

    public ModernHumanoidModel(float scale, boolean isSlim) {
        super(scale);

        if (isSlim) {
            this.leftArm = this.createModelPart(32, 48);
            this.leftArm.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, scale);
            this.leftArm.setPivot(5.0F, 2.5F, 0.0F);

            this.rightArm = this.createModelPart(40, 16);
            this.rightArm.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, scale);
            this.rightArm.setPivot(-5.0F, 2.5F, 0.0F);

            this.leftArmLayer = this.createModelPart(48, 48);
            this.leftArmLayer.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, scale + 0.25F);
            this.leftArmLayer.setPivot(5.0F, 2.5F, 0.0F);

            this.rightArmLayer = this.createModelPart(40, 32);
            this.rightArmLayer.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, scale + 0.25F);
            this.rightArmLayer.setPivot(-5.0F, 2.5F, 10.0F);
        } else {
            this.leftArm = this.createModelPart(32, 48);
            this.leftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, scale);
            this.leftArm.setPivot(5.0F, 2.0F, 0.0F);

            this.leftArmLayer = this.createModelPart(48, 48);
            this.leftArmLayer.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, scale + 0.25F);
            this.leftArmLayer.setPivot(5.0F, 2.0F, 0.0F);

            this.rightArmLayer = this.createModelPart(40, 32);
            this.rightArmLayer.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, scale + 0.25F);
            this.rightArmLayer.setPivot(-5.0F, 2.0F, 10.0F);
        }

        this.leftLeg = this.createModelPart(16, 48);
        this.leftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, scale);
        this.leftLeg.setPivot(2.0F, 12.0F, 0.0F);

        this.leftLegLayer = this.createModelPart(0, 48);
        this.leftLegLayer.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, scale + 0.25F);
        this.leftLegLayer.setPivot(1.9F, 12.0F, 0.0F);

        this.rightLegLayer = this.createModelPart(0, 32);
        this.rightLegLayer.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, scale + 0.25F);
        this.rightLegLayer.setPivot(-1.9F, 12.0F, 0.0F);

        this.bodyLayer = this.createModelPart(16, 32);
        this.bodyLayer.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, scale + 0.25F);
        this.bodyLayer.setPivot(0.0F, 0.0F, 0.0F);
    }

    private ModelPart createModelPart(int x, int y) {
        ModelPart modelPart = new ModelPart(x, y);
        ((ModelPartAccessor)modelPart).setTextureHeight(64);
        return modelPart;
    }

    @Override
    public void render(float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch, float scale) {
        super.render(limbAngle, limbDistance, animationProgress, headYaw, headPitch, scale);

        this.leftLegLayer.render(scale);
        this.rightLegLayer.render(scale);
        this.leftArmLayer.render(scale);
        this.rightArmLayer.render(scale);
        this.bodyLayer.render(scale);
    }

    public void updatePositionAndRotation(ModelPart from, ModelPart to) {
        to.setPivot(from.pivotX, from.pivotY, from.pivotZ);
        to.rotationX = from.rotationX;
        to.rotationY = from.rotationY;
        to.rotationZ = from.rotationZ;
    }

    @Override
    public void setAngles(float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch, float scale) {
        super.setAngles(limbAngle, limbDistance, animationProgress, headYaw, headPitch, scale);
        this.updatePositionAndRotation(this.leftLeg, this.leftLegLayer);
        this.updatePositionAndRotation(this.rightLeg, this.rightLegLayer);
        this.updatePositionAndRotation(this.leftArm, this.leftArmLayer);
        this.updatePositionAndRotation(this.rightArm, this.rightArmLayer);
        this.updatePositionAndRotation(this.body, this.bodyLayer);
        this.updatePositionAndRotation(this.head, this.hat);
    }
}