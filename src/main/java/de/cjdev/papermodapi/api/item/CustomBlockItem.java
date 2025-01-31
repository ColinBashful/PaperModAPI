package de.cjdev.papermodapi.api.item;

import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

public class CustomBlockItem extends CustomItem {
    public CustomBlockItem(Settings settings) {
        super(settings);
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.text("idkfk how I should check, I'd need RegistryKey for that...");
    }
}
