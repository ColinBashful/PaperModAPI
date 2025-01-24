package de.cjdev.papermodapi.api.recipe;

import org.bukkit.inventory.ItemStack;

public interface CustomRecipeInput {
    ItemStack getItem(int index);

    int size();

    default boolean isEmpty() {
        for(int i = 0; i < this.size(); ++i) {
            if (!this.getItem(i).isEmpty()) {
                return false;
            }
        }

        return true;
    }
}
