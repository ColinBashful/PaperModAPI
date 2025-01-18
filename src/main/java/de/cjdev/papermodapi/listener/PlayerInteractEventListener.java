package de.cjdev.papermodapi.listener;

import de.cjdev.papermodapi.PaperModAPI;
import de.cjdev.papermodapi.api.item.CustomItem;
import de.cjdev.papermodapi.api.item.CustomItems;
import de.cjdev.papermodapi.api.util.ActionResult;
import de.cjdev.papermodapi.api.util.BlockHitResult;
import de.cjdev.papermodapi.api.util.ItemUsageContext;
import net.kyori.adventure.key.Key;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Bell;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.VoxelShape;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PlayerInteractEventListener implements Listener {
//    private final List<Player> swingingPlayers = new ArrayList<>();
//
//    private static boolean checkCollisionWithEntities(Location location, VoxelShape shape){
//        World world = location.getWorld();
//        Location blockPos = location.toBlockLocation();
//        return shape.getBoundingBoxes().stream()
//                .anyMatch(boundingBox ->
//                        !world.getNearbyEntities(boundingBox.shift(blockPos),
//                                entity -> !(entity instanceof Item)).isEmpty());
//    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() == null)
            return;

        if (event.getItem() == null)
            return;

        if (!event.getItem().getPersistentDataContainer().has(new NamespacedKey(PaperModAPI.getPlugin(), "item"), PersistentDataType.STRING))
            return;

        String customItemKey = event.getItem().getPersistentDataContainer().get(new NamespacedKey(PaperModAPI.getPlugin(), "item"), PersistentDataType.STRING);

        Player player = event.getPlayer();
        EquipmentSlot hand = event.getHand();

//        switch (event.getAction()) {
//            case RIGHT_CLICK_BLOCK -> {
//                if (swingingPlayers.contains(player))
//                    break;
//
//                if (event.getClickedBlock() == null || event.getClickedBlock().isEmpty())
//                    return;
//
//                if (!player.isSneaking()) {
//                    Location blockLocation = event.getClickedBlock().getLocation();
//                    BlockPos NMSblockPos = new BlockPos(blockLocation.getBlockX(), blockLocation.getBlockY(), blockLocation.getBlockZ());
//                    ServerLevel serverLevel = ((CraftWorld) event.getClickedBlock().getWorld()).getHandle();
//                    InteractionHand interactionHand = switch (event.getHand()) {
//                        case HAND -> InteractionHand.MAIN_HAND;
//                        default -> InteractionHand.OFF_HAND;
//                    };
//                    Direction NMSdirection = switch (event.getBlockFace()) {
//                        case NORTH -> Direction.NORTH;
//                        case SOUTH -> Direction.SOUTH;
//                        case WEST -> Direction.WEST;
//                        case EAST -> Direction.EAST;
//                        case UP -> Direction.UP;
//                        default -> Direction.DOWN; // DOWN
//                    };
//                    Location location = event.getInteractionPoint();
//                    Vec3 NMSinteractionPoint = new Vec3(location.getX(), location.getY(), location.getZ());
//                    net.minecraft.world.phys.BlockHitResult blockHitResult = new net.minecraft.world.phys.BlockHitResult(NMSinteractionPoint, NMSdirection, NMSblockPos, false);
//                    InteractionResult interactionResult = serverLevel.getBlockState(NMSblockPos).useItemOn(ItemStack.fromBukkitCopy(event.getItem()), serverLevel, ((CraftPlayer) player).getHandle(), interactionHand, blockHitResult);
//                    //player.sendMessage(String.valueOf(interactionResult.toString()));
//
//                    Material blockMaterial = event.getClickedBlock().getBlockData().getMaterial();
//
//                    if (blockMaterial.isInteractable()) {
//                        MATERIAL_CHECK:
//                        {
//                            switch (blockMaterial) {
//                                case CAMPFIRE, SOUL_CAMPFIRE, COMPOSTER, FLETCHING_TABLE, CAULDRON, JUKEBOX,
//                                     BEE_NEST, BEEHIVE, CANDLE, BLACK_CANDLE, GRAY_CANDLE, LIGHT_GRAY_CANDLE,
//                                     WHITE_CANDLE, RED_CANDLE, ORANGE_CANDLE, YELLOW_CANDLE, LIME_CANDLE, BLUE_CANDLE,
//                                     CYAN_CANDLE, LIGHT_BLUE_CANDLE, MAGENTA_CANDLE, PURPLE_CANDLE, PINK_CANDLE, BROWN_CANDLE -> {
//                                }
//                                case RESPAWN_ANCHOR -> {
//                                    if (((RespawnAnchor) event.getClickedBlock().getBlockData()).getCharges() > 0)
//                                        return;
//                                }
//                                case CHISELED_BOOKSHELF -> {
//                                    if (((Directional) event.getClickedBlock().getBlockData()).getFacing() == event.getBlockFace())
//                                        return;
//                                }
//                                case BELL -> {
//                                    Bell blockData = ((Bell) event.getClickedBlock().getBlockData());
//
//                                    BlockFace side = event.getBlockFace();
//                                    switch (side) {
//                                        case UP, DOWN -> {
//                                            break MATERIAL_CHECK;
//                                        }
//                                    }
//
//                                    if (event.getInteractionPoint().getY() % 1f > 0.8125f)
//                                        break MATERIAL_CHECK;
//
//                                    switch (blockData.getAttachment()) {
//                                        case CEILING -> {
//                                            return;
//                                        }
//                                        case FLOOR -> { // ah i see lol
//                                            if (Util.axisFromBlockFace(side) == Util.axisFromBlockFace(blockData.getFacing()))
//                                                return;
//                                        }
//                                        case SINGLE_WALL, DOUBLE_WALL -> {
//                                            if (Util.axisFromBlockFace(side) != Util.axisFromBlockFace(blockData.getFacing()))
//                                                return;
//                                        }
//                                    }
//                                }
//                                default -> {
//                                    return;
//                                }
//                            }
//                        }
//                    }
//                }
//
//                Optional<CustomBlock> customBlock = CustomBlock.getBlockAtPos(event.getClickedBlock().getLocation());
//                if (customBlock.isPresent()) {
//                    ActionResult result = customBlock.get().onUse(event.getClickedBlock().getState(), event.getClickedBlock().getWorld(), event.getClickedBlock().getLocation().toBlock(), player, new BlockHitResult(event.getInteractionPoint(), event.getBlockFace(), event.getClickedBlock().getLocation().toBlock(), false, false));
//                    if (result.shouldSwingHand())
//                        player.swingHand(hand);
//                    if (result.isAccepted())
//                        return;
//                }
//
//                if (event.getMaterial().isEmpty())
//                    return;
//                Block block;
//                if (event.getClickedBlock().isReplaceable())
//                    block = event.getClickedBlock();
//                else {
//                    block = event.getClickedBlock().getRelative(event.getBlockFace());
//                    if (!block.isEmpty() && !block.isReplaceable())
//                        return;
//                }
//                Location center = block.getLocation().toCenterLocation();
//                BlockPlacementContext context = new BlockPlacementContext(center, center.getWorld(), player, event.getBlockFace());
//
//                boolean placedBlock = true;
//                switch (customItemKey) {
//                    case "create:shaft":
//                        center.getWorld().playSound(center, "minecraft:block.stone.place", SoundCategory.BLOCKS, 1.0f, 0.8f);
//                        ShaftBlock shaft = new ShaftBlock(context);
//                        break;
//                    case "create:crushing_wheel":
//                        center.getWorld().playSound(center, "minecraft:block.stone.place", SoundCategory.BLOCKS, 1.0f, 0.8f);
//                        CrushingWheelBlock crushingWheelBlock = new CrushingWheelBlock(context);
//                        break;
//                    case "create:hand_crank":
//                        center.getWorld().playSound(center, "minecraft:block.wood.place", SoundCategory.BLOCKS, 1.0f, 0.8f);
//                        HandCrankBlock handCrank = new HandCrankBlock(context);
//                        break;
//                    case "create:cogwheel":
//                        center.getWorld().playSound(center, "minecraft:block.wood.place", SoundCategory.BLOCKS, 1.0f, 0.8f);
//                        CogwheelBlock cogwheel = new CogwheelBlock(context);
//                        break;
//                    case "create:large_cogwheel":
//                        center.getWorld().playSound(center, "minecraft:block.wood.place", SoundCategory.BLOCKS, 1.0f, 0.8f);
//                        LargeCogwheelBlock largeCogwheel = new LargeCogwheelBlock(context);
//                        break;
//                    case "create:depot":
//                        center.getWorld().playSound(center, "minecraft:block.stone.place", SoundCategory.BLOCKS, 1.0f, 0.8f);
//                        DepotBlock depot = new DepotBlock(context);
//                        break;
//                    case "create:weighted_ejector":
//                        center.getWorld().playSound(center, "minecraft:block.stone.place", SoundCategory.BLOCKS, 1.0f, 0.8f);
//                        new FullBlock(context, ItemInit.WEIGHTED_EJECTOR, Material.LIGHT_GRAY_STAINED_GLASS, Create.key("weighted_depot"));
//                        break;
//                    case "create:andesite_alloy_block":
//                        center.getWorld().playSound(center, "minecraft:block.stone.place", SoundCategory.BLOCKS, 1.0f, 0.8f);
//                        new FullBlock(context, ItemInit.ANDESITE_ALLOY_BLOCK, Material.STONE, Create.key("andesite_alloy"));
//                        break;
//                    case "create:rose_quartz_tiles":
//                        center.getWorld().playSound(center, "minecraft:block.stone.place", SoundCategory.BLOCKS, 1.0f, 0.8f);
//                        new FullBlock(context, ItemInit.ROSE_QUARTZ_TILES, Material.STONE, Create.key("rose_quarz_tiles"));
//                        break;
//                    case "create:small_rose_quartz_tiles":
//                        center.getWorld().playSound(center, "minecraft:block.stone.place", SoundCategory.BLOCKS, 1.0f, 0.8f);
//                        new FullBlock(context, ItemInit.SMALL_ROSE_QUARTZ_TILES, Material.STONE, Create.key("small_rose_quarz_tiles"));
//                        break;
//                    case "create:basin":
//                        center.getWorld().playSound(center, "minecraft:block.netherite_block.place", SoundCategory.BLOCKS, 1.0f, 0.8f);
//                        new FullBlock(context, ItemInit.BASIN, Material.LIGHT_GRAY_STAINED_GLASS, Create.key("basin"));
//                        break;
//                    case "create:empty_schematic":
//                        center.getWorld().playSound(center, "minecraft:block.wood.place", SoundCategory.BLOCKS, 1.0f, 0.8f);
//                        ButtonTestBlock buttonTest = new ButtonTestBlock(context);
//                        break;
//                    default:
//                        placedBlock = false;
//                        break;
//                }
//                if (placedBlock) {
//                    swingingPlayers.add(player);
//                    player.swingHand(hand);
//                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Create.getPlugin(), () -> swingingPlayers.remove(player), 4);
//                    if (player.getGameMode() != GameMode.CREATIVE)
//                        event.getItem().subtract();
//                    return;
//                }
//            }
//        }

        Key itemKey = Key.key(customItemKey);
        CustomItem customItem = CustomItems.getItemByKey(itemKey);
        if (customItem == null)
            return;
        //event.getPlayer().sendMessage(customItemKey);

        switch (event.getAction()) {
            case RIGHT_CLICK_AIR -> {
                ActionResult actionResult = customItem.use(player.getWorld(), player, hand);
                if (actionResult.shouldSwingHand())
                    player.swingHand(hand);
            }
            case RIGHT_CLICK_BLOCK -> {
                // To Do
                //
                //  new Vector3d(0d, 0d, 0d) ändern in Position der Maus, kp, Raycast
                //
                ActionResult actionResult = customItem.useOnBlock(new ItemUsageContext(player, hand, new BlockHitResult(event.getInteractionPoint(), event.getBlockFace(), event.getClickedBlock().getLocation().toBlock(), false), player.getWorld(), event.getItem()));
                if (actionResult.shouldSwingHand())
                    player.swingHand(hand);
            }
        }
    }
}