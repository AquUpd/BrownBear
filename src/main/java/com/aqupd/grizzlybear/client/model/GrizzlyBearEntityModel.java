//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.aqupd.grizzlybear.client.model;

import com.aqupd.grizzlybear.entities.GrizzlyBearEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.QuadrupedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

@Environment(EnvType.CLIENT)
public class GrizzlyBearEntityModel<T extends GrizzlyBearEntity> extends QuadrupedModel<T> {
    public GrizzlyBearEntityModel(ModelPart root) {
        super(root, true, 16.0F, 4.0F, 2.25F, 2.0F, 24);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        modelPartData.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-3.5F, -3.0F, -3.0F, 7.0F, 7.0F, 7.0F).texOffs(0, 44).addBox("mouth", -2.5F, 1.0F, -6.0F, 5.0F, 3.0F, 3.0F).texOffs(26, 0).addBox("right_ear", -4.5F, -4.0F, -1.0F, 2.0F, 2.0F, 1.0F).texOffs(26, 0).mirror().addBox("left_ear", 2.5F, -4.0F, -1.0F, 2.0F, 2.0F, 1.0F), PartPose.offset(0.0F, 10.0F, -16.0F));
        modelPartData.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 19).addBox(-5.0F, -13.0F, -7.0F, 14.0F, 14.0F, 11.0F).texOffs(39, 0).addBox(-4.0F, -25.0F, -7.0F, 12.0F, 12.0F, 10.0F), PartPose.offsetAndRotation(-2.0F, 9.0F, 12.0F, 1.5707964F, 0.0F, 0.0F));
        CubeListBuilder modelPartBuilder = CubeListBuilder.create().texOffs(50, 22).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 10.0F, 8.0F);
        modelPartData.addOrReplaceChild("right_hind_leg", modelPartBuilder, PartPose.offset(-4.5F, 14.0F, 6.0F));
        modelPartData.addOrReplaceChild("left_hind_leg", modelPartBuilder, PartPose.offset(4.5F, 14.0F, 6.0F));
        CubeListBuilder modelPartBuilder2 = CubeListBuilder.create().texOffs(50, 40).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 10.0F, 6.0F);
        modelPartData.addOrReplaceChild("right_front_leg", modelPartBuilder2, PartPose.offset(-3.5F, 14.0F, -8.0F));
        modelPartData.addOrReplaceChild("left_front_leg", modelPartBuilder2, PartPose.offset(3.5F, 14.0F, -8.0F));
        return LayerDefinition.create(modelData, 128, 64);
    }

    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        float f = ageInTicks - (float) entity.tickCount;
        float g = entity.getStandingAnimationScale(f);
        g *= g;
        float h = 1.0f - g;
        this.body.xRot = 1.5707964f - g * (float)Math.PI * 0.35f;
        this.body.y = 9.0f * h + 11.0f * g;
        this.rightFrontLeg.y = 14.0f * h - 6.0f * g;
        this.rightFrontLeg.z = -8.0f * h - 4.0f * g;
        this.rightFrontLeg.xRot -= g * (float)Math.PI * 0.45f;
        this.leftFrontLeg.y = this.rightFrontLeg.y;
        this.leftFrontLeg.z = this.rightFrontLeg.z;
        this.leftFrontLeg.xRot -= g * (float)Math.PI * 0.45f;
        if (this.young) {
            this.head.y = 10.0f * h - 9.0f * g;
            this.head.z = -16.0f * h - 7.0f * g;
        } else {
            this.head.y = 10.0f * h - 14.0f * g;
            this.head.z = -16.0f * h - 3.0f * g;
        }
        this.head.xRot += g * (float)Math.PI * 0.15f;
    }
}
