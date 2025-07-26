package de.cjdev.papermodapi.api.util;

import de.cjdev.papermodapi.api.item.CustomItem;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Equippable;
import io.papermc.paper.datacomponent.item.ItemAttributeModifiers;
import io.papermc.paper.registry.set.RegistryKeySet;
import net.kyori.adventure.key.Key;
import net.minecraft.world.item.equipment.ArmorType;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public record CustomArmorMaterial(
        int durability,
        Map<EquipmentSlot, Integer> defense,
        int enchantmentValue,
        Key equipSound,
        float toughness,
        float knockbackResistance,
        RegistryKeySet<@NotNull ItemType> repairIngredient,
        Key modelId
) {
    public CustomItem.Settings humanoidProperties(CustomItem.Settings settings, EquipmentSlot equipmentType, boolean overrideModel) {
        Equippable.Builder equippable = Equippable.equippable(equipmentType).equipSound(this.equipSound);
        if(overrideModel)
            equippable.assetId(this.modelId);

        return settings.maxDamage(Util.getArmorType(equipmentType).getDurability(this.durability))
                .attributeModifiers(this.createAttributes(equipmentType))
                .enchantable(this.enchantmentValue)
                .component(DataComponentTypes.EQUIPPABLE, equippable.build())
                .repairable(this.repairIngredient);
    }

    public CustomItem.Settings humanoidProperties(CustomItem.Settings settings, EquipmentSlot equipmentType) {
        return this.humanoidProperties(settings, equipmentType, true);
    }

    public CustomItem.Settings animalProperties(CustomItem.Settings settings, RegistryKeySet<@NotNull EntityType> allowedEntities) {
        return settings.maxDamage(ArmorType.BODY.getDurability(this.durability))
                .attributeModifiers(this.createAttributes(EquipmentSlot.BODY))
                .repairable(this.repairIngredient)
                .component(
                        DataComponentTypes.EQUIPPABLE,
                        Equippable.equippable(EquipmentSlot.BODY).equipSound(this.equipSound).assetId(this.modelId).allowedEntities(allowedEntities).build()
                );
    }

    public CustomItem.Settings animalProperties(
            CustomItem.Settings settings, Key equipSound, boolean damageOnHurt, RegistryKeySet<@NotNull EntityType> allowedEntities
    ) {
        if (damageOnHurt) {
            settings = settings.maxDamage(ArmorType.BODY.getDurability(this.durability)).repairable(this.repairIngredient);
        }

        return settings.attributeModifiers(this.createAttributes(EquipmentSlot.BODY))
                .component(
                        DataComponentTypes.EQUIPPABLE,
                        Equippable.equippable(EquipmentSlot.BODY)
                                .equipSound(equipSound)
                                .assetId(this.modelId)
                                .allowedEntities(allowedEntities)
                                .damageOnHurt(damageOnHurt)
                                .build()
                );
    }

    public ItemAttributeModifiers createAttributes(EquipmentSlot slot) {
        ArmorType armorType = Util.getArmorType(slot);

        int i = this.defense.getOrDefault(armorType, 0);
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.itemAttributes();
        NamespacedKey key = NamespacedKey.minecraft("armor." + armorType.getName());
        builder.addModifier(Attribute.ARMOR, new AttributeModifier(key, i, AttributeModifier.Operation.ADD_NUMBER), slot.getGroup());
        builder.addModifier(
                Attribute.ARMOR_TOUGHNESS,
                new AttributeModifier(key, this.toughness, AttributeModifier.Operation.ADD_NUMBER),
                slot.getGroup()
        );
        if (this.knockbackResistance > 0.0F) {
            builder.addModifier(
                    Attribute.KNOCKBACK_RESISTANCE,
                    new AttributeModifier(key, this.knockbackResistance, AttributeModifier.Operation.ADD_NUMBER),
                    slot.getGroup()
            );
        }

        return builder.build();
    }
}
