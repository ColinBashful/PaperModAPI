package de.cjdev.papermodapi.listener;

import de.cjdev.papermodapi.PaperModAPI;
import de.cjdev.papermodapi.api.item.CustomItem;
import de.cjdev.papermodapi.api.item.CustomItems;
import de.cjdev.papermodapi.api.util.ActionResult;
import de.cjdev.papermodapi.api.util.BlockHitResult;
import de.cjdev.papermodapi.api.util.ItemUsageContext;
import de.cjdev.papermodapi.api.util.Util;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.UseCooldown;
import io.papermc.paper.event.player.PlayerStopUsingItemEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
//        Vector oldVector = player.getVelocity();
//
//        float yaw = player.getYaw();
//        float pitch = player.getPitch();
//
//        double yawRad = Math.toRadians(yaw);
//        double pitchRad = Math.toRadians(pitch);
//        Vector playerRotation = new Vector(-Math.sin(yawRad) * Math.cos(pitchRad), -Math.sin(pitchRad), Math.cos(yawRad) * Math.cos(pitchRad));
//        player.setVelocity(new Vector(oldVector.getX() + playerRotation.getX(), oldVector.getY() + playerRotation.getY(), oldVector.getZ() + playerRotation.getZ()));

        if (!player.isSneaking() && event.getClickedBlock() != null && event.getInteractionPoint() != null) {
            net.minecraft.world.entity.player.Player nmsPlayer = ((CraftPlayer)event.getPlayer()).getHandle();
            Level nmsLevel = nmsPlayer.level();
            BlockState blockState = nmsLevel.getBlockState(Util.nmsBlockPos(event.getClickedBlock().getLocation().toBlock()));
            InteractionHand nmsHand = Util.nmsInteractionHand(event.getHand());
            net.minecraft.world.phys.BlockHitResult nmsBlockHitResult = new BlockHitResult(event.getInteractionPoint().toVector(), event.getBlockFace(), event.getClickedBlock().getLocation().toBlock(), false).asNMSCopy();
            if (event.getItem() != null) {
                net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.unwrap(event.getItem());
                if(blockState.useItemOn(nmsStack, nmsLevel, nmsPlayer, nmsHand, nmsBlockHitResult).consumesAction()){
                    if(event.getHand() != null)
                        event.getPlayer().swingHand(event.getHand());
                    event.setCancelled(true);
                    return;
                }
            }
            if (blockState.useWithoutItem(((CraftWorld) player.getWorld()).getHandle(), ((CraftPlayer) player).getHandle(), nmsBlockHitResult).consumesAction()) {
                if(event.getHand() != null)
                    event.getPlayer().swingHand(event.getHand());
                return;
            }
        }

        if (event.getHand() == null || event.getItem() == null)
            return;

        ItemStack stack = event.getItem();
        CustomItem customItem = CustomItems.getItemByStack(stack);
        if (customItem == null)
            return;

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
//                    InteractionHand interactionHand = Util.nmsInteractionHand(event.getHand());
//                    Direction NMSdirection = Util.directionFromBlockFace(event.getBlockFace());
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
        if (player.hasCooldown(stack))
            return;

        ActionResult actionResult = null;
        switch (event.getAction()) {
            case RIGHT_CLICK_AIR -> {
                actionResult = customItem.use(player.getWorld(), player, hand);
                if (actionResult.shouldSwingHand())
                    player.swingHand(hand);
            }
            case RIGHT_CLICK_BLOCK -> {
                actionResult = customItem.useOnBlock(new ItemUsageContext(player, hand, new BlockHitResult(event.getInteractionPoint().toVector(), event.getBlockFace(), event.getClickedBlock().getLocation().toBlock(), false, false), player.getWorld(), event.getItem()));
                if (actionResult.shouldSwingHand())
                    player.swingHand(hand);
            }
        }

        if (actionResult != null && actionResult.isAccepted()) {
            UseCooldown useCooldown = stack.getData(DataComponentTypes.USE_COOLDOWN);
            if (useCooldown == null)
                return;
            player.setCooldown(stack, (int) (useCooldown.seconds() * 20));
        }
    }

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        CustomItem customItem = CustomItems.getItemByStack(event.getItem());
        if (customItem == null)
            return;

        customItem.onConsumed(event);
    }

    @EventHandler
    public void onPlayerStopUsingItem(PlayerStopUsingItemEvent event) {
        CustomItem customItem = CustomItems.getItemByStack(event.getItem());
        if (customItem == null)
            return;

        customItem.onStoppedUsing(event.getItem(), event.getPlayer().getWorld(), event.getPlayer(), event.getTicksHeldFor());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event){
        ItemStack stack = event.getPlayer().getInventory().getItem(event.getHand());
        CustomItem customItem = CustomItems.getItemByStack(stack);
        if (customItem == null)
            return;
        if(customItem.useOnEntity(stack, event.getPlayer(), event.getRightClicked(), event.getHand()).shouldSwingHand())
            event.getPlayer().swingHand(event.getHand());
    }
}