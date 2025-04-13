package de.cjdev.papermodapi.packet;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
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

import java.util.*;

public class PaperModAPIPacketListener implements PacketListener {

    private static final HashMap<UUID, Integer> lastUsed = new HashMap<>(Bukkit.getMaxPlayers());
    private static final HashMap<UUID, Integer> startedUsing = new HashMap<>(Bukkit.getMaxPlayers());

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        switch (event.getPacketType()) {
            case PacketType.Play.Client.CREATIVE_INVENTORY_ACTION -> {
                WrapperPlayClientCreativeInventoryAction packet = new WrapperPlayClientCreativeInventoryAction(event);
                NBTByteArray originalItem = packet.getItemStack().getComponentOr(ComponentTypes.CUSTOM_DATA, new NBTCompound()).getTagOfTypeOrNull("modapi:original_item", NBTByteArray.class);
                if (originalItem == null) return;
                ItemStack originalStack = ItemStack.deserializeBytes(originalItem.getValue());
                packet.setItemStack(SpigotConversionUtil.fromBukkitItemStack(originalStack));
            }
            case PacketType.Play.Client.USE_ITEM -> {
                WrapperPlayClientUseItem packet = new WrapperPlayClientUseItem(event);

                EquipmentSlot hand = EquipmentSlot.OFF_HAND;
                if (packet.getHand() == InteractionHand.MAIN_HAND)
                    hand = EquipmentSlot.HAND;

                int currentTick = Bukkit.getCurrentTick();
                User user = event.getUser();
                Player player = event.getPlayer();
                Integer previousLastUsed = lastUsed.put(user.getUUID(), currentTick);
                Integer finalPreviousLastUsed = previousLastUsed;
                startedUsing.compute(user.getUUID(), (uuid, integer) -> {
                    if (integer != null) {
                        if (finalPreviousLastUsed == null)
                            return currentTick;
                        if (finalPreviousLastUsed + 4 != currentTick) {
                            return currentTick;
                        }
                        return integer;
                    }
                    return currentTick;
                });
                if (previousLastUsed == null)
                    previousLastUsed = currentTick;
                else if (previousLastUsed + 4 != currentTick) {
                    previousLastUsed = currentTick;
                }

                ItemStack usedItem = player.getInventory().getItem(hand);
                CustomItem customItem = CustomItems.getItemByStack(usedItem);
                if (customItem == null)
                    return;

                for (int t = previousLastUsed; t < currentTick; ++t) {
                    customItem.usageTick(player.getWorld(), player, usedItem, t - startedUsing.get(user.getUUID()));
                }
            }
            default -> {
            }
        }
    }

    private static void appendTooltip(com.github.retrooper.packetevents.protocol.item.ItemStack packetItem, Player player) {
        if (packetItem == null || packetItem.isEmpty())
            return;
        ItemStack stack = SpigotConversionUtil.toBukkitItemStack(packetItem);
        CustomItem item = CustomItems.getItemByStack(stack);

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
        stack.setAmount(Math.min(stack.getAmount(), Item.ABSOLUTE_MAX_STACK_SIZE));
        customData.setTag("modapi:original_item", new NBTByteArray(stack.serializeAsBytes()));
        packetItem.setComponent(ComponentTypes.CUSTOM_DATA, customData);
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        switch (event.getPacketType()) {
            case PacketType.Play.Server.SET_SLOT -> {
                WrapperPlayServerSetSlot packet = new WrapperPlayServerSetSlot(event);
                appendTooltip(packet.getItem(), event.getPlayer());
            }
            case PacketType.Play.Server.WINDOW_ITEMS -> {
                WrapperPlayServerWindowItems packet = new WrapperPlayServerWindowItems(event);
                packet.getItems().forEach(packetItem -> appendTooltip(packetItem, event.getPlayer()));
            }
            default -> {}
        }
    }
}
