package de.cjdev.papermodapi.api.recipe;

import de.cjdev.papermodapi.api.item.CustomItems;
import org.bukkit.inventory.ItemStack;

public class CustomCookingRecipeInput implements CustomRecipeInput {
    private final ItemStack fuel;
    private final ItemStack source;
    private final boolean isCustom;

    public CustomCookingRecipeInput(ItemStack fuel, ItemStack source) {
        this.fuel = fuel == null ? ItemStack.empty() : fuel;
        this.source = source == null ? ItemStack.empty() : source;

        this.isCustom = CustomItems.isCustomStack(source);
    }

    public boolean isCustom(){
        return this.isCustom;
    }

    @Override
    public ItemStack getItem(int index) {
        if (index == 0) {
            return this.source;
        }
        throw new IllegalArgumentException("Recipe does not contain slot " + index);
    }

    @Override
    public int size() {
        return 3;
    }

    public boolean isEmpty() {
        return this.source.isEmpty();
    }

    public ItemStack fuel() {
        return this.fuel;
    }

    public ItemStack source() {
        return this.source;
    }
}
