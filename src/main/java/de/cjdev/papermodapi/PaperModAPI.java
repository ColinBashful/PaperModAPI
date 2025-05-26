package de.cjdev.papermodapi;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import de.cjdev.papermodapi.api.block.UseItemOnCallback;
import de.cjdev.papermodapi.api.block.UseWithoutItemCallback;
import de.cjdev.papermodapi.api.item.TooltipCallback;
import de.cjdev.papermodapi.init.CommandInit;
import de.cjdev.papermodapi.inventory.CustomCreativeInventory;
import de.cjdev.papermodapi.listener.*;
import de.cjdev.papermodapi.packet.PaperModAPIPacketListener;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Logger;

public final class PaperModAPI extends JavaPlugin {
    private static PaperModAPI plugin;
    public static Logger LOGGER;

    public static final List<TooltipCallback> TOOLTIP_CALLBACKS = new ArrayList<>();
    public static final List<UseWithoutItemCallback> USE_BLOCK_WITHOUT_ITEM_CALLBACKS = new ArrayList<>();
    public static final List<UseItemOnCallback> USE_ITEM_ON_BLOCK_CALLBACKS = new ArrayList<>();

    ///
    /// Don't ask me why the entire class gets deleted if unused for too long or whatever the reason is
    /// Answer: Just don't reload :thumbs_up: it's funky otherwise
    ///
    public static final Function<Boolean, CustomCreativeInventory> CUSTOM_CREATIVE_INVENTORY = hasOp -> new CustomCreativeInventory(getPlugin(), hasOp, null);

    public static class ConsoleColor {
        static String hexColor = "\u001B[38;2;%d;%d;%dm";
        public static String reset = "\u001B[0m";
        public static String blue = hexColor.formatted(85, 85, 255);
        public static String gray = hexColor.formatted(170, 170, 170);
        public static String darkGray = hexColor.formatted(85, 85, 85);
        public static String darkGreen = hexColor.formatted(0, 170, 0);
        //public static String gold = hexColor.formatted(255, 170, 0);
    }

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));

        PacketEvents.getAPI().getEventManager().registerListener(
                new PaperModAPIPacketListener(), PacketListenerPriority.HIGHEST);
    }

    @Override
    public void onEnable() {
        plugin = this;
        LOGGER = getLogger();

        // Registering Event Listeners
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new PlayerInteractEventListener(), this);
        pluginManager.registerEvents(new InventoryEventListener(), this);
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

    public static void registerTooltipCallback(TooltipCallback callback) {
        TOOLTIP_CALLBACKS.add(callback);
    }
}
