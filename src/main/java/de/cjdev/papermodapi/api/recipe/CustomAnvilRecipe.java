package de.cjdev.papermodapi.api.recipe;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface CustomAnvilRecipe extends CustomRecipe<CustomAnvilRecipeInput> {
    default boolean matches(@NotNull CustomAnvilRecipeInput craftingInput) {
        return CustomIngredient.testOptionalIngredient(this.baseIngredient(), craftingInput.base()) && CustomIngredient.testOptionalIngredient(this.additionIngredient(), craftingInput.addition());
    }

    Optional<CustomIngredient> baseIngredient();

    Optional<CustomIngredient> additionIngredient();
}
