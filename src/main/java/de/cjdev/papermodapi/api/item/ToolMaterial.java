package de.cjdev.papermodapi.api.item;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemAttributeModifiers;
import io.papermc.paper.datacomponent.item.Tool;
import io.papermc.paper.datacomponent.item.Weapon;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
import net.kyori.adventure.util.TriState;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.BlockType;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemType;

import java.util.List;

public record ToolMaterial(RegistryKeySet<BlockType> incorrectBlocksForDrops, int durability, float speed, float attackDamageBonus, int enchantmentValue, RegistryKeySet<ItemType> repairItems) {
    public static final ToolMaterial WOOD = new ToolMaterial(Tag.INCORRECT_FOR_WOODEN_TOOL, 59, 2.0F, 0.0F, 15, Tag.ITEMS_WOODEN_TOOL_MATERIALS);
    public static final ToolMaterial STONE = new ToolMaterial(Tag.INCORRECT_FOR_STONE_TOOL, 131, 4.0F, 1.0F, 5, Tag.ITEMS_STONE_TOOL_MATERIALS);
    public static final ToolMaterial IRON = new ToolMaterial(Tag.INCORRECT_FOR_IRON_TOOL, 250, 6.0F, 2.0F, 14, Tag.ITEMS_IRON_TOOL_MATERIALS);
    public static final ToolMaterial DIAMOND = new ToolMaterial(Tag.INCORRECT_FOR_DIAMOND_TOOL, 1561, 8.0F, 3.0F, 14, Tag.ITEMS_DIAMOND_TOOL_MATERIALS);
    public static final ToolMaterial GOLD = new ToolMaterial(Tag.INCORRECT_FOR_GOLD_TOOL, 32, 12.0F, 0.0F, 22, Tag.ITEMS_GOLD_TOOL_MATERIALS);
    public static final ToolMaterial NETHERITE = new ToolMaterial(Tag.INCORRECT_FOR_NETHERITE_TOOL, 2031, 9.0F, 4.0F, 15, Tag.ITEMS_NETHERITE_TOOL_MATERIALS);

    public ToolMaterial(Tag<Material> incorrectBlocksForDrops, int durability, float speed, float attackDamageBonus, int enchantmentValue, Tag<Material> repairItems) {
        this(RegistrySet.keySet(RegistryKey.BLOCK, TypedKey.create(RegistryKey.BLOCK, incorrectBlocksForDrops.getKey())), durability, speed, attackDamageBonus, enchantmentValue, RegistrySet.keySet(RegistryKey.ITEM, TypedKey.create(RegistryKey.ITEM, repairItems.getKey())));
    }

    public CustomItem.Settings applyCommonSettings(CustomItem.Settings settings) {
        return settings.maxDamage(this.durability).repairable(this.repairItems).enchantable(this.enchantmentValue);
    }

    public CustomItem.Settings applyToolSettings(CustomItem.Settings settings, RegistryKeySet<BlockType> correctForDrops, float attackDamage, float attackSpeed, float disableBlockingForSeconds) {
        return this.applyCommonSettings(settings).component(DataComponentTypes.TOOL, Tool.tool().addRule(Tool.rule(this.incorrectBlocksForDrops, null, TriState.FALSE)).addRule(Tool.rule(correctForDrops, this.speed, TriState.TRUE)).defaultMiningSpeed(1.0F).damagePerBlock(1).canDestroyBlocksInCreative(true).build()).attributeModifiers(this.createToolAttributes(attackDamage, attackSpeed)).component(DataComponentTypes.WEAPON, Weapon.weapon().itemDamagePerAttack(2).disableBlockingForSeconds(disableBlockingForSeconds).build());
    }

    public CustomItem.Settings applyToolSettings(CustomItem.Settings settings, Tag<Material> correctForDrops, float attackDamage, float attackSpeed, float disableBlockingForSeconds) {
        return this.applyToolSettings(settings, RegistrySet.keySet(RegistryKey.BLOCK, TypedKey.create(RegistryKey.BLOCK, correctForDrops.getKey())), attackDamage, attackSpeed, disableBlockingForSeconds);
    }

    public ItemAttributeModifiers createToolAttributes(float attackDamage, float attackSpeed) {
        return ItemAttributeModifiers.itemAttributes().addModifier(Attribute.ATTACK_DAMAGE, new AttributeModifier(CustomItem.BASE_ATTACK_DAMAGE_ID, attackDamage + this.attackDamageBonus, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND)).addModifier(Attribute.ATTACK_SPEED, new AttributeModifier(CustomItem.BASE_ATTACK_SPEED_ID, attackSpeed, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND)).build();
    }

    public CustomItem.Settings applySwordSettings(CustomItem.Settings settings, float attackDamage, float attackSpeed) {
        return this.applyCommonSettings(settings).component(DataComponentTypes.TOOL, Tool.tool().addRule(Tool.rule(RegistrySet.keySet(RegistryKey.BLOCK, List.of(TypedKey.create(RegistryKey.BLOCK, Material.COBWEB.getKey()))), 15.0F, TriState.TRUE)).addRule(Tool.rule(RegistrySet.keySet(RegistryKey.BLOCK, TypedKey.create(RegistryKey.BLOCK, Tag.SWORD_INSTANTLY_MINES.getKey())), Float.MAX_VALUE, TriState.NOT_SET)).addRule(Tool.rule(RegistrySet.keySet(RegistryKey.BLOCK, TypedKey.create(RegistryKey.BLOCK, Tag.SWORD_INSTANTLY_MINES.getKey())), 1.5F, TriState.NOT_SET)).defaultMiningSpeed(1.0F).damagePerBlock(2).canDestroyBlocksInCreative(false).build()).attributeModifiers(this.createSwordAttributes(attackDamage, attackSpeed)).component(DataComponentTypes.WEAPON, Weapon.weapon().itemDamagePerAttack(1).build());
    }

    public ItemAttributeModifiers createSwordAttributes(float attackDamage, float attackSpeed) {
        return ItemAttributeModifiers.itemAttributes().addModifier(Attribute.ATTACK_DAMAGE, new AttributeModifier(CustomItem.BASE_ATTACK_DAMAGE_ID, attackDamage + this.attackDamageBonus, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND)).addModifier(Attribute.ATTACK_SPEED, new AttributeModifier(CustomItem.BASE_ATTACK_SPEED_ID, attackSpeed, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND)).build();
    }

}
