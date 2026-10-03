package com.achilles.entity;

import com.achilles.Achilles;
import com.achilles.world.AchillesProtection;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Estatua inmortal de Aquiles. Es una entidad en vez de un bloque para que nunca pueda romperse
 * mediante herramientas, explosiones o interacciones de bloques.
 */
public class AchillesStatueEntity extends Entity {
    private static final EntityDataAccessor<Integer> RADIUS_LEVEL = SynchedEntityData.defineId(AchillesStatueEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> HEAL_LEVEL = SynchedEntityData.defineId(AchillesStatueEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> CLAIMED = SynchedEntityData.defineId(AchillesStatueEntity.class, EntityDataSerializers.BOOLEAN);
    private UUID owner;
    private final List<UUID> trusted = new ArrayList<>();

    public AchillesStatueEntity(EntityType<? extends AchillesStatueEntity> type, Level level) {
        super(type, level);
        this.setInvulnerable(true);
        this.noPhysics = true;
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(RADIUS_LEVEL, 0);
        entityData.define(HEAL_LEVEL, 0);
        entityData.define(CLAIMED, false);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.hasUUID("Owner")) owner = tag.getUUID("Owner");
        entityData.set(RADIUS_LEVEL, Math.max(0, Math.min(100, tag.getInt("RadiusLevel"))));
        entityData.set(HEAL_LEVEL, Math.max(0, Math.min(100, tag.getInt("HealLevel"))));
        entityData.set(CLAIMED, owner != null);
        trusted.clear();
        if (tag.contains("Trusted")) {
            var list = tag.getList("Trusted", 11);
            for (int i = 0; i < list.size(); i++) trusted.add(NbtUtils.loadUUID(list.get(i)));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        if (owner != null) tag.putUUID("Owner", owner);
        tag.putInt("RadiusLevel", getRadiusLevel());
        tag.putInt("HealLevel", getHealLevel());
        var list = new net.minecraft.nbt.ListTag();
        for (UUID id : trusted) list.add(net.minecraft.nbt.NbtUtils.createUUID(id));
        tag.put("Trusted", list);
    }

    public int getRadiusLevel() { return entityData.get(RADIUS_LEVEL); }
    public int getHealLevel() { return entityData.get(HEAL_LEVEL); }
    public double getProtectionRadius() { return 12.0D + getRadiusLevel() * 0.78D; }
    public float getHealAmount() { return 0.5F + getHealLevel() * 0.12F; }
    @Nullable public UUID getOwner() { return owner; }
    public boolean isClaimed() { return owner != null; }

    public boolean isAllowed(Player player) {
        return owner == null || owner.equals(player.getUUID()) || trusted.contains(player.getUUID());
    }

    public boolean isOwner(Player player) { return owner != null && owner.equals(player.getUUID()); }

    public void claim(ServerPlayer player) {
        if (owner != null) return;
        owner = player.getUUID();
        entityData.set(CLAIMED, true);
        AchillesProtection.addPoint(player);
        player.sendSystemMessage(Component.translatable("message.achilles.claimed"));
        level().playSound(null, blockPosition(), net.minecraft.sounds.SoundEvents.BEACON_ACTIVATE,
                net.minecraft.sounds.SoundSource.BLOCKS, 0.8F, 1.1F);
    }

    public boolean trust(UUID id) {
        if (trusted.contains(id) || trusted.size() >= 32) return false;
        trusted.add(id);
        return true;
    }

    public boolean untrust(UUID id) { return trusted.remove(id); }
    public List<UUID> getTrusted() { return List.copyOf(trusted); }

    public boolean upgradeRadius(ServerPlayer player) {
        if (!isOwner(player) || getRadiusLevel() >= 100 || !AchillesProtection.spendPoint(player)) return false;
        entityData.set(RADIUS_LEVEL, getRadiusLevel() + 1);
        return true;
    }

    public boolean upgradeHealing(ServerPlayer player) {
        if (!isOwner(player) || getHealLevel() >= 100 || !AchillesProtection.spendPoint(player)) return false;
        entityData.set(HEAL_LEVEL, getHealLevel() + 1);
        return true;
    }

    public void showInfo(ServerPlayer player) {
        String ownerText = owner == null ? "sin propietario" : (isOwner(player) ? "tu estatua" : "propiedad de otro jugador");
        player.sendSystemMessage(Component.literal("§6✦ Estatua de Aquiles §7— " + ownerText));
        player.sendSystemMessage(Component.literal("§eRadio: §f" + String.format("%.1f", getProtectionRadius()) + " bloques §7(nivel " + getRadiusLevel() + "/100)"));
        player.sendSystemMessage(Component.literal("§aCuración: §f" + String.format("%.2f", getHealAmount()) + " §7(nivel " + getHealLevel() + "/100)"));
        if (isOwner(player)) player.sendSystemMessage(Component.literal("§bPuntos disponibles: §f" + AchillesProtection.getPoints(player)));
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (level().isClientSide) return InteractionResult.SUCCESS;
        if (!(player instanceof ServerPlayer sp)) return InteractionResult.PASS;
        if (hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;

        if (owner == null) {
            claim(sp);
        } else {
            showInfo(sp);
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public net.minecraft.network.protocol.Packet<net.minecraft.network.protocol.game.ClientGamePacketListener> getAddEntityPacket() {
        return net.minecraftforge.network.NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public boolean isPickable() { return true; }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && tickCount % 20 == 0) {
            AchillesProtection.tickStatue(this);
        }
    }

    @Override
    public boolean canBeCollidedWith() { return true; }

    @Override
    public boolean hurt(net.minecraft.world.damagesource.DamageSource source, float amount) { return false; }
}
