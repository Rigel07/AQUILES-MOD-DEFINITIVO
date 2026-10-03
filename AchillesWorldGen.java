package com.achilles.world;

import com.achilles.Achilles;
import com.achilles.entity.AchillesStatueEntity;
import com.achilles.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

/**
 * Generacion segura de estatuas. Solo se decide a partir del chunk que acaba de cargar
 * y el spawn se aplaza al siguiente tick del servidor, evitando consultas de entidades
 * durante la generacion de regiones.
 */
@Mod.EventBusSubscriber(modid = Achilles.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class AchillesWorldGen {
    public static final int CELL_CHUNKS = 20;
    private static final int MIN_Y = -60;

    private AchillesWorldGen() {}
    public static void register() {}

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        if (!(event.getChunk() instanceof LevelChunk chunk)) return;
        if (!level.dimension().equals(Level.OVERWORLD)) return;

        int cx = chunk.getPos().x;
        int cz = chunk.getPos().z;
        if (cx == 0 && cz == 0) return;

        int cellX = Math.floorDiv(cx, CELL_CHUNKS);
        int cellZ = Math.floorDiv(cz, CELL_CHUNKS);
        if (Math.floorMod(cx, CELL_CHUNKS) != CELL_CHUNKS / 2
                || Math.floorMod(cz, CELL_CHUNKS) != CELL_CHUNKS / 2) return;

        long seed = level.getSeed() ^ (cellX * 341873128712L)
                ^ (cellZ * 132897987541L) ^ 0xA0C11E5L;
        Random random = new Random(seed);
        if (random.nextInt(100) >= 72) return;

        // Dejar que Minecraft termine de incorporar el chunk y sus entidades.
        level.getServer().execute(() -> spawnIfNeeded(level, chunk.getPos().x, chunk.getPos().z, cellX, cellZ, seed));
    }

    private static void spawnIfNeeded(ServerLevel level, int cx, int cz, int cellX, int cellZ, long seed) {
        if (!level.hasChunk(cx, cz)) return;

        // Esta comprobacion solo mira dentro del chunk que genera la estatua.
        // No fuerza la carga de chunks vecinos.
        int minX = cx << 4;
        int minZ = cz << 4;
        int maxX = minX + 16;
        int maxZ = minZ + 16;
        var existing = level.getEntitiesOfClass(AchillesStatueEntity.class,
                new net.minecraft.world.phys.AABB(minX, level.getMinBuildHeight(), minZ,
                        maxX, level.getMaxBuildHeight(), maxZ));
        if (!existing.isEmpty()) return;

        Random random = new Random(seed);
        int x = minX + 3 + random.nextInt(10);
        int z = minZ + 3 + random.nextInt(10);
        int y = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);
        if (y <= MIN_Y || y >= level.getMaxBuildHeight() - 4) return;

        BlockPos ground = new BlockPos(x, y - 1, z);
        String biome = level.getBiome(ground).unwrapKey()
                .map(k -> k.location().toString()).orElse("");
        if (biome.contains("ocean") || biome.contains("river") || biome.contains("swamp")
                || biome.contains("nether") || biome.contains("end")) return;
        if (!level.getBlockState(ground).isSolid()) return;

        AchillesStatueEntity statue = ModEntities.STATUE.get().create(level);
        if (statue == null) return;
        statue.moveTo(x + 0.5D, y, z + 0.5D, random.nextFloat() * 360F, 0F);
        level.addFreshEntity(statue);
    }
}
