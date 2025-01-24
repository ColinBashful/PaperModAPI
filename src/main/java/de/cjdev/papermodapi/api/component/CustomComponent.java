package de.cjdev.papermodapi.api.component;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import org.bukkit.inventory.ItemStack;

public class CustomComponent<T> {
    /*private final Codec<T> CODEC;

    public CustomComponent(Codec<T> CODEC){
        this.CODEC = CODEC;

    }

    public T get(ItemStack stack){
        CustomData customData = net.minecraft.world.item.ItemStack.fromBukkitCopy(stack).get(DataComponents.CUSTOM_DATA);
    }

    @Override
    public <T1> DataResult<Pair<T, T1>> decode(DynamicOps<T1> ops, T1 input) {
        return null;
    }

    @Override
    public <T1> DataResult<T1> encode(T input, DynamicOps<T1> ops, T1 prefix) {
        return null;
    }*/
}
