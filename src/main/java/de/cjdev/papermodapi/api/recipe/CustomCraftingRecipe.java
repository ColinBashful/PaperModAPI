package de.cjdev.papermodapi.api.recipe;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface CustomCraftingRecipe extends CustomRecipe<CustomCraftingInput> {
    @NotNull CraftingBookCategory category();

    @Override
    default @Nullable Recipe toBukketRecipe(@NotNull NamespacedKey key) {
        return null;
    }
}
