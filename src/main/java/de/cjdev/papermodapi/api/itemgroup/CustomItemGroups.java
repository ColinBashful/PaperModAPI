package de.cjdev.papermodapi.api.itemgroup;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import de.cjdev.papermodapi.PaperModAPI;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class CustomItemGroups {
    private static final BiMap<NamespacedKey, CustomItemGroup> itemGroups = HashBiMap.create();

    public static CustomItemGroup register(NamespacedKey key, CustomItemGroup itemGroup) {
        if(itemGroups.containsKey(key)){
            PaperModAPI.LOGGER.warning("[ItemGroup] " + key.toString() + " has already been registered.");
            return itemGroups.get(key);
        }
        itemGroups.put(key, itemGroup);
        return itemGroup;
    }

    public static @Nullable CustomItemGroup getGroupByKey(NamespacedKey key) {
        return itemGroups.get(key);
    }

    public static @Nullable NamespacedKey getKeyByGroup(CustomItemGroup group) {
        return itemGroups.inverse().get(group);
    }

    public static Map<NamespacedKey, CustomItemGroup> getItemGroups(){
        return new HashMap<>(itemGroups);
    }
}
