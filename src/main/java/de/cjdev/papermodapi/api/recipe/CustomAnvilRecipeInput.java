package de.cjdev.papermodapi.api.recipe;

import de.cjdev.papermodapi.api.item.CustomItems;
import org.bukkit.inventory.ItemStack;

public class CustomAnvilRecipeInput implements CustomRecipeInput {
    private final ItemStack base;
    private final ItemStack addition;
    private final boolean anyCustom;

    public CustomAnvilRecipeInput(ItemStack base, ItemStack addition) {
        this.base = base == null ? ItemStack.empty() : base;
        this.addition = addition == null ? ItemStack.empty() : addition;

        this.anyCustom = CustomItems.isCustomStack(base) || CustomItems.isCustomStack(addition);
    }

    public boolean anyCustom(){
        return this.anyCustom;
    }

    @Override
    public ItemStack getItem(int index) {
        return switch (index) {
            case 0 -> this.base;
            case 1 -> this.addition;
            default -> throw new IllegalArgumentException("Recipe does not contain slot " + index);
        };
    }

    @Override
    public int size() {
        return 2;
    }

    public boolean isEmpty() {
        return this.base.isEmpty() && this.addition.isEmpty();
    }


    public ItemStack base() {
        return this.base;
    }

    public ItemStack addition() {
        return this.addition;
    }
}
