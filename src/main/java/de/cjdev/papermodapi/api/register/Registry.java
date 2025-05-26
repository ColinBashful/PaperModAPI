package de.cjdev.papermodapi.api.register;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.util.StringRepresentable;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class Registry<T> implements StringRepresentable, Iterable<T> {
    private final BiMap<NamespacedKey, T> ITEMS_MAP;

    public Registry() {
        ITEMS_MAP = HashBiMap.create();
    }

    public static <T, U extends T> U register(Registry<T> registry, NamespacedKey key, U value) {
        T registered = registry.ITEMS_MAP.putIfAbsent(key, value);
        if (registered != null) {
            throw new IllegalArgumentException("[" + registry.getSerializedName() + "] " + key.toString() + " has already been registered.");
        }
        return value;
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        ITEMS_MAP.values().forEach(action);
    }

    @Override
    public @NotNull Iterator<T> iterator() {
        return ITEMS_MAP.values().iterator();
    }

    @Override
    public Spliterator<T> spliterator() {
        return ITEMS_MAP.values().spliterator();
    }

    @Nullable
    public NamespacedKey getKey(T value) {
        return ITEMS_MAP.inverse().get(value);
    }

    @Nullable
    public T getValue(@Nullable NamespacedKey key) {
        return ITEMS_MAP.get(key);
    }

    public Set<NamespacedKey> keySet() {
        return ITEMS_MAP.keySet();
    }

    public Stream<T> stream() {
        return StreamSupport.stream(this.spliterator(), false);
    }

}
