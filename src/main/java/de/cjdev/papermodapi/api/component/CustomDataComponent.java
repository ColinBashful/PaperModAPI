package de.cjdev.papermodapi.api.component;

import com.mojang.serialization.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.component.CustomData;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class CustomDataComponent<T> {
    private final NamespacedKey key;
    private final Codec<T> CODEC;

    public CustomDataComponent(NamespacedKey key, Codec<T> CODEC) {
        this.key = key;
        this.CODEC = CODEC;
    }

    public boolean has(ItemStack stack) {
        if (stack == null)
            return false;
        CustomData customData = CraftItemStack.unwrap(stack).get(DataComponents.CUSTOM_DATA);
        return customData != null && customData.contains(key.asString());
    }

    public @Nullable T get(ItemStack stack) {
        if (stack == null)
            return null;
        CustomData customData = CraftItemStack.unwrap(stack).get(DataComponents.CUSTOM_DATA);
        if (customData == null || !customData.contains(key.asString()))
            return null;
        return customData.read(CODEC.fieldOf(key.asString())).mapOrElse(t -> t, tError -> null);
    }

    public @Nullable T getOrDefault(ItemStack stack, T defaultValue) {
        if (stack == null)
            return defaultValue;
        CustomData customData = CraftItemStack.unwrap(stack).get(DataComponents.CUSTOM_DATA);
        if (customData == null || !customData.contains(key.asString()))
            return defaultValue;
        return customData.read(CODEC.fieldOf(key.asString())).mapOrElse(t -> t, tError -> defaultValue);
    }

    public void set(ItemStack stack, T value) {
        if (stack == null)
            return;
        net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.unwrap(stack);
        CustomData customData = nmsStack.get(DataComponents.CUSTOM_DATA);

        CompoundTag compoundTag = customData == null ? new CompoundTag() : customData.copyTag();

        DataResult<Tag> result = CODEC.encode(value, NbtOps.INSTANCE, NbtOps.INSTANCE.empty());

        result.resultOrPartial(System.err::println).ifPresent(serializedData -> {
            compoundTag.put(key.toString(), serializedData);
        });

        nmsStack.set(DataComponents.CUSTOM_DATA, CustomData.of(compoundTag));
    }

    public void update(ItemStack stack, Consumer<T> updateConsumer) {
        this.update(stack, updateConsumer, null);
    }

    public void update(ItemStack stack, Consumer<T> updateConsumer, T defaultValue) {
        if (stack == null)
            return;
        T originalData = get(stack);
        if (originalData == null) {
            if (defaultValue == null) {
                return;
            } else {
                originalData = defaultValue;
            }
        }
        updateConsumer.accept(originalData);
        set(stack, originalData);
    }
}
