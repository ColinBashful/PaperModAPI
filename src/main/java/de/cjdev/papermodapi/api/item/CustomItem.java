package de.cjdev.papermodapi.api.item;

import de.cjdev.papermodapi.api.component.CustomDataComponent;
import de.cjdev.papermodapi.api.component.CustomDataComponents;
import de.cjdev.papermodapi.api.util.ActionResult;
import de.cjdev.papermodapi.api.util.ItemUsageContext;
import de.cjdev.papermodapi.api.util.Util;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.*;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.keys.tags.DamageTypeTagKeys;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class CustomItem {
    //    public static final Map<CustomBlock, CustomItem> BLOCK_ITEMS = Maps.newHashMap();
    public static final int DEFAULT_MAX_COUNT = 64;
    public static final int MAX_MAX_COUNT = 99;
    public static final int ITEM_BAR_STEPS = 13;
    private final Material baseMaterial;
    private final boolean dyeable;
    private final Map<DataComponentType, Object> components;
    @Nullable
    private final Consumer<ItemStack> recipeRemainder;
    @Nullable
    private final String translationKey;

    private final ItemStack defaultStack;
    private final ItemStack displayStack;

    public static @Nullable NamespacedKey getId(CustomItem item) {
        return item == null ? null : CustomItems.getKeyByItem(item);
    }

    public static @Nullable CustomItem byId(NamespacedKey key) {
        return key == null ? null : CustomItems.getItemByKey(key);
    }

    public CustomItem(Settings settings) {
        this.baseMaterial = settings.getBaseMaterial();
        this.dyeable = settings.canDye();
        this.translationKey = settings.getTranslationKey();
        this.components = settings.getValidatedComponents();
        this.recipeRemainder = settings.recipeRemainder;

        NamespacedKey itemId = settings.registryKey;
        ItemStack defaultStack = ItemStack.of(this.getBaseMaterial());
        ItemStack displayStack = defaultStack.clone();

        defaultStack.editMeta(itemMeta -> {
            itemMeta.itemName(getName());
            itemMeta.setItemModel(settings.getModelId());
        });
        displayStack.editMeta(itemMeta -> itemMeta.setItemModel(itemId));

        this.components.forEach((type, o) -> {
            if (type == DataComponentTypes.CUSTOM_MODEL_DATA) {
                displayStack.setData(DataComponentTypes.CUSTOM_MODEL_DATA, (CustomModelData) this.components.get(DataComponentTypes.CUSTOM_MODEL_DATA));
            }
            if (type == DataComponentTypes.USE_COOLDOWN) {
                defaultStack.setData(DataComponentTypes.USE_COOLDOWN, UseCooldown.useCooldown(((UseCooldown) o).seconds()).cooldownGroup(itemId).build());
                return;
            }
            if (type instanceof DataComponentType.Valued valued)
                defaultStack.setData(valued, o);
            else if (type instanceof DataComponentType.NonValued nonValued) {
                defaultStack.setData(nonValued);
            }
        });

        Map<CustomDataComponent, ?> customData = settings.getCustomComponents();
        customData.forEach((customDataComponent, o) -> customDataComponent.set(defaultStack, o));
        CustomDataComponents.ITEM_COMPONENT.set(defaultStack, itemId);

        this.defaultStack = defaultStack;
        this.displayStack = displayStack;
    }

    public final ItemStack getDefaultStack() {
        return this.defaultStack.clone();
    }

    public final boolean isSimilar(@Nullable ItemStack stack) {
        if (stack == null || stack.isEmpty())
            return false;
        return CustomItems.getKeyByStack(stack) == getId(this);
    }

    /// Simply returns the DefaultStack without the unnecessary components
    public final ItemStack getDisplayStack() {
        return this.displayStack.clone();
    }

    public Material getBaseMaterial() {
        return this.baseMaterial;
    }

    public final Component getName() {
        return Component.translatable(this.translationKey);
    }

    public Component getName(ItemStack stack) {
        return stack == null ? Component.empty() : stack.getDataOrDefault(DataComponentTypes.ITEM_NAME, Component.empty());
    }

    public @Nullable Consumer<ItemStack> getRecipeRemainder() {
        return this.recipeRemainder;
    }

    public Key getBreakSound() {
        return Key.key("minecraft", "entity.item.break");
    }

    public String toString() {
        return CustomItems.getKeyByItem(this).asString();
    }

    public String getTranslationKey() {
        return this.translationKey;
    }

    public @NotNull ActionResult useOnBlock(ItemUsageContext context) {
        return ActionResult.PASS;
    }

    public @NotNull ActionResult useOnEntity(ItemStack stack, @Nullable Player user, Entity entity, EquipmentSlot hand) {
        return ActionResult.PASS;
    }

    public @NotNull ActionResult use(World world, Player player, EquipmentSlot hand) {
        return ActionResult.PASS;
    }

    public void usageTick(World world, LivingEntity user, ItemStack stack, int ticksHeldFor) {
    }

    public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int ticksHeldFor) {
        return false;
    }

    public void onCraft(ItemStack stack, World world) {
    }

    public void onCraftByPlayer(ItemStack stack, World world, Player player) {
    }

    public void onItemEntityDestroyed(Item entity) {
    }

    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
    }

    public static boolean sameItem(ItemStack stack, ItemStack otherStack) {
        if (stack == null || otherStack == null || stack.isEmpty() == otherStack.isEmpty())
            return false;
        NamespacedKey item = CustomDataComponents.ITEM_COMPONENT.get(stack);
        NamespacedKey otherItem = CustomDataComponents.ITEM_COMPONENT.get(otherStack);
        return item == otherItem;
    }

    public static class Settings {
        private static final Function<NamespacedKey, String> BLOCK_PREFIXED_TRANSLATION_KEY = id -> Util.createTranslationKey("block", id);
        private static final Function<NamespacedKey, String> ITEM_PREFIXED_TRANSLATION_KEY = id -> Util.createTranslationKey("item", id);
        private @NotNull Material baseMaterial;
        private boolean dyeable;
        private @Nullable Consumer<ItemStack> recipeRemainder;
        private final Map<DataComponentType, Object> components = new HashMap<>();
        private final Map<CustomDataComponent, Object> customComponents = new HashMap<>();
        private NamespacedKey registryKey;
        private Function<NamespacedKey, String> translationKey;
        private Function<NamespacedKey, NamespacedKey> modelId;
        private @Nullable NamespacedKey repairable;

        public Settings() {
            this.baseMaterial = Material.PAPER;
            this.dyeable = false;
            this.translationKey = ITEM_PREFIXED_TRANSLATION_KEY;
            this.modelId = key -> key;
        }

        public Settings dyeable() {
            this.dyeable = true;
            return this;
        }

        public Settings food(FoodProperties food) {
            return this.food(food, Consumable.consumable().build());
        }

        public Settings food(FoodProperties food, Consumable consumable) {
            return this.component(DataComponentTypes.FOOD, food).component(DataComponentTypes.CONSUMABLE, consumable);
        }

        public Settings useRemainder(ItemStack convertInto) {
            return this.component(DataComponentTypes.USE_REMAINDER, UseRemainder.useRemainder(convertInto));
        }

        public Settings useCooldown(float seconds) {
            return this.component(DataComponentTypes.USE_COOLDOWN, UseCooldown.useCooldown(seconds).build());
        }

        public Settings maxCount(int maxCount) {
            return this.component(DataComponentTypes.MAX_STACK_SIZE, maxCount);
        }

        public Settings maxDamage(int maxDamage) {
            this.component(DataComponentTypes.MAX_DAMAGE, maxDamage);
            this.component(DataComponentTypes.MAX_STACK_SIZE, 1);
            this.component(DataComponentTypes.DAMAGE, 0);
            return this;
        }

        public Settings recipeRemainder(Consumer<ItemStack> recipeRemainder) {
            this.recipeRemainder = recipeRemainder;
            return this;
        }

        public Settings rarity(ItemRarity rarity) {
            return this.component(DataComponentTypes.RARITY, rarity);
        }

        public Settings fireproof() {
            return this.component(DataComponentTypes.DAMAGE_RESISTANT, DamageResistant.damageResistant(DamageTypeTagKeys.IS_FIRE));
        }

        public Settings jukeboxPlayable(JukeboxSong songKey) {
            return this.component(DataComponentTypes.JUKEBOX_PLAYABLE, JukeboxPlayable.jukeboxPlayable(songKey).build());
        }

        public Settings enchantable(int enchantability) {
            return this.component(DataComponentTypes.ENCHANTABLE, Enchantable.enchantable(enchantability));
        }

        public Settings repairable(Material repairIngredient) {
            return this.component(DataComponentTypes.REPAIRABLE, Repairable.repairable(RegistrySet.keySet(RegistryKey.ITEM, TypedKey.create(RegistryKey.ITEM, repairIngredient.getKey()))));
        }

        public Settings repairable(RegistryKeySet<ItemType> repairIngredientsTag) {
            return this.component(DataComponentTypes.REPAIRABLE, Repairable.repairable(repairIngredientsTag));
        }

        public Settings equippable(EquipmentSlot slot) {
            return this.component(DataComponentTypes.EQUIPPABLE, Equippable.equippable(slot).build());
        }

        public Settings equippableUnswappable(EquipmentSlot slot) {
            return this.component(DataComponentTypes.EQUIPPABLE, Equippable.equippable(slot).swappable(false).build());
        }

        public Settings registryKey(NamespacedKey registryKey) {
            this.registryKey = registryKey;
            return this;
        }

        public Settings baseMaterial(@NotNull Material baseMaterial) {
            this.baseMaterial = baseMaterial;
            return this;
        }

        public boolean canDye() {
            return this.dyeable;
        }

        private @NotNull Material getBaseMaterial() {
            return this.baseMaterial;
        }

        public Settings translationKey(String translationKey) {
            this.translationKey = customItem -> translationKey;
            return this;
        }

        public Settings modelId(NamespacedKey key){
            this.modelId = customItem -> key;
            return this;
        }

        private String getTranslationKey() {
            return this.translationKey.apply(this.registryKey);
        }

        private NamespacedKey getModelId() {
            return this.modelId.apply(this.registryKey);
        }

        private Map<CustomDataComponent, ?> getCustomComponents() {
            return this.customComponents;
        }

        public Settings component(DataComponentType.NonValued type) {
            this.components.put(type, true);
            return this;
        }

        public <T> Settings component(DataComponentType.Valued<T> type, T value) {
            this.components.put(type, value);
            return this;
        }

        public <T> Settings component(CustomDataComponent<T> type, T value) {
            customComponents.put(type, value);
            return this;
        }

        public Settings attributeModifiers(ItemAttributeModifiers attributeModifiers) {
            return this.component(DataComponentTypes.ATTRIBUTE_MODIFIERS, attributeModifiers);
        }

        Map<DataComponentType, Object> getValidatedComponents() {
            Map<DataComponentType, Object> componentMap = this.components;
            if (componentMap.containsKey(DataComponentTypes.DAMAGE) && (Integer) componentMap.getOrDefault(DataComponentTypes.MAX_STACK_SIZE, 1) > 1) {
                throw new IllegalStateException("Item cannot have both durability and be stackable");
            } else {
                return componentMap;
            }
        }
    }

    //static {
    //    BLOCK_ITEMS = Maps.newHashMap();
    //}
}
