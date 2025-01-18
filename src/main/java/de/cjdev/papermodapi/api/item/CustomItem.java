package de.cjdev.papermodapi.api.item;

import de.cjdev.papermodapi.PaperModAPI;
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
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class CustomItem {
//    public static final Map<CustomBlock, CustomItem> BLOCK_ITEMS = Maps.newHashMap();
    public static final int DEFAULT_MAX_COUNT = 64;
    public static final int MAX_MAX_COUNT = 99;
    public static final int ITEM_BAR_STEPS = 13;
    private final Map<DataComponentType.Valued, Object> components;
    @Nullable
    private final ItemStack recipeRemainder;
    @Nullable
    private final String translationKey;

    public static @Nullable Key getId(CustomItem item){
        return item == null ? null : CustomItems.getKeyByItem(item);
    }

    public static @Nullable CustomItem byId(Key key){
        return key == null ? null : CustomItems.getItemByKey(key);
    }

    public CustomItem(Settings settings) {
        this.translationKey = settings.getTranslationKey();
        this.components = settings.getValidatedComponents(Component.translatable(this.translationKey), settings.getModelId());
        this.recipeRemainder = settings.recipeRemainder;
    }

    public final ItemStack getDefaultStack() {
        ItemStack stack = ItemStack.of(Material.CLOCK);
        Key id = getId(this);
        PaperModAPI.LOGGER.info(id.asString());
        stack.setData(DataComponentTypes.ITEM_NAME, getName());
        stack.setData(DataComponentTypes.ITEM_MODEL, id);
        this.components.forEach((valued, o) -> stack.setData(valued, o));
        stack.editMeta(itemMeta -> itemMeta.getPersistentDataContainer().set(NamespacedKey.fromString("item", PaperModAPI.getPlugin()), PersistentDataType.STRING, id.asString()));
        return stack;
    }

    public final ItemStack getDisplayStack() {
        ItemStack stack = ItemStack.of(Material.CLOCK);
        stack.getDataTypes().forEach(stack::unsetData);
        stack.setData(DataComponentTypes.ITEM_MODEL, getId(this));
        if(this.components.containsKey(DataComponentTypes.CUSTOM_MODEL_DATA))
            stack.setData(DataComponentTypes.CUSTOM_MODEL_DATA, (CustomModelData) this.components.get(DataComponentTypes.CUSTOM_MODEL_DATA));
        return stack;
    }

    public final Component getName() {
        return (Component) this.components.getOrDefault(DataComponentTypes.ITEM_NAME, Component.empty());
    }

    public Component getName(ItemStack stack) {
        return stack.getDataOrDefault(DataComponentTypes.ITEM_NAME, Component.empty());
    }

    public Key getBreakSound(){
        return Key.key("minecraft", "entity.item.break");
    }

    public String toString() {
        return CustomItems.getKeyByItem(this).asString();
    }

    public String getTranslationKey() {
        return this.translationKey;
    }

    public ActionResult useOnBlock(ItemUsageContext context){
        return ActionResult.PASS;
    }

    public ActionResult use(World world, Player player, EquipmentSlot hand){
        return ActionResult.PASS;
    }

    public static class Settings {
        private static final Function<Key, String> BLOCK_PREFIXED_TRANSLATION_KEY = id -> Util.createTranslationKey("block", id);
        private static final Function<Key, String> ITEM_PREFIXED_TRANSLATION_KEY = id -> Util.createTranslationKey("item", id);
        ItemStack recipeRemainder;
        private final Map<DataComponentType.Valued, Object> components = new HashMap<>();
        private Key registryKey;
        private Function<Key, String> translationKey;
        private Function<CustomItem, Key> modelId;

        public Settings(){
            this.translationKey = ITEM_PREFIXED_TRANSLATION_KEY;
            this.modelId = CustomItem::getId;
        }

        public Settings food(FoodProperties food){
            this.component(DataComponentTypes.FOOD, food);
            this.component(DataComponentTypes.CONSUMABLE, Consumable.consumable().build());
            return this;
        }

        public Settings useRemainder(ItemStack convertInto){
            return this.component(DataComponentTypes.USE_REMAINDER, UseRemainder.useRemainder(convertInto));
        }

        public Settings useCooldown(float seconds) {
            return this.component(DataComponentTypes.USE_COOLDOWN, UseCooldown.useCooldown(seconds));
        }

        public Settings maxCount(int maxCount){
            return this.component(DataComponentTypes.MAX_STACK_SIZE, maxCount);
        }

        public Settings maxDamage(int maxDamage){
            this.component(DataComponentTypes.MAX_DAMAGE, maxDamage);
            this.component(DataComponentTypes.MAX_STACK_SIZE, 1);
            this.component(DataComponentTypes.DAMAGE, 0);
            return this;
        }

        public Settings recipeRemainder(ItemStack recipeRemainder){
            this.recipeRemainder = recipeRemainder;
            return this;
        }

        public Settings rarity(ItemRarity rarity){
            return this.component(DataComponentTypes.RARITY, rarity);
        }

        public Settings fireproof() {
            return this.component(DataComponentTypes.DAMAGE_RESISTANT, DamageResistant.damageResistant(DamageTypeTagKeys.IS_FIRE));
        }

        public Settings jukeboxPlayable(JukeboxSong songKey){
            return this.component(DataComponentTypes.JUKEBOX_PLAYABLE, JukeboxPlayable.jukeboxPlayable(songKey).build());
        }

        public Settings enchantable(int enchantability){
            return this.component(DataComponentTypes.ENCHANTABLE, Enchantable.enchantable(enchantability));
        }

        public Settings repairable(Material repairIngredient){
            return this.component(DataComponentTypes.REPAIRABLE, Repairable.repairable(RegistrySet.keySet(RegistryKey.ITEM, TypedKey.create(RegistryKey.ITEM, repairIngredient.getKey()))));
        }

        public Settings repairable(RegistryKeySet<ItemType> repairIngredientsTag){
            return this.component(DataComponentTypes.REPAIRABLE, Repairable.repairable(repairIngredientsTag));
        }

        public Settings equippable(EquipmentSlot slot){
            return this.component(DataComponentTypes.EQUIPPABLE, Equippable.equippable(slot).build());
        }

        public Settings equippableUnswappable(EquipmentSlot slot){
            return this.component(DataComponentTypes.EQUIPPABLE, Equippable.equippable(slot).swappable(false).build());
        }

        public Settings registryKey(Key registryKey){
            this.registryKey = registryKey;
            return this;
        }

        public Settings translationKey(String translationKey) {
            this.translationKey = customItem -> translationKey;
            return this;
        }

        public String getTranslationKey() {
            return this.translationKey.apply(this.registryKey);
        }

        public Key getModelId() {
            return this.modelId.apply(CustomItems.getItemByKey(registryKey));
        }

        public <T> Settings component(DataComponentType.Valued type, T value){
            this.components.put(type, value);
            return this;
        }

        public Settings attributeModifiers(ItemAttributeModifiers attributeModifiers) {
            return this.component(DataComponentTypes.ATTRIBUTE_MODIFIERS, attributeModifiers);
        }

        Map<DataComponentType.Valued, Object> getValidatedComponents(Component name, Key modelId) {
            Map<DataComponentType.Valued, Object> componentMap = this.components;
            if(name != null)
                componentMap.put(DataComponentTypes.ITEM_NAME, name);
            if(modelId != null)
                componentMap.put(DataComponentTypes.ITEM_MODEL, modelId);
            if (componentMap.containsKey(DataComponentTypes.DAMAGE) && (Integer) componentMap.getOrDefault(DataComponentTypes.MAX_STACK_SIZE, 1) > 1) {
                throw new IllegalStateException("Item cannot have both durability and be stackable");
            } else {
                return componentMap;
            }
        }
    }

    static {
        //BLOCK_ITEMS = Maps.newHashMap();
    }
}
