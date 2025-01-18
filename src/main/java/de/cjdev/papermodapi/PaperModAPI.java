package de.cjdev.papermodapi;

import com.mojang.brigadier.tree.LiteralCommandNode;
import de.cjdev.papermodapi.inventory.CustomCreativeInventory;
import de.cjdev.papermodapi.listener.InventoryClickEventListener;
import de.cjdev.papermodapi.listener.PlayerInteractEventListener;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class PaperModAPI extends JavaPlugin {
    private static PaperModAPI plugin;
    public static Logger LOGGER;

    @Override
    public void onEnable() {
        plugin = this;
        LOGGER = getLogger();

        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new PlayerInteractEventListener(), this);
        pluginManager.registerEvents(new InventoryClickEventListener(), this);

        final LifecycleEventManager<Plugin> lifecycleManager = getLifecycleManager();
        lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            LiteralCommandNode apiCommand = Commands.literal("papermodapi")
                    .then(Commands.literal("items").requires(ctx -> {
                        if(!(ctx.getSender() instanceof Player player))
                            return false;
                        return player.isOp() || player.getGameMode() == GameMode.CREATIVE;
                    }).executes(ctx -> {
                        if(!(ctx.getSource().getExecutor() instanceof Player player))
                            return 0;
                        player.openInventory(new CustomCreativeInventory(this).getInventory());
                        return 1;
                    }).build()).build();

            event.registrar().register(apiCommand);
        });
    }

    public static PaperModAPI getPlugin(){
        return plugin;
    }
}
