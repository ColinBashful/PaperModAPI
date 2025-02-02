package de.cjdev.papermodapi.api.component;

import de.cjdev.papermodapi.PaperModAPI;
import org.bukkit.NamespacedKey;

public final class CustomDataComponents {
    public static final CustomDataComponent<NamespacedKey> ITEM_COMPONENT = new CustomDataComponent<>(PaperModAPI.key("item"), MoreCodecs.NAMESPACEDKEY);
}
