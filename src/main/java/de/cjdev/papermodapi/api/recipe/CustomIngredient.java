package de.cjdev.papermodapi.api.recipe;

import de.cjdev.papermodapi.api.item.CustomItem;
import de.cjdev.papermodapi.api.item.CustomItems;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Predicate;

public class CustomIngredient implements Predicate<ItemStack>, Cloneable {
    static boolean testOptionalIngredient(Optional<CustomIngredient> ingredient, @NotNull ItemStack stack) {
        return ingredient.map(recipeChoice -> recipeChoice.test(stack)).orElseGet(stack::isEmpty);
    }

    private List<NamespacedKey> keys;

    public CustomIngredient(List<NamespacedKey> keys) {
        this.keys = keys;
    }

    public CustomIngredient(NamespacedKey... keys) {
        this(List.of(keys));
    }

    public CustomIngredient(CustomItem customItem) {
        this(CustomItems.getKeyByItem(customItem));
    }

    public CustomIngredient(Material material) {
        this(material.getKey());
    }

    public boolean test(ItemStack stack) {
        NamespacedKey checkAgainst = CustomItems.getKeyByStack(stack);
        if (checkAgainst == null)
            checkAgainst = stack.getType().getKey();
        return this.keys.contains(checkAgainst);
    }

    public RecipeChoice.ExactChoice toBukkitRecipeChoice() {
        return new RecipeChoice.ExactChoice(this.keys.stream().map(key -> {
            CustomItem customItem = CustomItems.getItemByKey(key);
            if (customItem == null) {
                Material material = Material.getMaterial(key.value().toUpperCase(Locale.ROOT));
                return material == null ? ItemStack.empty() : ItemStack.of(material);
            }
            return customItem.getDefaultStack();
        }).toList());
    }

    @Override
    public @NotNull CustomIngredient clone() {
        return new CustomIngredient(new ArrayList<>(this.keys));
    }
}
