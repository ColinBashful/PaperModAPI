package de.cjdev.papermodapi.api.recipe;

import de.cjdev.papermodapi.PaperModAPI;
import de.cjdev.papermodapi.api.item.CustomItems;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CustomCraftingInput implements CustomRecipeInput {
    public static final CustomCraftingInput EMPTY = new CustomCraftingInput(0, 0, List.of());
    private final int width;
    private final int height;
    private final List<ItemStack> items;
    private final List<ItemStack> ingredients;
    private final int ingredientCount;
    private final boolean anyCustom;

    public CustomCraftingInput(int width, int height, @NotNull List<ItemStack> item) {
        this.width = width;
        this.height = height;
        this.items = Collections.unmodifiableList(item);
        int i = 0;
        boolean anyCustom = false;

        List<ItemStack> ingredients = new ArrayList<>();

        for (ItemStack itemStack : item) {
            if (!itemStack.isEmpty()) {
                ++i;
                ingredients.add(itemStack);
                if (CustomItems.isCustomStack(itemStack))
                    anyCustom = true;
            }
        }

        this.ingredientCount = i;
        this.ingredients = Collections.unmodifiableList(ingredients);
        this.anyCustom = anyCustom;
    }

    public boolean anyCustom(){
        return this.anyCustom;
    }

    public int width() {
        return this.width;
    }

    public int height() {
        return this.height;
    }

    public int ingredientCount() {
        return this.ingredientCount;
    }

    public boolean isEmpty() {
        return this.ingredientCount == 0;
    }

    public List<ItemStack> items() {
        return this.items;
    }

    public List<ItemStack> ingredients() {
        return this.ingredients;
    }

    @Override
    public ItemStack getItem(int index) {
        return this.items.get(index);
    }

    @Override
    public int size() {
        return this.items.size();
    }
}
