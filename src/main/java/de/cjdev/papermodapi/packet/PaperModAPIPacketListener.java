package de.cjdev.papermodapi.packet;

import com.github.retrooper.packetevents.event.*;
import com.github.retrooper.packetevents.protocol.component.ComponentTypes;
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemLore;
import com.github.retrooper.packetevents.protocol.nbt.NBTByteArray;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.InteractionHand;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientCreativeInventoryAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientUseItem;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetSlot;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowItems;
import de.cjdev.papermodapi.PaperModAPI;
import de.cjdev.papermodapi.api.item.CustomItem;
import de.cjdev.papermodapi.api.item.CustomItems;
import de.cjdev.papermodapi.api.item.TooltipCallback;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import io.papermc.paper.inventory.tooltip.TooltipContext;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.world.item.Item;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

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
                final WrapperPlayClientCreativeInventoryAction packet = new WrapperPlayClientCreativeInventoryAction(event);
                NBTByteArray originalItem = packet.getItemStack().getComponentOr(ComponentTypes.CUSTOM_DATA, new NBTCompound()).getTagOfTypeOrNull("modapi:original_item", NBTByteArray.class);
                if (originalItem == null) return;
                ItemStack originalStack = ItemStack.deserializeBytes(originalItem.getValue());
                originalStack.setAmount(packet.getItemStack().getAmount());
                packet.setItemStack(SpigotConversionUtil.fromBukkitItemStack(originalStack));
            }
            case PacketType.Play.Client.USE_ITEM -> {
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
            }
            default -> {
            }
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
            case PacketType.Play.Server.SET_SLOT -> {
                WrapperPlayServerSetSlot packet = new WrapperPlayServerSetSlot(event);
                appendTooltip(packet.getItem(), player);
            }
            case PacketType.Play.Server.WINDOW_ITEMS -> {
                WrapperPlayServerWindowItems packet = new WrapperPlayServerWindowItems(event);
                packet.getItems().forEach(packetItem -> appendTooltip(packetItem, player));
            }
            default -> {}
        }
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
