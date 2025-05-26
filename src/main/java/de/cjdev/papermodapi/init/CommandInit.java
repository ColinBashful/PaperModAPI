package de.cjdev.papermodapi.init;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.cjdev.papermodapi.PaperModAPI;
import de.cjdev.papermodapi.api.item.CustomItem;
import de.cjdev.papermodapi.api.register.Registries;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.ShadowBrigNode;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import org.bukkit.*;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class CommandInit {
    public static void load(LifecycleEventManager<Plugin> lifecycleManager, JavaPlugin plugin) {
        lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            LiteralArgumentBuilder<CommandSourceStack> apiCommand = Commands.literal("modapi");

            apiCommand.then(Commands.literal("items").requires(ctx -> {
                if (ctx.getSender().isOp())
                    return true;
                if (ctx.getSender() instanceof Player player)
                    return player.getGameMode() == GameMode.CREATIVE;
                return false;
            }).executes(ctx -> {
                if (!(ctx.getSource().getExecutor() instanceof Player player))
                    return 0;
                player.openInventory(PaperModAPI.CUSTOM_CREATIVE_INVENTORY.apply(player.isOp()).getInventory());
                return 1;
            }).build());

            Command<net.minecraft.commands.CommandSourceStack> giveCommand = context -> {
                int itemCount;
                try {
                    itemCount = IntegerArgumentType.getInteger(context, "count");
                } catch (Exception ignored) {
                    itemCount = 1;
                }
                Collection<ServerPlayer> playersResolver = EntityArgument.getPlayers(context, "targets");
                ResourceLocation resourceLocation = ResourceLocationArgument.getId(context, "modapi_item"); // It prob differentiates, I hope
                NamespacedKey id;
                try {
                    id = CraftNamespacedKey.fromMinecraft(resourceLocation);
                } catch(IllegalArgumentException ignored) {
                    context.getSource().getSender().sendMessage(Component.translatable("argument.id.invalid").color(TextColor.color(16733525)));
                    return 0;
                }
                CustomItem item = Registries.ITEM.getValue(id);
                if (item == null) {
                    context.getSource().getSender().sendMessage(Component.translatable("argument.item.id.invalid", Component.text(resourceLocation.toString())).color(TextColor.color(16733525)));
                    return 0;
                }
                if (playersResolver.isEmpty())
                    return 1;
                List<ItemStack> itemStacks = Collections.nCopies(itemCount, item.getDefaultStack());
                for (ServerPlayer serverPlayer : playersResolver) {
                    serverPlayer.getBukkitEntity().give(itemStacks);
                    serverPlayer.level().playSound(null, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((serverPlayer.getRandom().nextFloat() - serverPlayer.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
                }
                return 1;
            };

            ((ShadowBrigNode)event.registrar().getDispatcher().getRoot().getChild("give")).getHandle().getChild("targets").addChild(net.minecraft.commands.Commands.argument("modapi_item", ResourceLocationArgument.id()).suggests((context, builder) -> {
                for(NamespacedKey namespacedKey : Registries.ITEM.keySet()) {
                    String plainId = namespacedKey.asString().toLowerCase(Locale.ROOT);
                    if (plainId.startsWith(builder.getRemainingLowerCase())) {
                        builder.suggest(plainId);
                    }
                }
                return builder.buildFuture();
            }).executes(giveCommand).then(net.minecraft.commands.Commands.argument("count", IntegerArgumentType.integer(1)).executes(giveCommand).build()).build());

            event.registrar().register(apiCommand.build());
        });
    }
}
