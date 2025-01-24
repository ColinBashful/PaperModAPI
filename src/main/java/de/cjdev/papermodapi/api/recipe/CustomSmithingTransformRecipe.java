package de.cjdev.papermodapi.api.recipe;

import de.cjdev.papermodapi.api.item.CustomItem;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CustomSmithingTransformRecipe implements CustomSmithingRecipe {
    private final CustomRecipeChoice template;
    private final CustomRecipeChoice base;
    private final CustomRecipeChoice addition;
    private final ItemStack result;

    final boolean copyDataComponents;

    public CustomSmithingTransformRecipe(CustomRecipeChoice template, CustomRecipeChoice base, CustomRecipeChoice addition, @NotNull ItemStack result, boolean copyDataComponents) {
        this.copyDataComponents = copyDataComponents;
        this.template = template == null ? CustomRecipeChoice.empty() : template;
        this.base = base == null ? CustomRecipeChoice.empty() : base;
        this.addition = addition == null ? CustomRecipeChoice.empty() : addition;
        this.result = result;
    }

    public CustomSmithingTransformRecipe(CustomRecipeChoice template, CustomRecipeChoice base, CustomRecipeChoice addition, @NotNull CustomItem result, boolean copyDataComponents) {
        this(template, base, addition, result.getDefaultStack(), copyDataComponents);
    }

    public CustomSmithingTransformRecipe(CustomRecipeChoice template, CustomRecipeChoice base, CustomRecipeChoice addition, @NotNull ItemStack result){
        this(template, base, addition, result, true);
    }

    public CustomSmithingTransformRecipe(CustomRecipeChoice template, CustomRecipeChoice base, CustomRecipeChoice addition, @NotNull CustomItem result){
        this(template, base, addition, result, true);
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CustomSmithingRecipeInput smithingInput) {
        net.minecraft.world.item.ItemStack nmsResult = CraftItemStack.asNMSCopy(this.result);
        net.minecraft.world.item.ItemStack itemStack = CraftItemStack.asNMSCopy(smithingInput.base()).transmuteCopy(nmsResult.getItem(), nmsResult.getCount());
        if (this.copyDataComponents) {
            itemStack.applyComponents(nmsResult.getComponentsPatch());
        }

        return itemStack.getBukkitStack();
    }

    @Override
    public Optional<CustomRecipeChoice> templateIngredient() {
        return Optional.empty();
    }

    @Override
    public Optional<CustomRecipeChoice> baseIngredient() {
        return Optional.empty();
    }

    @Override
    public Optional<CustomRecipeChoice> additionIngredient() {
        return Optional.empty();
    }

    @Override
    public @Nullable Recipe toBukketRecipe(NamespacedKey key) {
        return null;
    }
}
