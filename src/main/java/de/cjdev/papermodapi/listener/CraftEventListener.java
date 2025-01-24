package de.cjdev.papermodapi.listener;

import com.destroystokyo.paper.event.inventory.PrepareResultEvent;
import de.cjdev.papermodapi.PaperModAPI;
import de.cjdev.papermodapi.api.item.CustomItem;
import de.cjdev.papermodapi.api.item.CustomItems;
import de.cjdev.papermodapi.api.recipe.*;
import io.papermc.paper.event.player.PlayerStonecutterRecipeSelectEvent;
import net.kyori.adventure.text.Component;
import net.minecraft.world.level.block.CrafterBlock;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.StonecutterBlock;
import net.minecraft.world.level.block.entity.CrafterBlockEntity;
import org.bukkit.Bukkit;
import org.bukkit.block.Crafter;
import org.bukkit.craftbukkit.inventory.util.CraftTileInventoryConverter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.CrafterCraftEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.inventory.*;

import java.util.Arrays;
import java.util.List;

public class CraftEventListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCraft(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player player))
            return;
        event.getWhoClicked().sendMessage(Component.text("You (Player) CRAFTED!"));
        ItemStack result = event.getInventory().getResult();
        CustomItem item = CustomItems.getItemByStack(event.getInventory().getResult());
        if (item == null)
            return;
        item.onCraftByPlayer(result, player.getWorld(), player);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCrafterCraft(CrafterCraftEvent event) {
        ItemStack result = event.getResult();
        CustomItem item = CustomItems.getItemByStack(event.getResult());
        if (item == null)
            return;
        item.onCraft(result, event.getBlock().getWorld());
        event.setResult(result);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
        List<ItemStack> input = Arrays.stream(event.getInventory().getMatrix()).map(stack -> stack == null ? ItemStack.empty() : stack).toList();

        int width;
        int height = width = (int) Math.sqrt(input.size());

        CustomCraftingInput craftingInput = new CustomCraftingInput(width, height, input);

        for (CustomRecipe customRecipe : PaperModAPI.CustomRecipes) {
            if (!(customRecipe instanceof CustomCraftingRecipe))
                continue;
            if (customRecipe.matches(craftingInput)) {
                event.getInventory().setResult(customRecipe.assemble(craftingInput));
                return;
            }
        }

        if (craftingInput.anyCustom())
            event.getInventory().setResult(ItemStack.empty());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPrepareAnvil(PrepareAnvilEvent event) {

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPrepareSmithing(PrepareSmithingEvent event) {
        SmithingInventory inventory = event.getInventory();
        CustomSmithingRecipeInput smithingInput = new CustomSmithingRecipeInput(inventory.getInputTemplate(), inventory.getInputEquipment(), inventory.getInputMineral());

        for (CustomRecipe customRecipe : PaperModAPI.CustomRecipes) {
            if (!(customRecipe instanceof CustomSmithingRecipe))
                continue;
            if (customRecipe.matches(smithingInput)) {
                event.getInventory().setResult(customRecipe.assemble(smithingInput));
                return;
            }
        }

        if (smithingInput.anyCustom())
            event.getInventory().setResult(ItemStack.empty());
    }

    @EventHandler
    public void onPlayerStonecutterRecipeSelect(PlayerStonecutterRecipeSelectEvent event) {

    }
}
