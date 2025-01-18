package de.cjdev.papermodapi.init;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.cjdev.papermodapi.PaperModAPI;
import de.cjdev.papermodapi.inventory.CustomCreativeInventory;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

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
                player.openInventory(new CustomCreativeInventory(plugin, player.isOp(), null).getInventory());
                return 1;
            }).build());

            apiCommand.then(Commands.literal("zip-pack").requires(ctx -> {
                return ctx.getSender().isOp();
            }).executes(ctx -> {
                if (PaperModAPI.getPlugin().zipPack())
                    PaperModAPI.refreshResourcePack();
                return 1;
            }).build());

            event.registrar().register(apiCommand.build());
        });
    }
}
