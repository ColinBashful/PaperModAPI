package de.cjdev.papermodapi.api.item;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import de.cjdev.papermodapi.PaperModAPI;
import de.cjdev.papermodapi.api.component.CustomDataComponents;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CustomItems {
    private static final BiMap<NamespacedKey, CustomItem> items = HashBiMap.create();

    public static CustomItem register(NamespacedKey id, CustomItem.Settings settings) {
        return register(id, CustomItem::new, settings);
    }

    public static CustomItem register(NamespacedKey id) {
        return register(id, CustomItem::new, new CustomItem.Settings());
    }

    public static CustomItem register(NamespacedKey key, Function<CustomItem.Settings, CustomItem> factory) {
        return register(key, factory, new CustomItem.Settings());
    }

    public static CustomItem register(NamespacedKey key, Function<CustomItem.Settings, CustomItem> factory, CustomItem.Settings settings) {
        CustomItem item = factory.apply(settings.registryKey(key));
//        if (item instanceof CustomBlockItem blockItem) {
//            blockItem.appendBlocks(CustomItem.BLOCK_ITEMS, item);
//        }

        if(items.containsKey(key)){
            PaperModAPI.LOGGER.warning("[Item] " + key.toString() + " has already been registered.");
            return items.get(key);
        }
        items.put(key, item);
        return item;
    }

    public static @Nullable CustomItem getItemByKey(NamespacedKey key) {
        return items.get(key);
    }

    public static @Nullable CustomItem getItemByStack(ItemStack stack){
        return stack == null ? null : getItemByKey(getKeyByStack(stack));
    }

    public static @Nullable NamespacedKey getKeyByItem(CustomItem item) {
        return items.inverse().get(item);
    }

    public static boolean isCustomStack(ItemStack stack) {
        return CustomDataComponents.ITEM_COMPONENT.has(stack);
    }

    public static @Nullable NamespacedKey getKeyByStack(ItemStack stack) {
        return CustomDataComponents.ITEM_COMPONENT.get(stack);
    }

    public static List<ItemStack> getItemStacks(){
        return items.values().stream().map(CustomItem::getDefaultStack).collect(Collectors.toList());
    }
}
