package de.cjdev.papermodapi.api.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class SimpleItem extends Item implements PaperModAPIItem {
    public SimpleItem(Properties properties) {
        super(properties);
    }

    @Override
    public Item getClientItem() {
        return Items.TRIAL_KEY;
    }
}
