package de.cjdev.papermodapi.api.item;

import com.google.common.collect.Maps;
import de.cjdev.papermodapi.api.block.CustomBlock;
import de.cjdev.papermodapi.api.component.CustomDataComponent;
import de.cjdev.papermodapi.api.component.CustomDataComponents;
import de.cjdev.papermodapi.api.util.ActionResult;
import de.cjdev.papermodapi.api.util.CustomArmorMaterial;
import de.cjdev.papermodapi.api.util.ItemUsageContext;
import de.cjdev.papermodapi.api.util.Util;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.*;
import io.papermc.paper.inventory.tooltip.TooltipContext;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.keys.SoundEventKeys;
import io.papermc.paper.registry.keys.tags.DamageTypeTagKeys;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.minecraft.world.item.equipment.ArmorType;
import org.bukkit.*;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class CustomItem {
    public static final Map<CustomBlock, CustomItem> BLOCK_ITEMS;
    public static final NamespacedKey BASE_ATTACK_DAMAGE_ID;
    public static final NamespacedKey BASE_ATTACK_SPEED_ID;
    public static final int DEFAULT_MAX_COUNT = 64;
    public static final int MAX_MAX_COUNT = 99;
    public static final int ITEM_BAR_STEPS = 13;
    private final NamespacedKey registryKey;
    private final Material baseMaterial;
    private final boolean dyeable;
    private final Map<DataComponentType, Object> components;
    @Nullable
    private final Consumer<ItemStack> recipeRemainder;
    @Nullable
    private final String translationKey;

    private final ItemStack defaultStack;
    private final ItemStack displayStack;

    public @Nullable NamespacedKey getId() {
        return this.registryKey;
    }

    public static @Nullable NamespacedKey getId(CustomItem item) {
        return item == null ? null : item.registryKey;
    }

    public static @Nullable CustomItem byId(NamespacedKey key) {
        return key == null ? null : CustomItems.getItemByKey(key);
    }

    public CustomItem(Settings settings) {
        this.registryKey = settings.registryKey;
        this.baseMaterial = settings.getBaseMaterial();
        this.dyeable = settings.canDye();
        this.translationKey = settings.getTranslationKey();
        this.components = settings.getValidatedComponents();
        this.recipeRemainder = settings.recipeRemainder;

        ItemStack defaultStack = ItemStack.of(this.getBaseMaterial());
        ItemStack displayStack = defaultStack.clone();

        defaultStack.editMeta(itemMeta -> {
            itemMeta.itemName(getName());
            itemMeta.setItemModel(settings.getModelId());
        });
        displayStack.editMeta(itemMeta -> itemMeta.setItemModel(registryKey));

        this.components.forEach((type, o) -> {
            if (type == DataComponentTypes.CUSTOM_MODEL_DATA) {
                displayStack.setData(DataComponentTypes.CUSTOM_MODEL_DATA, (CustomModelData) this.components.get(DataComponentTypes.CUSTOM_MODEL_DATA));
            }
            if (type == DataComponentTypes.USE_COOLDOWN) {
                UseCooldown original = ((UseCooldown) o);
                Key group = original.cooldownGroup();
                defaultStack.setData(DataComponentTypes.USE_COOLDOWN, UseCooldown.useCooldown(original.seconds()).cooldownGroup(group == null ? registryKey : group).build());
                return;
            }
            if (type instanceof DataComponentType.Valued valued)
                defaultStack.setData(valued, o);
            else if (type instanceof DataComponentType.NonValued nonValued) {
                defaultStack.setData(nonValued);
            }
        });

        Map<CustomDataComponent, ?> customData = settings.getCustomComponents();
        customData.forEach((customDataComponent, value) -> customDataComponent.set(defaultStack, value));
        CustomDataComponents.ITEM_COMPONENT.set(defaultStack, registryKey);

        this.defaultStack = defaultStack;
        this.displayStack = displayStack;
    }

    public final ItemStack getDefaultStack() {
        return this.defaultStack.clone();
    }

    public final boolean isSimilar(@Nullable ItemStack stack) {
        if (stack == null || stack.isEmpty())
            return false;
        NamespacedKey itemId = CustomItems.getKeyByStack(stack);
        return itemId != null && itemId.equals(registryKey);
    }

    /// Simply returns the DefaultStack without the unnecessary components
    public final ItemStack getDisplayStack() {
        return this.displayStack.clone();
    }

    public Material getBaseMaterial() {
        return this.baseMaterial;
    }

    public boolean isDyeable() {
        return this.dyeable;
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
        return registryKey.asString();
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

    public void onConsumed(PlayerItemConsumeEvent event) {
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

    public void appendTooltip(ItemStack stack, List<Component> tooltip, TooltipContext context) {
    }

    public static void applyCooldown(Player player, ItemStack stack, float seconds) {
        UseCooldown useCooldown = stack.getData(DataComponentTypes.USE_COOLDOWN);
        stack.setData(DataComponentTypes.USE_COOLDOWN, UseCooldown.useCooldown(seconds).cooldownGroup(useCooldown == null ? null : useCooldown.cooldownGroup()).build());
        player.setCooldown(stack, (int)(seconds * 20));
    }

    /**
     * @deprecated Use {@link CustomItem#isSimilar(ItemStack, ItemStack)} instead.
     */
    @Deprecated(
            since = "1.2"
    )
    public static boolean sameItem(ItemStack stack, ItemStack otherStack) {
        return isSimilar(stack, otherStack);
    }

    public static boolean isSimilar(ItemStack stack, ItemStack otherStack) {
        if (stack == null || otherStack == null || stack.isEmpty() || otherStack.isEmpty())
            return false;
        NamespacedKey item = CustomDataComponents.ITEM_COMPONENT.get(stack);
        NamespacedKey otherItem = CustomDataComponents.ITEM_COMPONENT.get(otherStack);
        return Objects.equals(item, otherItem);
    }

    public static class Settings {
        private static final DependantName<NamespacedKey, String> BLOCK_PREFIXED_TRANSLATION_ID = id -> Util.createTranslationKey("block", id);
        private static final DependantName<NamespacedKey, String> ITEM_PREFIXED_TRANSLATION_ID = id -> Util.createTranslationKey("item", id);
        private @NotNull Material baseMaterial;
        private boolean dyeable;
        private @Nullable Consumer<ItemStack> recipeRemainder;
        private final Map<DataComponentType, Object> components;
        private final Map<CustomDataComponent, Object> customComponents;
        private NamespacedKey registryKey;
        private DependantName<NamespacedKey, String> translationKey;
        private DependantName<NamespacedKey, NamespacedKey> modelId;
        private @Nullable NamespacedKey repairable;

        public Settings() {
            this.components = new HashMap<>();
            this.customComponents = new HashMap<>();

            this.baseMaterial = Material.PAPER;
            this.dyeable = false;
            this.translationKey = ITEM_PREFIXED_TRANSLATION_ID;
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

        public Settings tool(ToolMaterial toolMaterial, Tag<Material> correctForDrops, float attackDamage, float attackSpeed, float disableBlockingForSeconds) {
            return toolMaterial.applyToolSettings(this, correctForDrops, attackDamage, attackSpeed, disableBlockingForSeconds);
        }

        public Settings pickaxe(ToolMaterial toolMaterial, float attackDamage, float attackSpeed) {
            return toolMaterial.applyToolSettings(this, Tag.MINEABLE_PICKAXE, attackDamage, attackSpeed, 0.0F);
        }

        public Settings axe(ToolMaterial toolMaterial, float attackDamage, float attackSpeed) {
            return toolMaterial.applyToolSettings(this, Tag.MINEABLE_AXE, attackDamage, attackSpeed, 5.0F);
        }

        public Settings hoe(ToolMaterial toolMaterial, float attackDamage, float attackSpeed) {
            return toolMaterial.applyToolSettings(this, Tag.MINEABLE_HOE, attackDamage, attackSpeed, 0.0F);
        }

        public Settings shovel(ToolMaterial toolMaterial, float attackDamage, float attackSpeed) {
            return toolMaterial.applyToolSettings(this, Tag.MINEABLE_SHOVEL, attackDamage, attackSpeed, 0.0F);
        }

        public Settings humanoidArmor(CustomArmorMaterial armorMaterial, EquipmentSlot slot) {
            return this.maxDamage(Util.getArmorType(slot).getDurability(armorMaterial.durability())).attributeModifiers(armorMaterial.createAttributes(slot)).enchantable(armorMaterial.enchantmentValue()).component(DataComponentTypes.EQUIPPABLE, Equippable.equippable(slot).equipSound(armorMaterial.equipSound()).assetId(armorMaterial.modelId()).build()).repairable(armorMaterial.repairIngredient());
        }

        public Settings wolfArmor(CustomArmorMaterial armorMaterial, float attackDamage, float attackSpeed) {
            return this.maxDamage(ArmorType.BODY.getDurability(armorMaterial.durability())).attributeModifiers(armorMaterial.createAttributes(EquipmentSlot.BODY)).enchantable(armorMaterial.enchantmentValue()).repairable(armorMaterial.repairIngredient()).component(DataComponentTypes.EQUIPPABLE, Equippable.equippable(EquipmentSlot.BODY).equipSound(armorMaterial.equipSound()).assetId(armorMaterial.modelId()).allowedEntities(RegistrySet.keySet(RegistryKey.ENTITY_TYPE, TypedKey.create(RegistryKey.ENTITY_TYPE, EntityType.WOLF.getKey()))).canBeSheared(true).shearSound(SoundEventKeys.ITEM_ARMOR_UNEQUIP_WOLF).build()).component(DataComponentTypes.BREAK_SOUND, SoundEventKeys.ITEM_WOLF_ARMOR_BREAK);
        }

        public Settings horseArmor(CustomArmorMaterial armorMaterial, float attackDamage, float attackSpeed) {
            return this.attributeModifiers(armorMaterial.createAttributes(EquipmentSlot.BODY)).enchantable(armorMaterial.enchantmentValue()).repairable(armorMaterial.repairIngredient()).component(DataComponentTypes.EQUIPPABLE, Equippable.equippable(EquipmentSlot.BODY).equipSound(armorMaterial.equipSound()).assetId(armorMaterial.modelId()).allowedEntities(RegistrySet.keySet(RegistryKey.ENTITY_TYPE, TypedKey.create(RegistryKey.ENTITY_TYPE, Tag.ENTITY_TYPES_CAN_WEAR_HORSE_ARMOR.getKey()))).damageOnHurt(false).canBeSheared(true).shearSound(SoundEventKeys.ITEM_HORSE_ARMOR_UNEQUIP).build()).maxCount(1);
        }

        public Settings trimMaterial(TrimMaterial trimMaterial) {
            return this.component(DataComponentTypes.PROVIDES_TRIM_MATERIAL, trimMaterial);
        }

        public final Settings registryKey(NamespacedKey registryKey) {
            this.registryKey = registryKey;
            return this;
        }

        public final Settings baseMaterial(@NotNull Material baseMaterial) {
            this.baseMaterial = baseMaterial;
            return this;
        }

        public final boolean canDye() {
            return this.dyeable;
        }

        private @NotNull Material getBaseMaterial() {
            return this.baseMaterial;
        }

        public Settings translationKey(String translationKey) {
            this.translationKey = DependantName.fixed(translationKey);
            return this;
        }

        public final Settings useBlockTranslationPrefix() {
            this.translationKey = BLOCK_PREFIXED_TRANSLATION_ID;
            return this;
        }

        public final Settings useItemTranslationPrefix() {
            this.translationKey = ITEM_PREFIXED_TRANSLATION_ID;
            return this;
        }

        public Settings modelId(NamespacedKey key){
            this.modelId = customItem -> key;
            return this;
        }

        protected String getTranslationKey() {
            return this.translationKey.get(Objects.requireNonNull(this.registryKey, "Item id not set"));
        }

        public final NamespacedKey getModelId() {
            return this.modelId.get(Objects.requireNonNull(this.registryKey, "Item id not set"));
        }

        private Map<CustomDataComponent, ?> getCustomComponents() {
            return this.customComponents;
        }

        public final Settings component(DataComponentType.NonValued type) {
            this.components.put(type, true);
            return this;
        }

        public final <T> Settings component(DataComponentType.Valued<T> type, T value) {
            this.components.put(type, value);
            return this;
        }

        public final <T> Settings component(CustomDataComponent<T> type, T value) {
            customComponents.put(type, value);
            return this;
        }

        public final Settings attributeModifiers(ItemAttributeModifiers attributeModifiers) {
            return this.component(DataComponentTypes.ATTRIBUTE_MODIFIERS, attributeModifiers);
        }

        final Map<DataComponentType, Object> getValidatedComponents() {
            Map<DataComponentType, Object> componentMap = this.components;
            if (componentMap.containsKey(DataComponentTypes.DAMAGE) && (Integer) componentMap.getOrDefault(DataComponentTypes.MAX_STACK_SIZE, 1) > 1) {
                throw new IllegalStateException("Item cannot have both durability and be stackable");
            } else {
                return componentMap;
            }
        }
    }

    static {
        BLOCK_ITEMS = Maps.newHashMap();
        BASE_ATTACK_DAMAGE_ID = CraftNamespacedKey.fromMinecraft(net.minecraft.world.item.Item.BASE_ATTACK_DAMAGE_ID);
        BASE_ATTACK_SPEED_ID = CraftNamespacedKey.fromMinecraft(net.minecraft.world.item.Item.BASE_ATTACK_SPEED_ID);
    }
}
