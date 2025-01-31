package de.cjdev.papermodapi.listener;

import de.cjdev.morepaperevents.api.event.FurnacePrepareSmeltEvent;
import de.cjdev.papermodapi.PaperModAPI;
import de.cjdev.papermodapi.api.component.CustomDataComponents;
import de.cjdev.papermodapi.api.item.CustomItem;
import de.cjdev.papermodapi.api.item.CustomItems;
import de.cjdev.papermodapi.api.recipe.*;
import org.bukkit.block.Furnace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.FurnaceStartSmeltEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class FurnaceEventListener implements Listener {
    /// Calls when the Furnace wants to burn an item for fuel
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFurnaceBurn(FurnaceBurnEvent event) {
        Integer fuelTicks = CustomDataComponents.FUEL_COMPONENT.get(event.getFuel());
        if (fuelTicks == null)
            return;
        event.setBurnTime(fuelTicks);
        CustomItem customFuelInput = CustomItems.getItemByStack(event.getFuel());
        Consumer<ItemStack> recipeRemainder;
        if (customFuelInput != null && (recipeRemainder = customFuelInput.getRecipeRemainder()) != null) {
            event.setConsumeFuel(false);
            recipeRemainder.accept(event.getFuel());
        }
    }

    @EventHandler
    public void onFurnacePrepareSmelt(FurnacePrepareSmeltEvent event) {
        CustomCookingRecipeInput cookingInput = new CustomCookingRecipeInput(event.getFuel(), event.getSource());

        for (CustomRecipe<?> customRecipe : PaperModAPI.CustomRecipes) {
            if (!(customRecipe instanceof CustomFurnaceRecipe furnaceRecipe))
                continue;
            if (furnaceRecipe.matches(cookingInput)) {
                event.setCanSmelt(true);
                return;
            }
        }

        if (cookingInput.isCustom())
            event.setCanSmelt(false);
    }

    @EventHandler
    public void onFurnaceStartSmelt(FurnaceStartSmeltEvent event) {
        CustomCookingRecipeInput cookingInput = new CustomCookingRecipeInput(null, event.getSource());

        for (CustomRecipe<?> customRecipe : PaperModAPI.CustomRecipes) {
            if (!(customRecipe instanceof CustomFurnaceRecipe furnaceRecipe))
                continue;
            if (furnaceRecipe.matches(cookingInput)) {
                event.setTotalCookTime(furnaceRecipe.getCookingTime());
                return;
            }
        }
    }

    @EventHandler
    public void onFurnaceSmelt(FurnaceSmeltEvent event) {
        if(!(event.getBlock().getState() instanceof Furnace furnace)) return;
        CustomCookingRecipeInput cookingInput = new CustomCookingRecipeInput(furnace.getInventory().getFuel(), event.getSource());

        for (CustomRecipe<?> customRecipe : PaperModAPI.CustomRecipes) {
            if (!(customRecipe instanceof CustomFurnaceRecipe furnaceRecipe))
                continue;
            if (furnaceRecipe.matches(cookingInput)) {
                event.setResult(furnaceRecipe.assemble(cookingInput));
                return;
            }
        }

        if (cookingInput.isCustom())
            event.setResult(ItemStack.empty());
    }
}
