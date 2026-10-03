package com.achilles.entity;

import com.achilles.Achilles;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModSoundEvents {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Achilles.MOD_ID);
    public static final RegistryObject<SoundEvent> AQUILLES_CUTE = SOUNDS.register("aquilles_cute",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Achilles.MOD_ID, "aquilles_cute")));
    public static final RegistryObject<SoundEvent> AQUILLES_HURT = SOUNDS.register("aquilles_hurt",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Achilles.MOD_ID, "aquilles_hurt")));
    private ModSoundEvents() {}
}
