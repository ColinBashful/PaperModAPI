package de.cjdev.papermodapi.api.util;

import io.papermc.paper.math.BlockPosition;
import net.kyori.adventure.key.Key;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Axis;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.CraftEquipmentSlot;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.util.CraftBlockVector;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

public class Util {
    public static String createTranslationKey(String type, @Nullable Key id){
        return id == null ? type + ".unregistered_sadface" : type + "." + id.namespace() + "." + id.value();
    }

    /**
     * @deprecated Use {@link CraftNamespacedKey#fromMinecraft(ResourceLocation)} instead.
     */
    @Deprecated(
            since = "1.2"
    )
    public static NamespacedKey getKey(ResourceLocation resourceLocation){
        return CraftNamespacedKey.fromMinecraft(resourceLocation);
    }

    /**
     * @deprecated Use {@link CraftBlock#blockFaceToNotch(BlockFace)} instead.
     */
    @Deprecated(
            since = "1.2"
    )
    public static Direction directionFromBlockFace(BlockFace side){
        return CraftBlock.blockFaceToNotch(side);
    }

    public static Direction.Axis nmsAxis(BlockFace side){
        return switch (side) {
            case WEST, EAST -> Direction.Axis.X;
            case UP, DOWN -> Direction.Axis.Y;
            default -> Direction.Axis.Z; // NORTH, SOUTH
        };
    }

    public static BlockFace bukkitBlockFace(Axis axis){
        return switch (axis) {
            case X -> BlockFace.EAST; // (West / East)
            case Y -> BlockFace.UP; // (Up / Down)
            case Z -> BlockFace.NORTH; // (North / South)
        };
    }

    public static BlockFace blockFaceFromDirection(Direction side){
        return switch (side) {
            case WEST -> BlockFace.WEST;
            case EAST -> BlockFace.EAST;
            case NORTH -> BlockFace.NORTH;
            case SOUTH -> BlockFace.SOUTH;
            case UP -> BlockFace.UP;
            case DOWN -> BlockFace.DOWN;
        };
    }

    /**
     * @deprecated Use {@link CraftBlockVector#toBlockPosition(BlockVector)} instead.
     */
    @Deprecated(
            since = "1.2"
    )
    public static BlockPos nmsBlockPos(BlockPosition position){
        return CraftBlockVector.toBlockPosition(position.toVector().toBlockVector());
    }

    /**
     * @deprecated Use {@link org.bukkit.craftbukkit.CraftEquipmentSlot#getNMS(EquipmentSlot)} instead.
     */
    @Deprecated(
            since = "1.2"
    )
    public static net.minecraft.world.entity.EquipmentSlot nmsEquipmentSlot(EquipmentSlot equipmentSlot){
        return CraftEquipmentSlot.getNMS(equipmentSlot);
    }

    /**
     * @deprecated Use {@link org.bukkit.craftbukkit.CraftEquipmentSlot#getHand(EquipmentSlot)} instead.
     */
    @Deprecated(
            since = "1.2"
    )
    public static InteractionHand nmsInteractionHand(EquipmentSlot equipmentSlot){
        return CraftEquipmentSlot.getHand(equipmentSlot);
    }

    ///
    /// @apiNote You'll need paperweight for this
    ///
    public static UseOnContext fromItemUsageContext(ItemUsageContext context){
        Player player = ((CraftPlayer)context.getPlayer()).getHandle();
        // Don't wanna throw an error .-.
        // Nobody has ever placed a block with their feet before, have they?
        InteractionHand hand = CraftEquipmentSlot.getHand(context.getHand());
        Vector hitLocation = context.getHitPos();
        Vec3 hitSpot = new Vec3(hitLocation.getX(), hitLocation.getY(), hitLocation.getZ());
        Direction direction = CraftBlock.blockFaceToNotch(context.getSide());
        BlockPos blockPos = CraftBlockVector.toBlockPosition(context.getBlockPos().toVector().toBlockVector());

        // Wth is inside, or rather how is it determined
        BlockHitResult hitResult = new BlockHitResult(hitSpot, direction, blockPos, false);

        return new UseOnContext(player, hand, hitResult);
    }
}
