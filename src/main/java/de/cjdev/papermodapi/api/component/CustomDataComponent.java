package de.cjdev.papermodapi.api.component;

import com.mojang.serialization.*;
import de.cjdev.papermodapi.PaperModAPI;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.component.CustomData;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.UnaryOperator;

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

    public Optional<T> getOptional(ItemStack stack) {
        return Optional.ofNullable(getOrDefault(stack, null));
    }

    public @Nullable T getOrDefault(ItemStack stack, @Nullable T defaultValue) {
        if (stack == null)
            return defaultValue;
        CustomData customData = CraftItemStack.unwrap(stack).get(DataComponents.CUSTOM_DATA);
        if (customData == null || !customData.contains(key.asString()))
            return defaultValue;
        return customData.read(CODEC.fieldOf(key.asString())).mapOrElse(t -> t, tError -> defaultValue);
    }

    public void set(ItemStack stack, @Nullable T value) {
        if (stack == null)
            return;
        CraftItemStack.unwrap(stack).update(DataComponents.CUSTOM_DATA, CustomData.EMPTY, customData -> {
            CompoundTag compoundTag = customData.copyTag();
            if (value != null) {
                DataResult<Tag> result = CODEC.encode(value, NbtOps.INSTANCE, NbtOps.INSTANCE.empty());
                result.resultOrPartial(PaperModAPI.LOGGER::warning).ifPresent(serializedData -> compoundTag.put(key.toString(), serializedData));
            } else
                compoundTag.remove(key.toString());
            return CustomData.of(compoundTag);
        });
    }

    /**
     * @return Returns whether the component was removed successfully
     */
    public boolean remove(ItemStack stack) {
        if (stack == null)
            return false;
        AtomicBoolean success = new AtomicBoolean();
        CraftItemStack.unwrap(stack).update(DataComponents.CUSTOM_DATA, CustomData.EMPTY, customData -> {
            CompoundTag compoundTag = customData.copyTag();
            if (compoundTag.contains(key.toString())) {
                compoundTag.remove(key.toString());
                success.set(true);
                return CustomData.of(compoundTag);
            }
            return customData;
        });
        return success.get();
    }

    /**
     * @return Returns whether the value changed
     */
    public boolean update(ItemStack stack, UnaryOperator<@Nullable T> updater) {
        return this.update(stack, updater, null);
    }

    /**
     * @return Returns whether the value changed
     */
    public boolean update(ItemStack stack, UnaryOperator<@Nullable T> updater, @Nullable T defaultValue) {
        if (stack == null)
            return false;
        T original = get(stack);
        T data = original == null ? defaultValue : original;
        T updated = updater.apply(data);
        if (updated != original) {
            set(stack, updated);
            return true;
        }
        return false;
    }
}
