package de.cjdev.papermodapi.api.util;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public abstract class HitResult {
    protected final Location pos;

    protected HitResult(Location pos){
        this.pos = pos;
    }

    public double squaredDistanceTo(Entity entity){
        double d = this.pos.getX() - entity.getX();
        double e = this.pos.getY() - entity.getY();
        double f = this.pos.getZ() - entity.getZ();
        return d * d + e * e + f * f;
    }

    public abstract Type getType();

    public Location getPos(){
        return this.pos;
    }

    public enum Type{
        MISS,
        BLOCK,
        ENTITY;

        Type(){}
    }
}
