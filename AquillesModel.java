package com.achilles.client;

import com.achilles.Achilles;
import com.achilles.entity.AchillesEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class AquillesModel extends EntityModel<AchillesEntity> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(Achilles.MOD_ID, "aquilles"), "main");
    private final ModelPart root, head, body, armR, armL, legR, legL, helmet;

    public AquillesModel(ModelPart root) {
        this.root = root; head = root.getChild("head"); body = root.getChild("body");
        armR = root.getChild("arm_r"); armL = root.getChild("arm_l");
        legR = root.getChild("leg_r"); legL = root.getChild("leg_l"); helmet = root.getChild("helmet");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition(); PartDefinition p = mesh.getRoot();
        p.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0,0).addBox(-4,-7,-4,8,8,8), PartPose.offset(0,10,0));
        p.addOrReplaceChild("helmet", CubeListBuilder.create().texOffs(0,16).addBox(-4.5F,-1,-4.5F,9,2,9), PartPose.offset(0,4,0));
        p.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0,28).addBox(-3,-1,-2,6,7,4), PartPose.offset(0,13,0));
        p.addOrReplaceChild("arm_r", CubeListBuilder.create().texOffs(20,28).addBox(-1,0,-1,2,6,2), PartPose.offset(-4,13,0));
        p.addOrReplaceChild("arm_l", CubeListBuilder.create().texOffs(28,28).addBox(-1,0,-1,2,6,2), PartPose.offset(4,13,0));
        p.addOrReplaceChild("leg_r", CubeListBuilder.create().texOffs(36,28).addBox(-1,0,-1,2,4,2), PartPose.offset(-1.5F,20,0));
        p.addOrReplaceChild("leg_l", CubeListBuilder.create().texOffs(44,28).addBox(-1,0,-1,2,4,2), PartPose.offset(1.5F,20,0));
        return LayerDefinition.create(mesh,64,64);
    }

    @Override public void setupAnim(AchillesEntity e,float limbSwing,float limbSwingAmount,float age,float yaw,float pitch) {
        float d=Mth.DEG_TO_RAD; head.yRot=yaw*d; head.xRot=pitch*d; helmet.yRot=head.yRot;
        armR.xRot=Mth.cos(limbSwing*.9F+Mth.PI)*1.15F*limbSwingAmount;
        armL.xRot=Mth.cos(limbSwing*.9F)*1.15F*limbSwingAmount;
        legR.xRot=Mth.cos(limbSwing*.9F)*1.2F*limbSwingAmount;
        legL.xRot=Mth.cos(limbSwing*.9F+Mth.PI)*1.2F*limbSwingAmount;
        float wobble=Mth.sin(age*.12F)*.08F; body.zRot=wobble; head.zRot=wobble*.7F;
    }
    @Override public void renderToBuffer(PoseStack ps, VertexConsumer vc,int light,int overlay,float r,float g,float b,float a){root.render(ps,vc,light,overlay,r,g,b,a);}
}
