package de.cjdev.papermodapi.api.registry;

import de.cjdev.papermodapi.PaperModAPI;
import de.cjdev.papermodapi.api.item.CustomItem;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class CustomRegistries {
    public static final ResourceKey<Registry<CustomItem>> ITEM = createRegistryKey("customitem");

    private static <T> ResourceKey<Registry<T>> createRegistryKey(String name) {
        return ResourceKey.createRegistryKey(PaperModAPI.getResourceLocation(name));
    }
}
