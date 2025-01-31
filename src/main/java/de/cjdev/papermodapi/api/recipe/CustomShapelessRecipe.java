package de.cjdev.papermodapi.api.recipe;

import de.cjdev.papermodapi.api.item.CustomItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CustomShapelessRecipe implements CustomCraftingRecipe {
    private @NotNull String group = "";
    private CraftingBookCategory category = CraftingBookCategory.MISC;
    private final ItemStack result;
    private final List<CustomIngredient> ingredients;

    public CustomShapelessRecipe(@NotNull ItemStack result) {
        this.result = result;
        this.ingredients = new ArrayList<>();
    }

    public CustomShapelessRecipe(@NotNull Material result) {
        this(ItemStack.of(result));
    }

    public CustomShapelessRecipe(@NotNull CustomItem result) {
        this(result.getDefaultStack());
    }

    public CustomShapelessRecipe setGroup(@NotNull String group){
        this.group = group;
        return this;
    }

    public CustomShapelessRecipe setCategory(@NotNull CraftingBookCategory category){
        this.category = category;
        return this;
    }

    public CustomShapelessRecipe addIngredient(@NotNull CustomIngredient recipeChoice){
        ingredients.add(recipeChoice);
        return this;
    }

    public CustomShapelessRecipe addIngredient(@NotNull NamespacedKey key){
        ingredients.add(new CustomIngredient(key));
        return this;
    }

    public CustomShapelessRecipe addIngredient(int count, @NotNull NamespacedKey key) {
        while (count-- > 0) {
            this.ingredients.add(new CustomIngredient(key));
        }
        return this;
    }

    public CustomShapelessRecipe addIngredient(@NotNull Material material){
        ingredients.add(new CustomIngredient(material));
        return this;
    }

    public CustomShapelessRecipe addIngredient(int count, @NotNull Material material) {
        while (count-- > 0) {
            this.ingredients.add(new CustomIngredient(material));
        }
        return this;
    }

    public CustomShapelessRecipe addIngredient(@NotNull CustomItem customItem){
        ingredients.add(new CustomIngredient(customItem));
        return this;
    }

    public CustomShapelessRecipe addIngredient(int count, @NotNull CustomItem customItem) {
        while (count-- > 0) {
            this.ingredients.add(new CustomIngredient(customItem));
        }
        return this;
    }

    public @NotNull CraftingBookCategory category() {
        return this.category;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CustomCraftingInput craftingInput) {
        return this.result.clone();
    }

    @Override
    public boolean matches(@NotNull CustomCraftingInput craftingInput) {
        if (craftingInput.ingredientCount() != this.ingredients.size()) return false;
        else if (craftingInput.ingredientCount() == 1)
            return (this.ingredients.getFirst()).test(craftingInput.getItem(0));
        else {
            List<ItemStack> remainingItems = craftingInput.ingredients();

            boolean[] matched = new boolean[craftingInput.ingredientCount()];

            for (CustomIngredient ingredient : this.ingredients) {
                boolean foundMatch = false;

                for (int i = 0; i < remainingItems.size(); i++) {
                    if (!matched[i] && ingredient.test(remainingItems.get(i))) {
                        matched[i] = true;
                        foundMatch = true;
                        break;
                    }
                }

                if (!foundMatch) {
                    return false;
                }
            }

            return true;
        }
    }

    @Override
    public ShapelessRecipe toBukketRecipe(@NotNull NamespacedKey key) {
        ShapelessRecipe bucketRecipe = new ShapelessRecipe(key, this.result);
        bucketRecipe.setGroup(this.group);
        bucketRecipe.setCategory(this.category());
        this.ingredients.forEach(recipeChoice -> bucketRecipe.addIngredient(recipeChoice.toBukkitRecipeChoice()));
        return bucketRecipe;
    }
}
