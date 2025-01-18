package de.cjdev.papermodapi.api.item;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import de.cjdev.papermodapi.PaperModAPI;
import net.kyori.adventure.key.Key;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CustomItems {
    private static final BiMap<Key, CustomItem> items = HashBiMap.create();

    private static Key createItemId(String id){
        return Key.key("papermodapi", id);
    }

    public static CustomItem register(String id, Function<CustomItem.Settings, CustomItem> factory){
        return register(createItemId(id), factory, new CustomItem.Settings());
    }

    public static CustomItem register(String id, Function<CustomItem.Settings, CustomItem> factory, CustomItem.Settings settings){
        return register(createItemId(id), factory, settings);
    }

    public static CustomItem register(Key id, CustomItem.Settings settings) {
        return register(id, CustomItem::new, settings);
    }

    public static CustomItem register(String id, CustomItem.Settings settings) {
        return register(createItemId(id), CustomItem::new, settings);
    }

    public static CustomItem register(Key id) {
        return register(id, CustomItem::new, new CustomItem.Settings());
    }

    public static CustomItem register(String id) {
        return register(createItemId(id), CustomItem::new, new CustomItem.Settings());
    }

    public static CustomItem register(Key key, Function<CustomItem.Settings, CustomItem> factory) {
        return register(key, factory, new CustomItem.Settings());
    }

    public static CustomItem register(Key key, Function<CustomItem.Settings, CustomItem> factory, CustomItem.Settings settings) {
        CustomItem item = factory.apply(settings.registryKey(key));
//        if (item instanceof CustomBlockItem blockItem) {
//            blockItem.appendBlocks(CustomItem.BLOCK_ITEMS, item);
//        }

        if(items.containsKey(key)){
            PaperModAPI.LOGGER.warning(key.toString() + " has already been registered.");
            return items.get(key);
        }
        items.put(key, item);
        return item;
    }

    public static @Nullable CustomItem getItemByKey(Key key) {
        return items.get(key);
    }

    public static @Nullable Key getKeyByItem(CustomItem item) {
        return items.inverse().get(item);
    }

    public static List<ItemStack> getItemStacks(){
        return items.values().stream().map(CustomItem::getDefaultStack).collect(Collectors.toList());
    }
}
