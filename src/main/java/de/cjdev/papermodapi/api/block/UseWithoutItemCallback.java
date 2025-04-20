package de.cjdev.papermodapi.api.block;

import de.cjdev.papermodapi.api.util.ActionResult;
import de.cjdev.papermodapi.api.util.BlockHitResult;
import org.bukkit.World;
import org.bukkit.entity.Player;

import javax.annotation.ParametersAreNonnullByDefault;

@FunctionalInterface
@ParametersAreNonnullByDefault
public interface UseWithoutItemCallback {
    ActionResult onEvent(World world, Player player, BlockHitResult hitResult);
}
