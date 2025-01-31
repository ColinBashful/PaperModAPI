package de.cjdev.papermodapi.api.recipe;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public interface CustomRecipe<T extends CustomRecipeInput> {
    @NotNull ItemStack assemble(@NotNull T craftingInput);
    boolean matches(@NotNull T craftingInput);
    //@Nullable CustomRecipe<T> fromBukkitRecipe();
    @Nullable Recipe toBukketRecipe(NamespacedKey key);
}
