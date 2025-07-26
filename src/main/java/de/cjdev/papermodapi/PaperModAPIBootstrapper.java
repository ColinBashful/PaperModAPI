package de.cjdev.papermodapi;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.cjdev.papermodapi.api.PaperModAPIPlugin;
import de.cjdev.papermodapi.api.item.PaperModAPIItem;
import de.cjdev.papermodapi.api.item.SimpleItem;
import de.cjdev.papermodapi.packet.PaperModAPIPacketListener;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.util.SafeAutoClosable;
import io.papermc.paper.util.sanitizer.ItemComponentSanitizer;
import io.papermc.paper.util.sanitizer.ItemObfuscationSession;
import net.minecraft.core.Holder;
import net.minecraft.core.IdMap;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.VarInt;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import org.bukkit.Material;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.jetbrains.annotations.NotNull;

import java.util.ServiceLoader;
import java.util.function.Function;

public class PaperModAPIBootstrapper implements PluginBootstrap {

    public static Item UWU;

    private static final Codec<Holder<Item>> ITEM_CODEC;

    private static StreamCodec<RegistryFriendlyByteBuf, ItemStack> createOptionalStreamCodec(final StreamCodec<RegistryFriendlyByteBuf, DataComponentPatch> codec) {
        return new StreamCodec<>() {
            public ItemStack decode(@NotNull RegistryFriendlyByteBuf buffer) {
                int varInt = buffer.readVarInt();
                if (varInt <= 0) {
                    return ItemStack.EMPTY;
                } else {
                    Holder<Item> holder = Item.STREAM_CODEC.decode(buffer);
                    DataComponentPatch dataComponentPatch = codec.decode(buffer);
                    return PaperModAPIPacketListener.decodeCreative(new ItemStack(holder, varInt, dataComponentPatch));
                }
            }

            public void encode(@NotNull RegistryFriendlyByteBuf buffer, @NotNull ItemStack value) {
                ItemStack finalValue = value.copy();

                if (!finalValue.isEmpty()) {
                    buffer.writeVarInt(ItemComponentSanitizer.sanitizeCount(ItemObfuscationSession.currentSession(), finalValue, finalValue.getCount()));
                    if (value.getItem() instanceof PaperModAPIItem modAPIItem)
                        Item.STREAM_CODEC.encode(buffer, modAPIItem.getClientItem().builtInRegistryHolder());
                    else
                        Item.STREAM_CODEC.encode(buffer, finalValue.getItemHolder());
                    boolean prev = ComponentSerialization.DONT_RENDER_TRANSLATABLES.get();

                    try {
                        SafeAutoClosable ignored = ItemObfuscationSession.withContext((c) -> c.itemStack(finalValue));

                        try {
                            ComponentSerialization.DONT_RENDER_TRANSLATABLES.set(true);
                            finalValue.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY, customData -> customData.update(compoundTag -> {
                                if (value.getItem() instanceof PaperModAPIItem modAPIItem) {
                                    compoundTag.putInt("modapi:id", BuiltInRegistries.ITEM.getId(value.getItem()));
                                    DataComponentMap base = modAPIItem.getClientItem().components(); // client base components
                                    DataComponentPatch patch = new PatchedDataComponentMap(value.getComponents()).asPatch();

                                    DataComponentPatch patched = PatchedDataComponentMap.fromPatch(base, patch).asPatch();
                                    DataComponentPatch.CODEC.encodeStart(NbtOps.INSTANCE, patched);
                                } else {
                                    DataComponentPatch.CODEC.encodeStart(NbtOps.INSTANCE, value.getComponentsPatch()).result().ifPresent(tag -> compoundTag.put("modapi:patch", tag));
                                }
                            }));
                            codec.encode(buffer, finalValue.getComponentsPatch());
                        } catch (Throwable var12) {
                            if (ignored != null) {
                                try {
                                    ignored.close();
                                } catch (Throwable var11) {
                                    var12.addSuppressed(var11);
                                }
                            }

                            throw var12;
                        }

                        if (ignored != null) {
                            ignored.close();
                        }
                    } finally {
                        ComponentSerialization.DONT_RENDER_TRANSLATABLES.set(prev);
                    }
                } else {
                    buffer.writeVarInt(0);
                }

            }
        };
    }

    @Override
    public void bootstrap(BootstrapContext context) {
        UnsafeFieldReplacer.setStaticFinal(ItemStack.class, "OPTIONAL_STREAM_CODEC", createOptionalStreamCodec(DataComponentPatch.STREAM_CODEC));
        UnsafeFieldReplacer.setStaticFinal(ItemStack.class, "OPTIONAL_UNTRUSTED_STREAM_CODEC", createOptionalStreamCodec(DataComponentPatch.DELIMITED_STREAM_CODEC));
        UnsafeFieldReplacer.setStaticFinal(ItemStack.class, "OPTIONAL_LIST_STREAM_CODEC", ItemStack.OPTIONAL_STREAM_CODEC.apply(ByteBufCodecs.collection(NonNullList::createWithCapacity)));
        MapCodec<HoverEvent.ShowItem> showItemMapCodec = ItemStack.MAP_CODEC.xmap(HoverEvent.ShowItem::new, showItem -> Items.DIRT.getDefaultInstance()/*HoverEvent.ShowItem::item*/);
        UnsafeFieldReplacer.setFinal(HoverEvent.Action.SHOW_ITEM, "codec", showItemMapCodec);

        // 1. i dont see a problem 2. how would you do it with packetLib lmao? yea, ok, comment it out ig, lol also, for the block, cant we just use the blockcodec? fr :sob:
        // the block codec says how to codec the4 block, not entities yea, but like in the blockcodec, we can return like the noteblock or barrier block + another packet for item display?
        // ah yes, true, like I said, comment out packetevents for now it already is long commented out, since like 1h ;_; even in the paper-plugin yml? bruh thats what u ment? ._.

        UWU = Items.registerItem("uwu", SimpleItem::new);
    }

    private static <T> StreamCodec<RegistryFriendlyByteBuf, Holder<Item>> itemRegistry(final ResourceKey<? extends Registry<T>> registryKey, final Function<Registry<T>, IdMap<Holder<Item>>> idGetter) {
        return new StreamCodec<>() {
            private IdMap<Holder<Item>> getRegistryOrThrow(RegistryFriendlyByteBuf buffer) {
                return idGetter.apply(buffer.registryAccess().lookupOrThrow(registryKey));
            }

            public Holder<Item> decode(RegistryFriendlyByteBuf buffer) {
                int i = VarInt.read(buffer);
                return this.getRegistryOrThrow(buffer).byIdOrThrow(i);
            }

            public void encode(RegistryFriendlyByteBuf buffer, Holder<Item> value) {
                int idOrThrow;
                if (value.value() instanceof PaperModAPIItem item) {
                    idOrThrow = this.getRegistryOrThrow(buffer).getIdOrThrow(BuiltInRegistries.ITEM.wrapAsHolder(item.getClientItem()));
                } else
                    idOrThrow = this.getRegistryOrThrow(buffer).getIdOrThrow(value);
                VarInt.write(buffer, idOrThrow);
            }
        };
    }

    static {
        ITEM_CODEC = BuiltInRegistries.ITEM.holderByNameCodec().validate((holder) -> holder.is(Items.AIR.builtInRegistryHolder()) ?
                DataResult.error(() -> "Item must not be minecraft:air")
                : DataResult.success(holder.value()
                instanceof PaperModAPIItem modAPIItem ? BuiltInRegistries.ITEM.wrapAsHolder(modAPIItem.getClientItem())
                : holder));
    }
}

