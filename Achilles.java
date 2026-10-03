package com.achilles;

import com.achilles.command.AchillesCommands;
import com.achilles.entity.AchillesEntity;
import com.achilles.entity.AchillesStatueEntity;
import com.achilles.registry.ModEntities;
import com.achilles.world.AchillesProtection;
import com.achilles.world.AchillesWorldGen;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Achilles.MOD_ID)
public class Achilles {
    public static final String MOD_ID = "achilles";

    public Achilles() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModEntities.ENTITIES.register(bus);
        com.achilles.entity.ModSoundEvents.SOUNDS.register(bus);
        bus.addListener(this::commonSetup);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> com.achilles.client.ClientSetup::init);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            SpawnPlacements.register(
                    ModEntities.AQUILLES.get(),
                    SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    (type, level, reason, pos, random) ->
                            level.getBlockState(pos.below()).isSolidRender(level, pos.below())
                                    && level.getMaxLocalRawBrightness(pos) > 8
            );
            AchillesWorldGen.register();
        });
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static final class ModBusEvents {
        @SubscribeEvent
        public static void attributes(EntityAttributeCreationEvent event) {
            event.put(ModEntities.AQUILLES.get(), AchillesEntity.createAttributes().build());
        }
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static final class ForgeEvents {
        @SubscribeEvent
        public static void commands(RegisterCommandsEvent event) {
            AchillesCommands.register(event.getDispatcher());
        }
    }
}
