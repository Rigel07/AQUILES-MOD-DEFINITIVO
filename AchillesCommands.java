package com.achilles.command;

import com.achilles.entity.AchillesStatueEntity;
import com.achilles.world.AchillesProtection;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public final class AchillesCommands {
    private AchillesCommands() {}

    public static void register(CommandDispatcher<CommandSourceStack> d) {
        d.register(Commands.literal("achilles")
                .then(Commands.literal("points").executes(ctx -> {
                    ServerPlayer p = ctx.getSource().getPlayerOrException();
                    p.sendSystemMessage(Component.literal("§6✦ Puntos de Aquiles: §f" + AchillesProtection.getPoints(p)));
                    return 1;
                }))
                .then(Commands.literal("info").executes(ctx -> {
                    ServerPlayer p = ctx.getSource().getPlayerOrException();
                    AchillesStatueEntity s = AchillesProtection.nearestOwned(p);
                    if (s == null) { p.sendSystemMessage(Component.literal("§cNo tienes una estatua de Aquiles cerca.")); return 0; }
                    s.showInfo(p);
                    return 1;
                }))
                .then(Commands.literal("upgrade")
                        .then(Commands.literal("radius").executes(ctx -> upgrade(ctx.getSource().getPlayerOrException(), true)))
                        .then(Commands.literal("healing").executes(ctx -> upgrade(ctx.getSource().getPlayerOrException(), false))))
                .then(Commands.literal("trust")
                        .then(Commands.argument("player", StringArgumentType.word()).executes(ctx -> trust(ctx.getSource().getPlayerOrException(), StringArgumentType.getString(ctx, "player"), true))))
                .then(Commands.literal("untrust")
                        .then(Commands.argument("player", StringArgumentType.word()).executes(ctx -> trust(ctx.getSource().getPlayerOrException(), StringArgumentType.getString(ctx, "player"), false))))
                .then(Commands.literal("help").executes(ctx -> {
                    ctx.getSource().sendSuccess(() -> Component.literal("§6§lACHILLES §7— §f/achilles points | info | upgrade radius | upgrade healing | trust <jugador> | untrust <jugador>"), false);
                    return 1;
                })));
    }

    private static int upgrade(ServerPlayer p, boolean radius) {
        AchillesStatueEntity s = AchillesProtection.nearestOwned(p);
        if (s == null) { p.sendSystemMessage(Component.literal("§cAcércate a una estatua que sea tuya.")); return 0; }
        boolean ok = radius ? s.upgradeRadius(p) : s.upgradeHealing(p);
        if (!ok) {
            p.sendSystemMessage(Component.literal("§cNo se pudo mejorar. Necesitas puntos y el nivel no puede superar 100."));
            return 0;
        }
        p.sendSystemMessage(Component.literal(radius
                ? "§6✦ Radio aumentado a nivel §f" + s.getRadiusLevel() + "§6."
                : "§a✦ Curación aumentada a nivel §f" + s.getHealLevel() + "§a."));
        return 1;
    }

    private static int trust(ServerPlayer owner, String name, boolean add) {
        AchillesStatueEntity s = AchillesProtection.nearestOwned(owner);
        if (s == null) { owner.sendSystemMessage(Component.literal("§cAcércate a tu estatua.")); return 0; }
        ServerPlayer target = owner.server.getPlayerList().getPlayerByName(name);
        if (target == null) { owner.sendSystemMessage(Component.literal("§cEse jugador debe estar conectado.")); return 0; }
        boolean ok = add ? s.trust(target.getUUID()) : s.untrust(target.getUUID());
        owner.sendSystemMessage(Component.literal(ok
                ? (add ? "§a✓ Permiso concedido a " : "§e✓ Permiso retirado a ") + name
                : "§cNo se pudo cambiar ese permiso."));
        return ok ? 1 : 0;
    }
}
