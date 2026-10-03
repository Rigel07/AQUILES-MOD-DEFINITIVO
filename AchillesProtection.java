package com.achilles.world;

import com.achilles.entity.AchillesStatueEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.achilles.Achilles;
import net.minecraft.world.phys.AABB;

import java.util.Comparator;

@Mod.EventBusSubscriber(modid = Achilles.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class AchillesProtection {
    private static final String DATA = "AchillesData";
    private static final String POINTS = "Points";
    private static final int MAX_POINTS = 100000;

    private AchillesProtection() {}

    private static CompoundTag data(Player player) { return player.getPersistentData().getCompound(DATA); }

    public static int getPoints(Player player) { return data(player).getInt(POINTS); }

    public static void addPoint(Player player) {
        CompoundTag d = data(player);
        d.putInt(POINTS, Math.min(MAX_POINTS, d.getInt(POINTS) + 1));
        player.getPersistentData().put(DATA, d);
    }

    public static boolean spendPoint(Player player) {
        CompoundTag d = data(player);
        int p = d.getInt(POINTS);
        if (p <= 0) return false;
        d.putInt(POINTS, p - 1);
        player.getPersistentData().put(DATA, d);
        return true;
    }

    public static AchillesStatueEntity nearestOwned(ServerPlayer player) {
        return player.serverLevel().getEntitiesOfClass(AchillesStatueEntity.class,
                player.getBoundingBox().inflate(48), s -> s.isAlive() && s.isOwner(player))
                .stream().min(Comparator.comparingDouble(s -> s.distanceToSqr(player))).orElse(null);
    }

    public static boolean isProtected(ServerPlayer player) {
        for (AchillesStatueEntity statue : player.serverLevel().getEntitiesOfClass(AchillesStatueEntity.class,
                player.getBoundingBox().inflate(128), s -> s.isAlive() && s.isClaimed())) {
            if (statue.isAllowed(player) && statue.distanceToSqr(player) <= statue.getProtectionRadius() * statue.getProtectionRadius()) return true;
        }
        return false;
    }

    public static boolean isProtectedPosition(ServerLevel level, BlockPos pos, @org.jetbrains.annotations.Nullable Player player) {
        double max = 95.0D;
        AABB box = new AABB(pos).inflate(max);
        for (AchillesStatueEntity statue : level.getEntitiesOfClass(AchillesStatueEntity.class, box,
                s -> s.isAlive() && s.isClaimed())) {
            double r = statue.getProtectionRadius();
            if (statue.distanceToSqr(pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5) <= r * r) {
                if (player == null || !statue.isAllowed(player)) return true;
            }
        }
        return false;
    }

    public static void tickStatue(AchillesStatueEntity statue) {
        if (!(statue.level() instanceof ServerLevel level) || !statue.isClaimed()) return;
        double radius = statue.getProtectionRadius();
        AABB box = statue.getBoundingBox().inflate(radius);

        for (ServerPlayer player : level.getEntitiesOfClass(ServerPlayer.class, box,
                p -> statue.isAllowed(p))) {
            if (player.getHealth() < player.getMaxHealth()) {
                player.heal(statue.getHealAmount());
            }
        }

        for (Mob mob : level.getEntitiesOfClass(Mob.class, box, m -> isDangerous(m) && m.isAlive())) {
            double dist = mob.distanceTo(statue);
            if (dist <= radius) {
                if (mob.getTarget() instanceof Player) mob.setTarget(null);
                double dx = mob.getX() - statue.getX();
                double dz = mob.getZ() - statue.getZ();
                double len = Math.sqrt(dx * dx + dz * dz);
                if (len < 0.01) { dx = 1; dz = 0; len = 1; }
                double safe = radius + 2.0;
                double x = statue.getX() + dx / len * safe;
                double z = statue.getZ() + dz / len * safe;
                mob.teleportTo(x, mob.getY(), z);
                mob.getNavigation().stop();
            }
        }
    }

    private static boolean isDangerous(Mob mob) {
        return mob instanceof Monster || mob.getType().getCategory() == net.minecraft.world.entity.MobCategory.MONSTER;
    }

    @SubscribeEvent
    public static void onAttack(LivingAttackEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (isProtected(player)) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onBreak(BlockEvent.BreakEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) return;
        if (isProtectedPosition(player.serverLevel(), event.getPos(), player)) {
            event.setCanceled(true);
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§6✦ Esta zona está protegida por Aquiles."));
        }
    }

    @SubscribeEvent
    public static void onPlace(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (isProtectedPosition(player.serverLevel(), event.getBlockSnapshot().getPos(), player)) {
            event.setCanceled(true);
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§6✦ No puedes construir dentro de una zona sagrada ajena."));
        }
    }
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide) return;
        if (!(event.player instanceof ServerPlayer player)) return;
        if (player.tickCount % 2 != 0) return;

        ServerLevel level = player.serverLevel();
        AABB search = player.getBoundingBox().inflate(96.0D);
        for (AchillesStatueEntity statue : level.getEntitiesOfClass(AchillesStatueEntity.class, search,
                s -> s.isAlive() && s.isClaimed())) {
            double radius = statue.getProtectionRadius();
            double dx = player.getX() - statue.getX();
            double dz = player.getZ() - statue.getZ();
            double horizontal = Math.sqrt(dx * dx + dz * dz);
            if (horizontal >= radius || statue.isAllowed(player)) continue;

            double safeRadius = radius + 1.5D;
            if (horizontal < 0.01D) {
                dx = 1.0D;
                dz = 0.0D;
                horizontal = 1.0D;
            }
            double x = statue.getX() + dx / horizontal * safeRadius;
            double z = statue.getZ() + dz / horizontal * safeRadius;
            player.teleportTo(x, player.getY(), z);
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                    "§6✦ Esta estatua pertenece a otro jugador. Necesitas su permiso para entrar."));
            break;
        }
    }

}
