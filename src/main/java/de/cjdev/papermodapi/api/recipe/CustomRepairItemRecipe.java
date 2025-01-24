package de.cjdev.papermodapi.api.recipe;

import com.mojang.datafixers.util.Pair;
import de.cjdev.papermodapi.api.item.CustomItem;
import de.cjdev.papermodapi.api.item.CustomItems;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class CustomRepairItemRecipe implements CustomCraftingRecipe {
    public CustomRepairItemRecipe() {
    }

    public @NotNull CraftingBookCategory category() {
        return CraftingBookCategory.MISC;
    }

    @Nullable
    private static Pair<ItemStack, ItemStack> getItemsToCombine(CustomCraftingInput input) {
        if (input.ingredientCount() != 2) {
            return null;
        } else {
            ItemStack itemStack = null;

            for (int i = 0; i < input.size(); ++i) {
                ItemStack item = input.getItem(i);
                if (!item.isEmpty()) {
                    if (itemStack != null) {
                        return canCombine(itemStack, item) ? Pair.of(itemStack, item) : null;
                    }

                    itemStack = item;
                }
            }

            return null;
        }
    }

    private static boolean canCombine(ItemStack stack1, ItemStack stack2) {
        CustomItem item1 = CustomItems.getItemByStack(stack1);
        CustomItem item2 = CustomItems.getItemByStack(stack2);
        if (item1 == null)
            return false;
        return item1 == item2 && stack1.getAmount() == 1 && stack2.getAmount() == 1 && stack1.hasData(DataComponentTypes.MAX_DAMAGE) && stack2.hasData(DataComponentTypes.MAX_DAMAGE) && stack1.hasData(DataComponentTypes.DAMAGE) && stack2.hasData(DataComponentTypes.DAMAGE);
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CustomCraftingInput craftingInput) {
        for (ItemStack stack : craftingInput.items()) {
            if (stack.isEmpty())
                continue;
            CustomItem currentItem = CustomItems.getItemByStack(stack);
            if (currentItem == null)
                continue;
            return currentItem.getDefaultStack();
        }
        return ItemStack.empty();
    }

    @Override
    public boolean matches(@NotNull CustomCraftingInput input) {
        return getItemsToCombine(input) != null;
    }

    @Override
    public Recipe toBukketRecipe(@NotNull NamespacedKey key) {
        return null;
    }
}
