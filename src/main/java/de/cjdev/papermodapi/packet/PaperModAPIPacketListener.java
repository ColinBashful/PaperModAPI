package de.cjdev.papermodapi.packet;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.component.ComponentTypes;
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemLore;
import com.github.retrooper.packetevents.protocol.item.type.ItemType;
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
import com.github.retrooper.packetevents.protocol.nbt.NBTByteArray;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.recipe.Ingredient;
import com.github.retrooper.packetevents.protocol.recipe.Recipe;
import com.github.retrooper.packetevents.protocol.recipe.RecipeSerializers;
import com.github.retrooper.packetevents.protocol.recipe.data.StoneCuttingRecipeData;
import com.github.retrooper.packetevents.resources.ResourceLocation;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientCreativeInventoryAction;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDeclareRecipes;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetSlot;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowItems;
import de.cjdev.papermodapi.api.item.CustomItem;
import de.cjdev.papermodapi.api.item.CustomItems;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import io.papermc.paper.inventory.tooltip.TooltipContext;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
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
