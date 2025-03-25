package de.cjdev.papermodapi.packet;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.component.ComponentTypes;
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemLore;
import com.github.retrooper.packetevents.protocol.nbt.NBTByteArray;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientCreativeInventoryAction;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetSlot;
import de.cjdev.papermodapi.api.item.CustomItem;
import de.cjdev.papermodapi.api.item.CustomItems;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import io.papermc.paper.inventory.tooltip.TooltipContext;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PaperModAPIPacketListener implements PacketListener {
    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() != PacketType.Play.Client.CREATIVE_INVENTORY_ACTION) return;

        WrapperPlayClientCreativeInventoryAction packet = new WrapperPlayClientCreativeInventoryAction(event);
        NBTByteArray originalItem = packet.getItemStack().getComponentOr(ComponentTypes.CUSTOM_DATA, new NBTCompound()).getTagOfTypeOrNull("modapi:original_item", NBTByteArray.class);
        if (originalItem == null) return;
        ItemStack originalStack = ItemStack.deserializeBytes(originalItem.getValue());
        packet.setItemStack(SpigotConversionUtil.fromBukkitItemStack(originalStack));
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() != PacketType.Play.Server.SET_SLOT) return;

        WrapperPlayServerSetSlot packet = new WrapperPlayServerSetSlot(event);
        com.github.retrooper.packetevents.protocol.item.ItemStack packetItem = packet.getItem().copy();
        ItemStack stack = SpigotConversionUtil.toBukkitItemStack(packetItem);
        CustomItem item = CustomItems.getItemByStack(stack);
        Player player = event.getPlayer();

        TooltipContext tooltipContext = TooltipContext.create(false, player.getGameMode() == GameMode.CREATIVE);
        ItemLore tooltip = packetItem.getComponentOr(ComponentTypes.LORE, new ItemLore(new ArrayList<>()));
        List<Component> lines = new ArrayList<>(tooltip.getLines());
        if (item != null)
            item.appendHoverText(stack, lines, tooltipContext);

        packetItem.setComponent(ComponentTypes.LORE, new ItemLore(lines));

        NBTCompound customData = packetItem.getComponentOr(ComponentTypes.CUSTOM_DATA, new NBTCompound());
        customData.setTag("modapi:original_item", new NBTByteArray(stack.serializeAsBytes()));
        packetItem.setComponent(ComponentTypes.CUSTOM_DATA, customData);

        packet.setItem(packetItem);
    }
}
