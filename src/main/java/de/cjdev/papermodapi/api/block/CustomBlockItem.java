package de.cjdev.papermodapi.api.block;

import de.cjdev.papermodapi.api.item.CustomItem;

import java.util.Map;

public interface CustomBlockItem {
    default void appendBlocks(Map<CustomBlock, CustomItem> blockToItemMap, CustomItem item) {
        blockToItemMap.put(this.getBlock(), item);
    }

    CustomBlock getBlock();
}
