package com.achilles.client;

import com.achilles.Achilles;
import com.achilles.entity.AchillesStatueEntity;
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

public class AchillesStatueModel extends EntityModel<AchillesStatueEntity> {
    public static final ModelLayerLocation LAYER=new ModelLayerLocation(new ResourceLocation(Achilles.MOD_ID,"statue"),"main");
    private final ModelPart root,head,body,armR,armL,legR,legL,crest;
    public AchillesStatueModel(ModelPart root){this.root=root;head=root.getChild("head");body=root.getChild("body");armR=root.getChild("arm_r");armL=root.getChild("arm_l");legR=root.getChild("leg_r");legL=root.getChild("leg_l");crest=root.getChild("crest");}
    public static LayerDefinition createBodyLayer(){
        MeshDefinition m=new MeshDefinition(); PartDefinition p=m.getRoot();
        p.addOrReplaceChild("head",CubeListBuilder.create().texOffs(0,0).addBox(-4,-8,-4,8,8,8),PartPose.offset(0,2,0));
        p.addOrReplaceChild("crest",CubeListBuilder.create().texOffs(0,16).addBox(-1,-4,-1,2,4,2),PartPose.offset(0,-5,0));
        p.addOrReplaceChild("body",CubeListBuilder.create().texOffs(0,22).addBox(-4,0,-2.5F,8,9,5),PartPose.offset(0,10,0));
        p.addOrReplaceChild("arm_r",CubeListBuilder.create().texOffs(26,22).addBox(-1,0,-1.5F,2,9,3),PartPose.offset(-5,10,0));
        p.addOrReplaceChild("arm_l",CubeListBuilder.create().texOffs(36,22).addBox(-1,0,-1.5F,2,9,3),PartPose.offset(5,10,0));
        p.addOrReplaceChild("leg_r",CubeListBuilder.create().texOffs(46,22).addBox(-1.5F,0,-1.5F,3,8,3),PartPose.offset(-2,19,0));
        p.addOrReplaceChild("leg_l",CubeListBuilder.create().texOffs(58,22).addBox(-1.5F,0,-1.5F,3,8,3),PartPose.offset(2,19,0));
        return LayerDefinition.create(m,64,64);
    }
    @Override public void setupAnim(AchillesStatueEntity e,float a,float b,float c,float d,float f){}
    @Override public void renderToBuffer(PoseStack ps,VertexConsumer vc,int light,int overlay,float r,float g,float b,float a){root.render(ps,vc,light,overlay,r,g,b,a);}
}
