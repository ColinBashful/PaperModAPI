package de.cjdev.papermodapi.packet;

import com.github.retrooper.packetevents.event.*;
import com.github.retrooper.packetevents.protocol.component.ComponentTypes;
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemLore;
import com.github.retrooper.packetevents.protocol.nbt.NBTByteArray;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.User;
import de.cjdev.papermodapi.PaperModAPI;
import de.cjdev.papermodapi.api.item.CustomItem;
import de.cjdev.papermodapi.api.item.CustomItems;
import de.cjdev.papermodapi.api.item.TooltipCallback;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import io.papermc.paper.inventory.tooltip.TooltipContext;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.CustomData;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PaperModAPIPacketListener implements PacketListener {

    private static final Map<UUID, Integer> lastUsed = new ConcurrentHashMap<>(Bukkit.getMaxPlayers());
    private static final Map<UUID, Integer> startedUsing = new ConcurrentHashMap<>(Bukkit.getMaxPlayers());

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        final User user = event.getUser();
        final Player player = event.getPlayer();
        if (player == null)
            return;
        switch (event.getPacketType()) {
            case PacketType.Play.Client.CREATIVE_INVENTORY_ACTION -> {
                /*final var packet = new de.cjdev.papermodapi.packet.wrapper.play.client.WrapperPlayClientCreativeInventoryAction(event);
                final var stack = packet.getItemStack();
                if (stack.isEmpty()) return;
                var tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).getUnsafe();
                Integer id = tag.getInt("modapi:id").orElse(null);
                if (id == null) return;
                Holder<Item> item = BuiltInRegistries.ITEM.get(id).orElse(null);
                if (item == null) return;
                var itemStack = new net.minecraft.world.item.ItemStack(item, stack.getCount());
                tag.getCompound("modapi:patch").flatMap(components ->
                        DataComponentPatch.CODEC.parse(NbtOps.INSTANCE, components).result()).ifPresent(itemStack::applyComponents);
                packet.setItemStack(itemStack);*/
            }
            /*case PacketType.Play.Client.USE_ITEM -> {
                final WrapperPlayClientUseItem packet = new WrapperPlayClientUseItem(event);

                EquipmentSlot hand = EquipmentSlot.OFF_HAND;
                if (packet.getHand() == InteractionHand.MAIN_HAND)
                    hand = EquipmentSlot.HAND;

                final int currentTick = Bukkit.getCurrentTick();

                int previousLastUsed = Objects.requireNonNullElse(lastUsed.put(user.getUUID(), currentTick), -1);
                final boolean newStart = previousLastUsed + 4 != currentTick;
                startedUsing.compute(user.getUUID(), (uuid, integer) -> newStart ? currentTick : integer == null ? -1 : integer);
                if (newStart) {
                    previousLastUsed = currentTick;
                }

                final ItemStack usedItem = player.getInventory().getItem(hand);
                final CustomItem customItem = CustomItems.getItemByStack(usedItem);
                if (customItem == null)
                    return;

                final int finalPreviousLastUsed = previousLastUsed;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (currentTick - startedUsing.get(user.getUUID()) == 0) {
                            customItem.usageTick(player.getWorld(), player, usedItem, 0);
                        } else {
                            for (int t = finalPreviousLastUsed + 1; t <= currentTick; ++t) {
                                customItem.usageTick(player.getWorld(), player, usedItem, t - startedUsing.get(user.getUUID()));
                            }
                        }
                    }
                }.runTask(PaperModAPI.getPlugin());
            }*/
            default -> {
            }
        }
    }

    public static @NotNull net.minecraft.world.item.ItemStack decodeCreative(net.minecraft.world.item.ItemStack stack) {
        if (stack.isEmpty()) return stack;
        var tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).getUnsafe();
        net.minecraft.world.item.ItemStack itemStack = null;
        Integer id = tag.getInt("modapi:id").orElse(null);
        if (id != null) {
            Holder<Item> item = BuiltInRegistries.ITEM.get(id).orElse(null);
            if (item == null) return stack;
            itemStack = new net.minecraft.world.item.ItemStack(item, stack.getCount());
        }
        DataComponentPatch patch = tag.getCompound("modapi:patch")
                .flatMap(components -> DataComponentPatch.CODEC.parse(NbtOps.INSTANCE, components).result()).orElse(null);

        if (itemStack != null) {
            if (patch == null) return itemStack;
            net.minecraft.world.item.ItemStack clone = itemStack.getItem().getDefaultInstance();
            clone.setCount(stack.getCount());
            clone.applyComponents(patch);
            return clone;
        } else {
            if (patch == null) return stack;
            net.minecraft.world.item.ItemStack clone = stack.getItem().getDefaultInstance();
            clone.setCount(stack.getCount());
            clone.applyComponents(patch);
            return clone;
        }
    }

    private static void appendTooltip(com.github.retrooper.packetevents.protocol.item.ItemStack packetItem, Player player) {
        if (packetItem == null || packetItem.isEmpty())
            return;
        final ItemStack stack = SpigotConversionUtil.toBukkitItemStack(packetItem);
        final CustomItem item = CustomItems.getItemByStack(stack);

        TooltipContext tooltipContext = TooltipContext.create(false, player.getGameMode() == GameMode.CREATIVE);
        ItemLore tooltip = packetItem.getComponentOr(ComponentTypes.LORE, new ItemLore(List.of()));
        List<Component> lines = new ArrayList<>(tooltip.getLines());
        List<Component> tooltipLines = new ArrayList<>();
        if (item != null)
            item.appendTooltip(stack, tooltipLines, tooltipContext);

        for (TooltipCallback tooltipCallback : PaperModAPI.TOOLTIP_CALLBACKS) {
            tooltipCallback.addTooltip(player, stack, tooltipLines);
        }

        List<Component> tooltipTransformed = tooltipLines.stream().map(component -> {
            Component modified = component;
            if (!component.hasDecoration(TextDecoration.ITALIC))
                modified = component.decoration(TextDecoration.ITALIC, false);
            if (component.color() == null)
                modified = component.color(TextColor.color(-1));
            return modified;
        }).toList();
        lines.addAll(tooltipTransformed);
        packetItem.setComponent(ComponentTypes.LORE, new ItemLore(lines));

        NBTCompound customData = packetItem.getComponentOr(ComponentTypes.CUSTOM_DATA, new NBTCompound());
        stack.setAmount(1);
        customData.setTag("modapi:original_item", new NBTByteArray(stack.serializeAsBytes()));
        packetItem.setComponent(ComponentTypes.CUSTOM_DATA, customData);
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        Player player = event.getPlayer();
        if (player == null)
            return;
        switch (event.getPacketType()) {
            /*case PacketType.Play.Server.SET_SLOT -> {
                final var packet = new de.cjdev.papermodapi.packet.wrapper.play.server.WrapperPlayServerSetSlot(event);
                packet.setItem(applyCustom(packet.getItem()));
                PaperModAPI.LOGGER.info(packet.getItem().toString());
                //appendTooltip(packet.getItem(), player);
                //event.setCancelled(true);
            }
            case PacketType.Play.Server.WINDOW_ITEMS -> {
                // The fact that this even gets made, makes packetevents throw
                // which is why I need to make a custom wrapper
                //var packet = new WrapperPlayServerWindowItems(event);
                final var packet = new WrapperPlayServerWindowItems(event);

                ListIterator<net.minecraft.world.item.ItemStack> iterator = packet.getItems().listIterator();
                while (iterator.hasNext()) {
                    iterator.set(applyCustom(iterator.next()));
                } // eh, not rn

                //event.setCancelled(true);
            }
            case PacketType.Play.Server.ENTITY_METADATA -> {
                //final var packet = new WrapperPlayServerEntityMetadata(event);
                event.setCancelled(true);
            }*/
            //case PacketType.Play.Server.SYSTEM_CHAT_MESSAGE -> {
            //    //final var packet = new WrapperPlayServerSystemChatMessage(event);
            //    //packet.setMessage(modifyShowItemHoverEvents(packet.getMessage()));
            //    event.setCancelled(true);
            //} // man i just realised it would be so much easier with mixins, just change Item.STREAM_CODEC fr :sob: lol
            /*case PacketType.Play.Server.ENTITY_EQUIPMENT -> {
                // that fixes it, trust
                event.setCancelled(true);
                // ahh, don't have access wideners w(ﾟДﾟ)w
            }
            case PacketType.Play.Server.SET_CURSOR_ITEM -> {
                final var packet = new WrapperPlayServerSetCursorItem(event);
                packet.setStack(applyCustom(packet.getStack()));
            }
            case PacketType.Play.Server.SET_PLAYER_INVENTORY -> {
                final var packet = new WrapperPlayServerSetPlayerInventory(event);
                packet.setStack(applyCustom(packet.getStack()));
            }*/
            default -> {}
        }
    }

    public static Component modifyShowItemHoverEvents(Component component) {
        Style style = component.style();
        HoverEvent<?> hoverEvent = style.hoverEvent();

        PaperModAPI.LOGGER.info(component.toString());
        if (hoverEvent != null && hoverEvent.action() == HoverEvent.Action.SHOW_ITEM) {
            HoverEvent.ShowItem showItem = (HoverEvent.ShowItem) hoverEvent.value();
            HoverEvent.ShowItem modified = HoverEvent.ShowItem.showItem(Key.key("minecraft", "dirt"), showItem.count(), showItem.dataComponents());

            component = component.style(style.hoverEvent(HoverEvent.showItem(modified)));
        }

        List<Component> modifiedChildren = component.children().stream()
                .map(PaperModAPIPacketListener::modifyShowItemHoverEvents)
                .toList();

        return component.children(modifiedChildren);
    }

    @Override
    public void onUserConnect(UserConnectEvent event) {
//        final UUID uuid = event.getUser().getUUID();
//        if (uuid == null)
//            return;
    }

    @Override
    public void onUserDisconnect(UserDisconnectEvent event) {
        final UUID uuid = event.getUser().getUUID();
        if (uuid == null)
            return;
        lastUsed.remove(uuid);
        startedUsing.remove(uuid);
    }
}
