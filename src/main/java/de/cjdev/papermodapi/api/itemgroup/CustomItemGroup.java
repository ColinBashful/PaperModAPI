package de.cjdev.papermodapi.api.itemgroup;

import de.cjdev.papermodapi.api.item.CustomItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Supplier;

public class CustomItemGroup {
    public final Component displayName;
    private Collection<ItemStack> displayStacks;
    public final Supplier<ItemStack> iconSupplier;
    public final EntryCollector entryCollector;

    public void updateEntries(boolean hasOp){
        EntriesImpl entriesImpl = new EntriesImpl(this);
        this.entryCollector.accept(hasOp, entriesImpl);
        this.displayStacks = entriesImpl.stacks;
    }

    public Collection<ItemStack> getDisplayStacks(){
        return displayStacks;
    }

    private CustomItemGroup(Supplier<ItemStack> iconSupplier, Component displayName, EntryCollector entryCollector){
        this.iconSupplier = iconSupplier;
        this.displayName = displayName;
        this.entryCollector = entryCollector;
        this.updateEntries(true);
    }

    public static Builder builder(){
        return new Builder();
    }

    public record DisplayContext(boolean hasPermissions) {
        public boolean doesNotMatch(boolean hasPermissions) {
            return this.hasPermissions != hasPermissions;
        }

        public boolean hasPermissions() {
            return this.hasPermissions;
        }
    }

    public static class Builder{
        private static final EntryCollector EMPTY_ENTRIES = (hasOp, entries) -> {};
        private Component displayName = Component.empty();
        private Supplier<ItemStack> iconSupplier;
        private EntryCollector entryCollector;

        private Builder(){
            this.entryCollector = EMPTY_ENTRIES;
        }

        public Builder displayName(Component displayName){
            this.displayName = displayName;
            return this;
        }

        public Builder icon(Supplier<ItemStack> iconSupplier){
            this.iconSupplier = iconSupplier;
            return this;
        }

        public Builder entries(EntryCollector entryCollector){
            this.entryCollector = entryCollector;
            return this;
        }

        public CustomItemGroup build(){
            return new CustomItemGroup(iconSupplier, displayName, entryCollector);
        }
    }

    static class EntriesImpl implements Entries {
        public final List<ItemStack> stacks = new ArrayList<>();
        private final CustomItemGroup group;

        public EntriesImpl(CustomItemGroup group) {
            this.group = group;
        }

        @Override
        public void add(ItemStack stack) {
            if (stacks.contains(stack))
                throw new IllegalStateException("Accidentally adding the same item stack twice " + PlainTextComponentSerializer.plainText().serialize(stack.displayName()) + " to a Creative Mode Tab: " + PlainTextComponentSerializer.plainText().serialize(this.group.displayName));
            stacks.add(stack.asOne());
        }
    }

    public interface Entries {
        void add(ItemStack stack);

        default void add(CustomItem item){
            this.add(item.getDefaultStack());
        }

        default void add(Material material) {
            this.add(ItemStack.of(material));
        }

        default void addAllStacks(Collection<? extends ItemStack> stacks) {
            stacks.forEach(this::add);
        }

        default void addAll(Collection<CustomItem> items) {
            items.forEach(this::add);
        }
    }

    @FunctionalInterface
    public interface EntryCollector {
        void accept(boolean hasOp, Entries entries);
    }
}
