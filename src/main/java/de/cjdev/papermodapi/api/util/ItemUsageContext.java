package de.cjdev.papermodapi.api.util;

import io.papermc.paper.math.BlockPosition;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

public class ItemUsageContext {
    @Nullable
    private final Player player;
    private final EquipmentSlot hand;
    private final BlockHitResult hit;
    private final World world;
    private final ItemStack stack;

    public ItemUsageContext(@Nullable Player player, EquipmentSlot hand, BlockHitResult hit, World world, ItemStack stack) {
        this.player = player;
        this.hand = hand;
        this.hit = hit;
        this.world = world;
        this.stack = stack;
    }

    protected final BlockHitResult getHitResult(){
        return this.hit;
    }

    public BlockPosition getBlockPos(){
        return this.hit.getBlockPos();
    }

    public BlockFace getSide(){
        return this.hit.getSide();
    }

    public Vector getHitPos(){
        return this.hit.getPos();
    }

    public ItemStack getStack(){
        return this.stack;
    }

    @Nullable
    public Player getPlayer(){
        return this.player;
    }

    public EquipmentSlot getHand(){
        return this.hand;
    }

    public World getWorld(){
        return this.world;
    }

    public BlockFace getHorizontalPlayerFacing(){
        return this.player == null ? BlockFace.NORTH : this.player.getFacing();
    }

    public boolean shouldCancelInteraction(){
        return this.player != null && this.player.isSneaking();
    }

    public float getPlayerYaw() {
        return this.player == null ? 0.0F : this.player.getYaw();
    }
}
