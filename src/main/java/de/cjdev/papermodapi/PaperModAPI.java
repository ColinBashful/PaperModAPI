package de.cjdev.papermodapi;

import de.cjdev.papermodapi.api.item.CustomItem;
import de.cjdev.papermodapi.api.item.CustomItems;
import de.cjdev.papermodapi.api.recipe.CustomRecipe;
import de.cjdev.papermodapi.api.recipe.CustomRepairItemRecipe;
import de.cjdev.papermodapi.init.CommandInit;
import de.cjdev.papermodapi.inventory.CustomCreativeInventory;
import de.cjdev.papermodapi.listener.*;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.logging.Logger;

public final class PaperModAPI extends JavaPlugin {
    private static PaperModAPI plugin;
    public static Logger LOGGER;

    public static final Map<CustomItem, Integer> FuelItems = new HashMap<>();
    public static final Set<CustomRecipe> CustomRecipes = new HashSet<>();

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
        pluginManager.registerEvents(new CraftEventListener(), this);
        pluginManager.registerEvents(new ItemDespawnEventListener(), this);
        pluginManager.registerEvents(new FurnaceEventListener(), this);
        pluginManager.registerEvents(new CookEventListener(), this);

        // Registering Special Recipes
        addRecipe(key("repair"), new CustomRepairItemRecipe());

        // Registering Commands
        CommandInit.load(getLifecycleManager(), this);

        // Loaded Message
        LOGGER.info(ConsoleColor.blue + "\n ___   _    ___  ___  ___         _   _      _    ___      " + ConsoleColor.darkGreen + "PaperModAPI " + ConsoleColor.gray + getPluginMeta().getVersion() + ConsoleColor.blue + "\n" +
                                               "|__/  /_\\  |__/ |__  |__/   |\\/| / \\ | \\    /_\\  |__/ |    " + ConsoleColor.darkGray + "Paper " + Bukkit.getVersion() + ConsoleColor.blue +
                                             "\n|    /   \\ |    |___ |  \\   |  | \\_/ |_/   /   \\ |    |\n" + ConsoleColor.reset);
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().iterator().forEachRemaining(player -> {
            if (player.getOpenInventory().getTopInventory().getHolder() instanceof CustomCreativeInventory customCreativeInventory)
                customCreativeInventory.getInventory().close();
        });
    }

    public static PaperModAPI getPlugin(){
        return plugin;
    }

    public static void addRecipe(NamespacedKey key, CustomRecipe recipe) {
        if (!CustomRecipes.add(recipe)) return;
        Recipe bukkitRecipe = recipe.toBukketRecipe(key);
        if (bukkitRecipe == null) return;
        Bukkit.getServer().addRecipe(bukkitRecipe);
    }

    public static void registerFuel(CustomItem fuelItem, int fuelTicks) {
        if (FuelItems.containsKey(fuelItem)) {
            FuelItems.put(fuelItem, fuelTicks);
            LOGGER.warning(String.format("Fuel Item %s (%st) registered" + ConsoleColor.reset, CustomItems.getKeyByItem(fuelItem), fuelTicks));
        } else {
            FuelItems.put(fuelItem, fuelTicks);
            LOGGER.info(String.format(ConsoleColor.gray + "Fuel Item " + ConsoleColor.blue + "%s" + ConsoleColor.gold + " (%st)" + ConsoleColor.darkGray + " registered" + ConsoleColor.reset, CustomItems.getKeyByItem(fuelItem), fuelTicks));
        }
    }

    public NamespacedKey key(String id){
        return NamespacedKey.fromString(id, this);
    }

    public static ResourceLocation getResourceLocation(String id){
        return ResourceLocation.fromNamespaceAndPath("papermodapi", id);
    }
}
