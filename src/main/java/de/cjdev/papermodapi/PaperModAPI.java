package de.cjdev.papermodapi;

import de.cjdev.papermodapi.init.CommandInit;
import de.cjdev.papermodapi.inventory.CustomCreativeInventory;
import de.cjdev.papermodapi.listener.*;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Function;
import java.util.logging.Logger;

public final class PaperModAPI extends JavaPlugin {
    private static PaperModAPI plugin;
    public static Logger LOGGER;

    ///
    /// Don't ask me why the entire class gets deleted if unused for too long or whatever the reason is
    ///
    public static final Function<Boolean, CustomCreativeInventory> CUSTOM_CREATIVE_INVENTORY = hasOp -> new CustomCreativeInventory(getPlugin(), hasOp, null);

    static class ConsoleColor {
        static String hexColor = "\u001B[38;2;%d;%d;%dm";
        static String reset = "\u001B[0m";
        static String blue = String.format(hexColor, 85, 85, 255);
        static String gray = String.format(hexColor, 170, 170, 170);
        static String darkGray = String.format(hexColor, 85, 85, 85);
        static String darkGreen = String.format(hexColor, 0, 170, 0);
        static String gold = String.format(hexColor, 255, 170, 0);
    }

    @Override
    public void onEnable() {
        plugin = this;
        LOGGER = getLogger();

        // Registering Event Listeners
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new PlayerInteractEventListener(), this);
        pluginManager.registerEvents(new InventoryClickEventListener(), this);
        pluginManager.registerEvents(new ItemDespawnEventListener(), this);

        // Registering Commands
        CommandInit.load(getLifecycleManager(), this);

        // Inventory Tick
//        new BukkitRunnable() {
//            @Override
//            public void run() {
//                Bukkit.getWorlds().iterator().forEachRemaining(world -> {
//                    world.getEntities().stream().filter(entity -> entity instanceof LivingEntity).forEach(livingEntity -> {
//                        //((LivingEntity)livingEntity)
//                        Inventory inventory = ((LivingEntity)livingEntity).getInventory();
//                        for (int slot = 0; slot < inventory.getSize(); ++slot){
//                            ItemStack stack = inventory.getItem(slot);
//                            CustomItem customItem = CustomItems.getItemByStack(stack);
//                            if(customItem == null)
//                                return;
//                            customItem.inventoryTick(stack, inventoryEntity.getWorld(), inventoryEntity, slot, false);
//                        }
//                    });
//                });
//            }
//        }.runTaskTimerAsynchronously(this, 1, 1);

        // Loaded Message
        LOGGER.info(ConsoleColor.blue + "\n ___   _    ___  ___  ___         _   _      _    ___      " + ConsoleColor.darkGreen + "PaperModAPI " + ConsoleColor.gray + getPluginMeta().getVersion() + ConsoleColor.blue + "\n" +
                                               "|__/  /_\\  |__/ |__  |__/   |\\/| / \\ | \\    /_\\  |__/ |    " + ConsoleColor.darkGray + "Paper " + Bukkit.getVersion() + ConsoleColor.blue +
                                             "\n|    /   \\ |    |___ |  \\   |  | \\_/ |_/   /   \\ |    |\n" + ConsoleColor.reset);
    }

    @Override
    public void onDisable() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.getOpenInventory().getTopInventory().getHolder() instanceof CustomCreativeInventory customCreativeInventory)
                customCreativeInventory.getInventory().close();
        }
    }

    public static PaperModAPI getPlugin(){
        return plugin;
    }

    public static NamespacedKey key(String id){
        return new NamespacedKey("modapi", id);
    }
}
