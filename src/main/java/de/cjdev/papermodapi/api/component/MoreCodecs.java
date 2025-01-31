package de.cjdev.papermodapi.api.component;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.PrimitiveCodec;
import de.cjdev.papermodapi.PaperModAPI;
import org.bukkit.NamespacedKey;

public class MoreCodecs {
    public static final PrimitiveCodec<NamespacedKey> NAMESPACEDKEY = new PrimitiveCodec<>() {
        @Override
        public <T> DataResult<NamespacedKey> read(DynamicOps<T> ops, T input) {
            return ops.getStringValue(input).mapOrElse(key -> {
                NamespacedKey resultKey = NamespacedKey.fromString(key, PaperModAPI.getPlugin());
                if(resultKey != null)
                    return DataResult.success(resultKey);
                return DataResult.error(() -> "Something went wrong :/");
            }, stringError -> null);
        }

        @Override
        public <T> T write(DynamicOps<T> ops, NamespacedKey value) {
            return ops.createString(value.asString());
        }
    };
}
