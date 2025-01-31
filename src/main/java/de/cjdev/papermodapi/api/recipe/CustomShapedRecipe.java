package de.cjdev.papermodapi.api.recipe;

import com.google.common.base.Preconditions;
import de.cjdev.papermodapi.api.item.CustomItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class CustomShapedRecipe implements CustomCraftingRecipe {
    private @NotNull String group = "";
    private CraftingBookCategory category;
    private final ItemStack result;
    private String[] shape;
    private int width;
    private int height;
    private Map<Character, CustomIngredient> ingredientMap;

    public CustomShapedRecipe(ItemStack result) {
        this.result = result;
        this.ingredientMap = new HashMap<>();
    }

    public CustomShapedRecipe(Material result) {
        this.result = ItemStack.of(result);
        this.ingredientMap = new HashMap<>();
    }

    public CustomShapedRecipe(CustomItem result) {
        this.result = result.getDefaultStack();
        this.ingredientMap = new HashMap<>();
    }

    public CustomShapedRecipe setGroup(@NotNull String group){
        this.group = group;
        return this;
    }

    public CustomShapedRecipe setCategory(@NotNull CraftingBookCategory category){
        this.category = category;
        return this;
    }

    public CustomShapedRecipe shape(String... shape){
        Preconditions.checkArgument(shape != null, "Must provide a shape");
        Preconditions.checkArgument(shape.length > 0 && shape.length < 4, "Crafting recipes should be 1, 2 or 3 rows, not ", shape.length);
        int lastLen = -1;

        for(String row : shape) {
            Preconditions.checkArgument(row != null, "Shape cannot have null rows");
            Preconditions.checkArgument(!row.isEmpty() && row.length() < 4, "Crafting rows should be 1, 2, or 3 characters, not ", row.length());
            Preconditions.checkArgument(lastLen == -1 || lastLen == row.length(), "Crafting recipes must be rectangular");
            lastLen = row.length();
        }

        this.width = lastLen;
        this.height = shape.length;

        this.shape = new String[shape.length];

        System.arraycopy(shape, 0, this.shape, 0, shape.length);

        HashMap<Character, CustomIngredient> newIngredientMap = new HashMap<>();

        for(String row : shape) {
            for(char c : row.toCharArray()) {
                if (c != ' ') {
                    newIngredientMap.put(c, this.ingredientMap.get(c));
                }
            }
        }

        this.ingredientMap = newIngredientMap;
        return this;
    }

    public CustomIngredient getIngredient(char ingredientChar){
        return this.ingredientMap.get(ingredientChar);
    }

    public CustomShapedRecipe setIngredient(char key, @NotNull CustomIngredient recipeChoice){
        Preconditions.checkArgument(key != ' ', "Space in recipe shape must represent no ingredient");
        Preconditions.checkArgument(this.ingredientMap.containsKey(key), "Symbol does not appear in the shape:", key);
        ingredientMap.put(key, recipeChoice.clone());
        return this;
    }

    public CustomShapedRecipe setIngredient(char key, @NotNull NamespacedKey material){
        return this.setIngredient(key, new CustomIngredient(material));
    }

    public CustomShapedRecipe setIngredient(char key, @NotNull Material material){
        return this.setIngredient(key, material.getKey());
    }

    public CustomShapedRecipe setIngredient(char key, @NotNull CustomItem customItem) {
        return this.setIngredient(key, new CustomIngredient(customItem));
    }

    @Override
    public @NotNull CraftingBookCategory category() {
        return this.category;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CustomCraftingInput craftingInput) {
        return this.result.clone();
    }

    /**
     * Checks if the recipe matches the crafting grid at a specific position and orientation.
     *
     * @param craftingInput The crafting input.
     * @param startRow The starting row in the crafting grid.
     * @param startCol The starting column in the crafting grid.
     * @param mirrored Whether to check the mirrored orientation.
     * @return true if the recipe matches at the given position and orientation, false otherwise.
     */
    private boolean matchesAtPosition(
            @NotNull CustomCraftingInput craftingInput,
                                      int startRow,
                                      int startCol,
                                      boolean mirrored
    ) {
        for (int row = 0; row < this.height; row++) {
            for (int col = 0; col < this.width; col++) {
                int recipeCol = mirrored ? (this.width - col - 1) : col;
                int craftIndex = (startRow + row) * craftingInput.width() + (startCol + col);

                if(recipeCol >= this.width || row >= this.shape.length)
                    return false;
                char recipeChar = this.shape[row].charAt(recipeCol); // Get the character at the position in the shape

                if (recipeChar == ' ') continue; // Skip empty spots
                ItemStack itemStack = craftingInput.getItem(craftIndex);

                // Get the CustomRecipeChoice for the current character using the getIngredient method
                CustomIngredient choice = this.getIngredient(recipeChar);
                if (choice == null || !choice.test(itemStack)) {
                    return false;
                }
            }
        }
        return true; // All ingredients matched
    }

    @Override
    public boolean matches(@NotNull CustomCraftingInput craftingInput) {
        int craftWidth = craftingInput.width();
        int craftHeight = craftingInput.height();
        int recipeWidth = this.width;
        int recipeHeight = this.height;

        // Ensure recipe fits within the crafting input dimensions
        if (recipeWidth > craftWidth || recipeHeight > craftHeight) {
            return false;
        }

        // Try matching recipe in all possible positions
        for (int startRow = 0; startRow <= craftHeight - recipeHeight; startRow++) {
            for (int startCol = 0; startCol <= craftWidth - recipeWidth; startCol++) {
                // Check normal orientation
                if (matchesAtPosition(craftingInput, startRow, startCol, false)) {
                    return true;
                }

                // Check mirrored orientation
                if (matchesAtPosition(craftingInput, startRow, startCol, true)) {
                    return true;
                }
            }
        }

        return false; // No match found
    }

    @Override
    public ShapedRecipe toBukketRecipe(@NotNull NamespacedKey key) {
        ShapedRecipe bucketRecipe = new ShapedRecipe(key, this.result);
        bucketRecipe.setGroup(group);
        bucketRecipe.setCategory(category == null ? CraftingBookCategory.MISC : category);
        bucketRecipe.shape(this.shape);
        this.ingredientMap.forEach((character, recipeChoice) -> bucketRecipe.setIngredient(character, recipeChoice.toBukkitRecipeChoice()));
        return bucketRecipe;
    }
}
