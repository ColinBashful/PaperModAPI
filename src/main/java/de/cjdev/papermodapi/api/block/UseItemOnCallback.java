package de.cjdev.papermodapi.api.block;

import de.cjdev.papermodapi.api.util.ActionResult;
import de.cjdev.papermodapi.api.util.BlockHitResult;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

@FunctionalInterface
public interface UseItemOnCallback {
    ActionResult onEvent(ItemStack stack, World world, Player player, EquipmentSlot hand, BlockHitResult hitResult);
}
