package de.cjdev.papermodapi.api.recipe;

import com.google.common.base.Preconditions;
import de.cjdev.papermodapi.api.item.CustomItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.recipe.CookingBookCategory;
import org.jetbrains.annotations.NotNull;

public abstract class CustomCookingRecipe implements CustomRecipe<CustomCookingRecipeInput> {
    private final ItemStack output;
    private CustomIngredient ingredient;
    private float experience;
    private int cookingTime;
    private String group;
    private CookingBookCategory category;

    public CustomCookingRecipe(@NotNull ItemStack result, @NotNull NamespacedKey source, float experience, int cookingTime) {
        this(result, new CustomIngredient(source), experience, cookingTime);
    }

    public CustomCookingRecipe(@NotNull ItemStack result, @NotNull Material source, float experience, int cookingTime) {
        this(result, source.getKey(), experience, cookingTime);
    }

    public CustomCookingRecipe(@NotNull ItemStack result, @NotNull CustomItem source, float experience, int cookingTime) {
        this(result, new CustomIngredient(source), experience, cookingTime);
    }

    public CustomCookingRecipe(@NotNull ItemStack result, @NotNull CustomIngredient input, float experience, int cookingTime) {
        this.group = "";
        this.category = CookingBookCategory.MISC;
        Preconditions.checkArgument(!result.isEmpty(), "Recipe cannot have an empty result.");
        this.output = new ItemStack(result);
        this.ingredient = input.clone();
        this.experience = experience;
        this.cookingTime = cookingTime;
    }

    public CustomIngredient getIngredient() {
        return this.ingredient;
    }

    public @NotNull CustomCookingRecipe setInput(@NotNull CustomIngredient input) {
        this.ingredient = input;
        return this;
    }

    @Override
    public boolean matches(@NotNull CustomCookingRecipeInput craftingInput) {
        return this.ingredient.test(craftingInput.source());
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CustomCookingRecipeInput craftingInput) {
        return this.getOutput();
    }

    public ItemStack getOutput() {
        return output.clone();
    }

    public void setExperience(float experience) {
        this.experience = experience;
    }

    public float getExperience() {
        return this.experience;
    }

    public void setCookingTime(int cookingTime) {
        Preconditions.checkArgument(cookingTime >= 0, "cookingTime must be >= 0");
        this.cookingTime = cookingTime;
    }

    public int getCookingTime() {
        return this.cookingTime;
    }

    public @NotNull String getGroup() {
        return this.group;
    }

    public void setGroup(@NotNull String group) {
        Preconditions.checkArgument(group != null, "group cannot be null");
        this.group = group;
    }

    public @NotNull CookingBookCategory getCategory() {
        return this.category;
    }

    public void setCategory(@NotNull CookingBookCategory category) {
        Preconditions.checkArgument(category != null, "category cannot be null");
        this.category = category;
    }
}
