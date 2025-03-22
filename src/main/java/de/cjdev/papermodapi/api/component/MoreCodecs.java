package de.cjdev.papermodapi.api.component;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.PrimitiveCodec;
import de.cjdev.papermodapi.PaperModAPI;
import org.bukkit.NamespacedKey;

import java.util.UUID;
import java.util.stream.IntStream;

public class MoreCodecs {
    public static final PrimitiveCodec<NamespacedKey> NAMESPACEDKEY = new PrimitiveCodec<>() {
        @Override
        public <T> DataResult<NamespacedKey> read(DynamicOps<T> ops, T input) {
            return ops.getStringValue(input).mapOrElse(key -> {
                NamespacedKey resultKey = NamespacedKey.fromString(key, PaperModAPI.getPlugin());
                if (resultKey != null)
                    return DataResult.success(resultKey);
                return DataResult.error(() -> "Something went wrong :/");
            }, stringError -> null);
        }

        @Override
        public <T> T write(DynamicOps<T> ops, NamespacedKey value) {
            return ops.createString(value.asString());
        }
    };

    public static final PrimitiveCodec<UUID> UUID = new PrimitiveCodec<>() {
        @Override
        public <T> DataResult<UUID> read(DynamicOps<T> ops, T input) {
            return ops.getIntStream(input).mapOrElse(intStream -> {
                int[] parts = intStream.toArray();

                if (parts.length != 4) {
                    return DataResult.error(() -> "Quit modifying NBT");
                }

                long mostSigBits = ((long) parts[0] << 32) | (parts[1] & 0xFFFFFFFFL);
                long leastSigBits = ((long) parts[2] << 32) | (parts[3] & 0xFFFFFFFFL);

                return DataResult.success(new UUID(mostSigBits, leastSigBits));
            }, intStreamError -> null);
        }

        @Override
        public <T> T write(DynamicOps<T> ops, UUID value) {
            long mostSigBits = value.getMostSignificantBits();
            long leastSigBits = value.getLeastSignificantBits();

            return ops.createIntList(IntStream.of(
                    (int) (mostSigBits >> 32),
                    (int) mostSigBits,
                    (int) (leastSigBits >> 32),
                    (int) leastSigBits
            ));
        }
    };
}
