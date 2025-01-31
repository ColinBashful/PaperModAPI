package de.cjdev.papermodapi.api.recipe;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface CustomSmithingRecipe extends CustomRecipe<CustomSmithingRecipeInput> {
    default boolean matches(@NotNull CustomSmithingRecipeInput craftingInput) {
        return CustomIngredient.testOptionalIngredient(this.templateIngredient(), craftingInput.template()) && CustomIngredient.testOptionalIngredient(this.baseIngredient(), craftingInput.base()) && CustomIngredient.testOptionalIngredient(this.additionIngredient(), craftingInput.addition());
    }

    Optional<CustomIngredient> templateIngredient();

    Optional<CustomIngredient> baseIngredient();

    Optional<CustomIngredient> additionIngredient();
}
