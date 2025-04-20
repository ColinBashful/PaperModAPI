package de.cjdev.papermodapi.api.util;

import io.papermc.paper.math.BlockPosition;
import net.minecraft.core.Direction;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ItemPlacementContext extends ItemUsageContext {
    private final BlockPosition placementPos;
    protected boolean canReplaceExisting;

    public ItemPlacementContext(Player player, EquipmentSlot hand, ItemStack stack, BlockHitResult hitResult) {
        this(player.getWorld(), player, hand, stack, hitResult);
    }

    public ItemPlacementContext(World world, @Nullable Player playerEntity, EquipmentSlot hand, ItemStack itemStack, BlockHitResult blockHitResult) {
        super(playerEntity, hand, blockHitResult, world, itemStack);
        this.canReplaceExisting = true;
        this.placementPos = blockHitResult.getBlockPos().offset(blockHitResult.getSide());
        this.canReplaceExisting = blockHitResult.getBlockPos().toLocation(world).getBlock().isReplaceable();
    }

    public static ItemPlacementContext offset(ItemPlacementContext context, BlockPosition pos, BlockFace side) {
        return new ItemPlacementContext(context.getWorld(), context.getPlayer(), context.getHand(), context.getStack(), new BlockHitResult(context.getBlockPos().offset(side).toVector()));
    }

    public BlockPosition getBlockPos() {
        return this.canReplaceExisting ? super.getBlockPos() : this.placementPos;
    }

    public boolean canPlace() {
        BlockPosition blockPosition = this.getBlockPos();
        return this.canReplaceExisting || this.getWorld().getBlockAt(blockPosition.blockX(),blockPosition.blockY(),blockPosition.blockZ()).isReplaceable();
    }

    public boolean canReplaceExisting() {
        return this.canReplaceExisting;
    }

    public BlockFace getPlayerLookDirection() {
        return Util.blockFaceFromDirection(Direction.getApproximateNearest(((CraftPlayer)this.getPlayer()).getHandle().getLookAngle()));
    }

    public BlockFace getVerticalPlayerLookDirection() {
        return this.getPlayer().getPitch() < 0f ? BlockFace.UP : BlockFace.DOWN;
    }
}
