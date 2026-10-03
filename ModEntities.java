package com.achilles.registry;

import com.achilles.Achilles;
import com.achilles.entity.AchillesEntity;
import com.achilles.entity.AchillesStatueEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Achilles.MOD_ID);

    public static final RegistryObject<EntityType<AchillesStatueEntity>> STATUE = ENTITIES.register("achilles_statue",
            () -> EntityType.Builder.of(AchillesStatueEntity::new, MobCategory.MISC)
                    .sized(0.9F, 2.7F).clientTrackingRange(32).updateInterval(20).build("achilles:achilles_statue"));

    public static final RegistryObject<EntityType<AchillesEntity>> AQUILLES = ENTITIES.register("aquilles",
            () -> EntityType.Builder.of(AchillesEntity::new, MobCategory.CREATURE)
                    .sized(0.55F, 0.85F).clientTrackingRange(32).build("achilles:aquilles"));

    private ModEntities() {}
}
