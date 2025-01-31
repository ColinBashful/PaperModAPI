package de.cjdev.papermodapi.listener;

import de.cjdev.papermodapi.PaperModAPI;
import de.cjdev.papermodapi.api.item.CustomItem;
import de.cjdev.papermodapi.api.item.CustomItems;
import de.cjdev.papermodapi.api.recipe.*;
import net.kyori.adventure.text.Component;
import net.minecraft.world.inventory.AnvilMenu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.CrafterCraftEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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

        // TODO: Add Recipe Remainders to this
        ItemStack[] copyMatrix = event.getInventory().getMatrix().clone();
        for (ItemStack matrix : copyMatrix) {
            if(matrix == null)
                continue;
        }
        event.getInventory().setMatrix(copyMatrix);
        Bukkit.getServer().broadcast(Component.text(String.join(", ", Arrays.stream(event.getInventory().getMatrix()).filter(Objects::nonNull).map(stack -> String.valueOf(stack.getAmount())).toList())));
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

        for (CustomRecipe<?> customRecipe : PaperModAPI.CustomRecipes) {
            if (!(customRecipe instanceof CustomCraftingRecipe craftingRecipe))
                continue;
            if (craftingRecipe.matches(craftingInput)) {
                event.getInventory().setResult(craftingRecipe.assemble(craftingInput));
                return;
            }
        }

        if (craftingInput.anyCustom())
            event.getInventory().setResult(ItemStack.empty());
    }

//    @EventHandler(priority = EventPriority.HIGHEST)
//    public void onPrepareAnvil(PrepareAnvilEvent event) {
//        AnvilInventory inventory = event.getInventory();
//        CustomAnvilRecipeInput anvilInput = new CustomAnvilRecipeInput(inventory.getFirstItem(), inventory.getSecondItem());
//
//        inventory.setResult(ItemStack.of(Material.IRON_INGOT));
////        for (CustomRecipe<?> customRecipe : PaperModAPI.CustomRecipes) {
////            if (!(customRecipe instanceof CustomAnvilRecipe anvilRecipe))
////                continue;
////            if (anvilRecipe.matches(anvilInput)) {
////                //inventory.getViewers().forEach(humanEntity -> humanEntity.sendMessage("TEST"));
////                //anvilRecipe.assemble(anvilInput)
////                inventory.setResult(ItemStack.of(Material.STRING));
////                return;
////            }
////        }
//    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPrepareSmithing(PrepareSmithingEvent event) {
        SmithingInventory inventory = event.getInventory();
        CustomSmithingRecipeInput smithingInput = new CustomSmithingRecipeInput(inventory.getInputTemplate(), inventory.getInputEquipment(), inventory.getInputMineral());

        for (CustomRecipe<?> customRecipe : PaperModAPI.CustomRecipes) {
            if (!(customRecipe instanceof CustomSmithingRecipe smithingRecipe))
                continue;
            if (smithingRecipe.matches(smithingInput)) {
                event.getInventory().setResult(smithingRecipe.assemble(smithingInput));
                return;
            }
        }

        if (smithingInput.anyCustom())
            event.getInventory().setResult(ItemStack.empty());
    }
}
