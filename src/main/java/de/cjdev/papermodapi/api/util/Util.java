package de.cjdev.papermodapi.api.util;

import io.papermc.paper.math.BlockPosition;
import net.kyori.adventure.key.Key;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Axis;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.Nullable;

public class Util {
    public static String createTranslationKey(String type, @Nullable Key id){
        return id == null ? type + ".unregistered_sadface" : type + "." + id.namespace() + "." + id.value();
    }

    public static Key getKey(ResourceLocation resourceLocation){
        return Key.key(resourceLocation.getNamespace(), resourceLocation.getPath());
    }

    public static Direction directionFromBlockFace(BlockFace side){
        return switch (side) {
            case SOUTH -> Direction.SOUTH;
            case WEST -> Direction.WEST;
            case EAST -> Direction.EAST;
            case UP -> Direction.UP;
            case DOWN -> Direction.DOWN;
            default -> Direction.NORTH;
        };
    }

    public static Axis nmsAxis(BlockFace side){
        return switch (side) {
            case WEST, EAST -> Axis.X;
            case UP, DOWN -> Axis.Y;
            default -> Axis.Z; // NORTH, SOUTH
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

    public static BlockPos nmsBlockPos(BlockPosition position){
        return new BlockPos(position.blockX(), position.blockY(), position.blockZ());
    }

    public static InteractionHand nmsInteractionHand(EquipmentSlot equipmentSlot){
        return equipmentSlot == EquipmentSlot.HAND ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
    }

    ///
    /// Note: You'll need paperweight for this
    ///
    public static UseOnContext fromItemUsageContext(ItemUsageContext context){
        Player player = ((CraftPlayer)context.getPlayer()).getHandle();
        // Don't wanna throw an error .-.
        // Nobody has ever placed a block with their feet before, have they?
        InteractionHand hand = nmsInteractionHand(context.getHand());
        Level level = ((CraftWorld)context.getWorld()).getHandle();
        Location hitLocation = context.getHitPos();
        Vec3 hitSpot = new Vec3(hitLocation.x(), hitLocation.y(), hitLocation.z());
        Direction direction = directionFromBlockFace(context.getSide());
        BlockPos blockPos = nmsBlockPos(context.getBlockPos());

        // Wth is inside, or rather how is it determined
        net.minecraft.world.phys.BlockHitResult hitResult = new BlockHitResult(hitSpot, direction, blockPos, false);

        return new UseOnContext(player, hand, hitResult);
    }
}
