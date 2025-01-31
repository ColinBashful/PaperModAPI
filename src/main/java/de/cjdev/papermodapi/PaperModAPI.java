package de.cjdev.papermodapi;

import de.cjdev.papermodapi.api.recipe.CustomRecipe;
import de.cjdev.papermodapi.api.recipe.CustomRepairItemRecipe;
import de.cjdev.papermodapi.init.CommandInit;
import de.cjdev.papermodapi.inventory.CustomCreativeInventory;
import de.cjdev.papermodapi.listener.*;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.logging.Logger;

public final class PaperModAPI extends JavaPlugin {
    private static PaperModAPI plugin;
    public static Logger LOGGER;

    ///
    /// Don't ask me why the entire class gets deleted if unused for too long or whatever the reason is
    ///
    public static final Function<Boolean, CustomCreativeInventory> CUSTOM_CREATIVE_INVENTORY = hasOp -> new CustomCreativeInventory(getPlugin(), hasOp, null);

    public static final Set<CustomRecipe<?>> CustomRecipes = new HashSet<>();

    static class ConsoleColor {
        static String hexColor = "\u001B[38;2;%d;%d;%dm";
        static String reset = "\u001B[0m";
        static String blue = String.format(hexColor, 85, 85, 255);
        static String gray = String.format(hexColor, 170, 170, 170);
        static String darkGray = String.format(hexColor, 85, 85, 85);
        static String darkGreen = String.format(hexColor, 0, 170, 0);
        static String gold = String.format(hexColor, 255, 170, 0);
    }
    // the difference of what?
    // also, it might be that you need to restart the server to edit the registry, idrk tho
    // Then this "might" work
    static { // and what would happen if you make the registry thing in static {} frfr
        // th, ExceptionInInitializer
        // ah yes, can't create Intrusive things.... :< :|
        // so, option 1: make a custom fork and somehow do the logic, or
        // option 2: use a mod that translates stuff to vanilla (exists actually)
        //Items.registerItem("test_item");
    }

    @Override
    public void onLoad() {
        // i dont think it wants a new Block, it just wants a class that extends block actually, nvm
        // This'll never work in Standalone PaperMC tho
        // So, if a Fork of PaperMC allowed "Mods" that initialize before Registries are finished
        // But that'd be Ignite .-. 'cause it allows loading before anything
        //Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath("test", "aaaaa"), new Block(BlockBehaviour.Properties.of().strength(1f)));
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

        // Turning All Recipes to Custom Ones
        new BukkitRunnable() {
            @Override
            public void run() {
                for (RecipeHolder<?> recipe : MinecraftServer.getServer().getRecipeManager().getRecipes()) {
                    Recipe bukkitRecipe = recipe.toBukkitRecipe();
                    if(bukkitRecipe instanceof CraftingRecipe craftingRecipe){
                        //addRecipe(craftingRecipe.getKey(), );
                    }
                }
            }
        }.runTask(this);

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

    public static @Nullable Recipe addRecipe(NamespacedKey key, CustomRecipe recipe) {
        if (!CustomRecipes.add(recipe)) return null;
        Recipe bukkitRecipe = recipe.toBukketRecipe(key);
        if (bukkitRecipe == null) return null;
        Bukkit.addRecipe(bukkitRecipe);
        return bukkitRecipe;
    }

    public static NamespacedKey key(String id){
        return NamespacedKey.fromString(id, getPlugin());
    }

    public static ResourceLocation getResourceLocation(String id){
        return ResourceLocation.fromNamespaceAndPath("papermodapi", id);
    }
}
