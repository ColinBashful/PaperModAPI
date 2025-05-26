package de.cjdev.papermodapi.api.register;

import org.jetbrains.annotations.NotNull;

public class NamedRegister<T> extends Registry<T> {

    private final String name;

    public NamedRegister(@NotNull String name) {
        this.name = name;
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name;
    }
}
