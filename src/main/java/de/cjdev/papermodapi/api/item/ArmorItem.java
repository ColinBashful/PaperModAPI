package de.cjdev.papermodapi.api.item;

import de.cjdev.papermodapi.api.util.CustomArmorMaterial;
import org.bukkit.inventory.EquipmentSlot;

public class ArmorItem extends CustomItem {
    public ArmorItem(CustomArmorMaterial material, EquipmentSlot slot, Settings settings) {
        super(material.humanoidProperties(settings, slot));
    }
}
