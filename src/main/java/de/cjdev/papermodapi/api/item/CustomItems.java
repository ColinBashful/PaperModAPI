package de.cjdev.papermodapi.api.item;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import de.cjdev.papermodapi.PaperModAPI;
import net.kyori.adventure.key.Key;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
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
            PaperModAPI.LOGGER.warning(key.toString() + " has already been registered.");
            return items.get(key);
        }
        items.put(key, item);
        return item;
    }

    public static @Nullable CustomItem getItemByKey(Key key) {
        return items.get(key);
    }

    public static @Nullable CustomItem getItemByStack(ItemStack stack){
        return stack == null ? null : getItemByKey(getKeyByStack(stack));
    }

    public static @Nullable NamespacedKey getKeyByItem(CustomItem item) {
        return items.inverse().get(item);
    }

    public static boolean isCustomStack(ItemStack stack) {
        return getKeyByStack(stack) != null;
    }

    public static @Nullable NamespacedKey getKeyByStack(ItemStack stack) {
        CustomData customData = net.minecraft.world.item.ItemStack.fromBukkitCopy(stack).get(DataComponents.CUSTOM_DATA);
        if(customData == null || !customData.contains("papermodapi:item"))
            return null;
        DataResult<String> customItemId = customData.read(Codec.STRING.fieldOf("papermodapi:item"));
        return NamespacedKey.fromString(customItemId.mapOrElse(pair -> pair, stringError -> null), PaperModAPI.getPlugin());
    }

    public static List<ItemStack> getItemStacks(){
        return items.values().stream().map(CustomItem::getDefaultStack).collect(Collectors.toList());
    }
}
