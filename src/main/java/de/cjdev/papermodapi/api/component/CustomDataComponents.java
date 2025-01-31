package de.cjdev.papermodapi.api.component;

import com.mojang.serialization.Codec;
import de.cjdev.papermodapi.PaperModAPI;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.NamespacedKey;

public final class CustomDataComponents {
    public static final CustomDataComponent<NamespacedKey> ITEM_COMPONENT = new CustomDataComponent<>(PaperModAPI.key("item"), MoreCodecs.NAMESPACEDKEY);
    public static final CustomDataComponent<Integer> FUEL_COMPONENT = new CustomDataComponent<>(PaperModAPI.key("fuel"), Codec.INT);
}
