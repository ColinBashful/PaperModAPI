package de.cjdev.papermodapi.api.item;

import de.cjdev.papermodapi.api.block.CustomBlockItem;
import de.cjdev.papermodapi.api.component.CustomDataComponents;
import de.cjdev.papermodapi.api.register.Registries;
import de.cjdev.papermodapi.api.register.Registry;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CustomItems {

    public static CustomItem register(NamespacedKey id, CustomItem.Settings settings) {
        return register(id, CustomItem::new, settings);
    }

    public static CustomItem register(NamespacedKey id) {
        return register(id, CustomItem::new, new CustomItem.Settings());
    }

    public static <T extends CustomItem> T register(NamespacedKey key, Function<CustomItem.Settings, T> factory) {
        return register(key, factory, new CustomItem.Settings());
    }

    public static <T extends CustomItem> T register(NamespacedKey key, Function<CustomItem.Settings, T> factory, CustomItem.Settings settings) {
        T item = Registry.register(Registries.ITEM, key, factory.apply(settings.registryKey(key)));
        if (item instanceof CustomBlockItem blockItem) {
            blockItem.appendBlocks(CustomItem.BLOCK_ITEMS, item);
        }
        return item;
    }

    public static @Nullable CustomItem getItemByKey(NamespacedKey key) {
        return Registries.ITEM.getValue(key);
    }

    public static @Nullable CustomItem getItemByStack(ItemStack stack){
        return stack == null ? null : getItemByKey(getKeyByStack(stack));
    }

    /**
     * @deprecated Use {@link CustomItem#getId(CustomItem)} or {@link CustomItem#getId()} instead.
     */
    @Deprecated(
            since = "1.2"
    )
    public static @Nullable NamespacedKey getKeyByItem(CustomItem item) {
        return item.getId();
    }

    public static boolean isCustomStack(ItemStack stack) {
        return CustomDataComponents.ITEM_COMPONENT.has(stack);
    }

    public static @Nullable NamespacedKey getKeyByStack(ItemStack stack) {
        return getKeyByStack(stack, false);
    }

    public static @Nullable NamespacedKey getKeyByStack(ItemStack stack, boolean includeVanilla) {
        return CustomDataComponents.ITEM_COMPONENT.getOrDefault(stack, includeVanilla ? stack.getType().getKey() : null);
    }

    public static Set<NamespacedKey> getItemKeys(){
        return Registries.ITEM.keySet();
    }

    public static List<ItemStack> getItemStacks(){
        return Registries.ITEM.stream().map(CustomItem::getDefaultStack).collect(Collectors.toList());
    }
}
