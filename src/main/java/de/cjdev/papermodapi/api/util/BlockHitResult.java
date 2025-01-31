package de.cjdev.papermodapi.api.util;

import io.papermc.paper.math.BlockPosition;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;

public class BlockHitResult extends HitResult {
    private final BlockFace side;
    private final BlockPosition blockPos;
    private final boolean missed;
    private final boolean insideBlock;
    private final boolean againstWorldBorder;

    public static BlockHitResult createMissed(Location pos, BlockFace side, BlockPosition blockPos){
        return new BlockHitResult(true, pos, side, blockPos, false, false);
    }

    public BlockHitResult(Location pos, BlockFace side, BlockPosition blockPos, boolean insideBlock) {
        this(false, pos, side, blockPos, insideBlock, false);
    }

    public BlockHitResult(Location pos, BlockFace side, BlockPosition blockPos, boolean insideBlock, boolean againstWorldBorder) {
        this(false, pos, side, blockPos, insideBlock, againstWorldBorder);
    }

    private BlockHitResult(boolean missed, Location pos, BlockFace side, BlockPosition blockPos, boolean insideBlock, boolean againstWorldBorder) {
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
        Location hitPos = this.getPos();
        return new net.minecraft.world.phys.BlockHitResult(new Vec3(hitPos.x(), hitPos.y(), hitPos.z()), Util.directionFromBlockFace(this.side), Util.nmsBlockPos(this.blockPos), false);
    }
}
