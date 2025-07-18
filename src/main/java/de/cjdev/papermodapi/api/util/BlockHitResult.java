package de.cjdev.papermodapi.api.util;

import io.papermc.paper.math.BlockPosition;
import net.minecraft.world.phys.Vec3;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.util.CraftBlockVector;
import org.bukkit.util.Vector;

public class BlockHitResult extends HitResult {
    private final BlockFace side;
    private final BlockPosition blockPos;
    private final boolean missed;
    private final boolean insideBlock;
    private final boolean againstWorldBorder;

    public static BlockHitResult createMissed(Vector pos, BlockFace side, BlockPosition blockPos){
        return new BlockHitResult(true, pos, side, blockPos, false, false);
    }

    public BlockHitResult(Vector pos, BlockFace side, BlockPosition blockPos, boolean insideBlock) {
        this(false, pos, side, blockPos, insideBlock, false);
    }

    public BlockHitResult(Vector pos, BlockFace side, BlockPosition blockPos, boolean insideBlock, boolean againstWorldBorder) {
        this(false, pos, side, blockPos, insideBlock, againstWorldBorder);
    }

    private BlockHitResult(boolean missed, Vector pos, BlockFace side, BlockPosition blockPos, boolean insideBlock, boolean againstWorldBorder) {
        super(pos);
        this.missed = missed;
        this.side = side;
        this.blockPos = blockPos;
        this.insideBlock = insideBlock;
        this.againstWorldBorder = againstWorldBorder;
    }

    public BlockHitResult withSide(BlockFace side) {
        return new BlockHitResult(this.missed, this.pos, side, this.blockPos, this.insideBlock, this.againstWorldBorder);
    }

    public BlockHitResult withBlockPos(BlockPosition blockPos) {
        return new BlockHitResult(this.missed, this.pos, this.side, blockPos, this.insideBlock, this.againstWorldBorder);
    }

    public BlockHitResult againstWorldBorder() {
        return new BlockHitResult(this.missed, this.pos, this.side, this.blockPos, this.insideBlock, true);
    }

    public BlockPosition getBlockPos() {
        return this.blockPos;
    }

    public BlockFace getSide() {
        return this.side;
    }

    public HitResult.Type getType() {
        return this.missed ? Type.MISS : Type.BLOCK;
    }

    public boolean isInsideBlock() {
        return this.insideBlock;
    }

    public boolean isAgainstWorldBorder() {
        return this.againstWorldBorder;
    }

    public net.minecraft.world.phys.BlockHitResult asNMSCopy(){
        Vector hitPos = this.getPos();
        return new net.minecraft.world.phys.BlockHitResult(new Vec3(hitPos.getX(), hitPos.getY(), hitPos.getZ()), CraftBlock.blockFaceToNotch(this.side), CraftBlockVector.toBlockPosition(this.blockPos.toVector().toBlockVector()), false);
    }
}
