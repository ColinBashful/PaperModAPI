package de.cjdev.papermodapi.api.item;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@FunctionalInterface
@ParametersAreNonnullByDefault
public interface TooltipCallback {
    void addTooltip(Player player, ItemStack itemStack, List<Component> tooltip);
}
