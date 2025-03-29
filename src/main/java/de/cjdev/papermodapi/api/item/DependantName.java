package de.cjdev.papermodapi.api.item;

@FunctionalInterface
public interface DependantName<T, V> {
    V get(T var1);

    static <T, V> DependantName<T, V> fixed(V value) {
        return (key) -> value;
    }
}