package de.cjdev.papermodapi.api.recipe;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface CustomSmithingRecipe extends CustomRecipe<CustomSmithingRecipeInput> {
    default boolean matches(@NotNull CustomSmithingRecipeInput craftingInput) {
        return CustomRecipeChoice.testOptionalIngredient(this.templateIngredient(), craftingInput.template()) && CustomRecipeChoice.testOptionalIngredient(this.baseIngredient(), craftingInput.base()) && CustomRecipeChoice.testOptionalIngredient(this.additionIngredient(), craftingInput.addition());
    }

    Optional<CustomRecipeChoice> templateIngredient();

    Optional<CustomRecipeChoice> baseIngredient();

    Optional<CustomRecipeChoice> additionIngredient();
}
