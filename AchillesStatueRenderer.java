package com.achilles.client;

import com.achilles.Achilles;
import com.achilles.entity.AchillesStatueEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class AchillesStatueRenderer extends EntityRenderer<AchillesStatueEntity> {
    private static final ResourceLocation TEXTURE=new ResourceLocation(Achilles.MOD_ID,"textures/entity/achilles_statue.png");
    private final AchillesStatueModel model;
    public AchillesStatueRenderer(EntityRendererProvider.Context c){super(c); model=new AchillesStatueModel(c.bakeLayer(AchillesStatueModel.LAYER)); this.shadowRadius=.35F;}
    @Override public void render(AchillesStatueEntity e,float yaw,float partial,PoseStack ps,MultiBufferSource buffers,int light){
        ps.pushPose();
        ps.translate(0,0.02,0);
        var vc=buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        model.setupAnim(e,0,0,e.tickCount+partial,yaw,0);
        model.renderToBuffer(ps,vc,light,net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY,1,1,1,1);
        ps.popPose();
        super.render(e,yaw,partial,ps,buffers,light);
    }
    @Override public ResourceLocation getTextureLocation(AchillesStatueEntity e){return TEXTURE;}
}
