package com.achilles.client;

import com.achilles.Achilles;
import com.achilles.registry.ModEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Achilles.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientSetup {
    private ClientSetup() {}
    public static void init() {}

    @SubscribeEvent
    public static void layers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(AquillesModel.LAYER, AquillesModel::createBodyLayer);
        event.registerLayerDefinition(AchillesStatueModel.LAYER, AchillesStatueModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void renderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.AQUILLES.get(), AquillesRenderer::new);
        event.registerEntityRenderer(ModEntities.STATUE.get(), AchillesStatueRenderer::new);
    }
}
