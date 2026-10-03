package com.achilles.client;

import com.achilles.Achilles;
import com.achilles.entity.AchillesEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class AquillesRenderer extends MobRenderer<AchillesEntity,AquillesModel> {
    private static final ResourceLocation TEXTURE=new ResourceLocation(Achilles.MOD_ID,"textures/entity/aquilles.png");
    public AquillesRenderer(EntityRendererProvider.Context c){super(c,new AquillesModel(c.bakeLayer(AquillesModel.LAYER)),0.2F);}
    @Override public ResourceLocation getTextureLocation(AchillesEntity e){return TEXTURE;}
}
