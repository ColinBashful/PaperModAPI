package de.cjdev.papermodapi.api.recipe;

import de.cjdev.papermodapi.api.item.CustomItem;
import de.cjdev.papermodapi.api.item.CustomItems;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public interface CustomRecipeChoice extends Predicate<ItemStack> {
    static CustomRecipeChoice empty() {
        return new EmptyRecipeChoice();
    }

    static boolean testOptionalIngredient(Optional<CustomRecipeChoice> ingredient, @NotNull ItemStack stack) {
        return ingredient.map(recipeChoice -> recipeChoice.test(stack)).orElseGet(stack::isEmpty);
    }

    static @NotNull CustomRecipeChoice combine(CustomRecipeChoice... recipeChoices){
        return Arrays.stream(recipeChoices).reduce((recipeChoice, recipeChoice2) -> new PredicateChoice(recipeChoice.or(recipeChoice2))).orElse(CustomRecipeChoice.empty());
    }

    RecipeChoice toBukkitRecipeChoice();

    class PredicateChoice implements CustomRecipeChoice{
        private final Predicate<ItemStack> predicate;

        private PredicateChoice(Predicate<ItemStack> predicate){
            this.predicate = predicate;
        }

        @Override
        public boolean test(ItemStack stack) {
            return this.predicate.test(stack);
        }

        @Override
        public RecipeChoice toBukkitRecipeChoice() {
            return RecipeChoice.empty();
        }
    }

    class MaterialChoice implements CustomRecipeChoice {
        private final List<Material> materials;

        public MaterialChoice(List<Material> materials) {
            this.materials = materials;
        }

        public MaterialChoice(Material... materials) {
            this(List.of(materials));
        }

        @Override
        public boolean test(ItemStack stack) {
            if (CustomItems.getKeyByStack(stack) != null)
                return false;
            return materials.contains(stack.getType());
        }

        @Override
        public RecipeChoice toBukkitRecipeChoice() {
            return new RecipeChoice.MaterialChoice(this.materials);
        }
    }

    class CustomItemChoice implements CustomRecipeChoice {
        private final List<CustomItem> customItems;

        public CustomItemChoice(List<CustomItem> customItems) {
            this.customItems = customItems;
        }

        public CustomItemChoice(CustomItem... customItems) {
            this(List.of(customItems));
        }

        @Override
        public boolean test(ItemStack stack) {
            CustomItem customItem = CustomItems.getItemByStack(stack);
            return customItems.stream().anyMatch(customItem1 -> customItem1 == customItem);
        }

        @Override
        public RecipeChoice toBukkitRecipeChoice() {
            return new RecipeChoice.ExactChoice(customItems.stream().map(CustomItem::getDefaultStack).toList());
        }
    }

    record EmptyRecipeChoice() implements CustomRecipeChoice {
        @Override
        public RecipeChoice toBukkitRecipeChoice() {
            return RecipeChoice.empty();
        }

        @Override
        public boolean test(ItemStack stack) {
            return false;
        }
    }
}
