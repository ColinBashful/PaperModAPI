package de.cjdev.papermodapi.inventory;

import de.cjdev.papermodapi.api.itemgroup.CustomItemGroup;
import de.cjdev.papermodapi.api.itemgroup.CustomItemGroups;
import de.cjdev.papermodapi.helper.PlayerHeadHelper;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.component.CustomData;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

public class CustomCreativeInventory implements InventoryHolder {
    private final JavaPlugin plugin;
    private final CustomItemGroup selectedItemGroup;
    private final Inventory inventory;
    private int page = 0;
    private final int maxPage;
    private final List<ItemStack> items;

    public CustomCreativeInventory(JavaPlugin plugin, boolean hasOp, CustomItemGroup selectedItemGroup) {
        this.plugin = plugin;
        this.selectedItemGroup = selectedItemGroup;
        if(selectedItemGroup != null){
            this.inventory = plugin.getServer().createInventory(this, 9*6, selectedItemGroup.displayName);
            selectedItemGroup.updateEntries(hasOp);
            this.items = selectedItemGroup.getDisplayStacks().stream().toList();
        }else {
            this.inventory = plugin.getServer().createInventory(this, 9*6, Component.text("Custom Items"));
            this.items = CustomItemGroups.getItemGroups().values().stream().map(itemGroup -> {
                ItemStack iconStack = itemGroup.iconSupplier.get();
                iconStack.editMeta(itemMeta -> itemMeta.itemName(itemGroup.displayName));
                CompoundTag customData = new CompoundTag();
                customData.putString("group", CustomItemGroups.getKeyByGroup(itemGroup).asString());
                CraftItemStack.unwrap(iconStack).set(DataComponents.CUSTOM_DATA, CustomData.of(customData));
                return iconStack;
            }).toList();
        }
        this.maxPage = this.items.size() / (inventory.getSize() - 9);
        refresh();
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    private void refresh() {
        inventory.clear();

        ItemStack leftArrow = PlayerHeadHelper.getSkull("http://textures.minecraft.net/texture/8550b7f74e9ed7633aa274ea30cc3d2e87abb36d4d1f4ca608cd44590cce0b");
        leftArrow.editMeta(itemMeta -> itemMeta.itemName(Component.text("Back")));
        ItemStack rightArrow = PlayerHeadHelper.getSkull("http://textures.minecraft.net/texture/96339ff2e5342ba18bdc48a99cca65d123ce781d878272f9d964ead3b8ad370");
        rightArrow.editMeta(itemMeta -> itemMeta.itemName(Component.text("Next")));
        ItemStack backButton = PlayerHeadHelper.getSkull("http://textures.minecraft.net/texture/2ca9dc66a54962f4ed04b2455aa994b4848896ea6ce9dc5e450ccdcf95f6b52c");
        backButton.editMeta(itemMeta -> itemMeta.itemName(Component.text("Menu")));
        ItemStack placeholder = ItemStack.of(Material.GRAY_STAINED_GLASS_PANE);
        placeholder.editMeta(itemMeta -> itemMeta.setHideTooltip(true));

        final int validInventorySize = inventory.getSize() - 9;
        IntStream.range(validInventorySize, this.inventory.getSize()).forEach(value -> inventory.setItem(value, placeholder));
        if (selectedItemGroup != null)
            inventory.setItem(this.inventory.getSize() - 5, backButton);
        if (page != 0)
            inventory.setItem(this.inventory.getSize() - 9, leftArrow);
        if (page < maxPage)
            inventory.setItem(this.inventory.getSize() - 1, rightArrow);

        IntStream.range(validInventorySize * page, Math.min(validInventorySize * page + validInventorySize, items.size()))
                .forEach(value -> inventory.setItem(value % validInventorySize, items.get(value)));
    }

    public void onClickEvent(InventoryClickEvent event) {
        HumanEntity player = event.getWhoClicked();

        boolean uiClick = event.getClickedInventory() == inventory;
        int clickedSlot = event.getSlot();
        int validInventorySize = inventory.getSize() - 9;

        if (uiClick && clickedSlot >= validInventorySize) {
            event.setCancelled(true);
            if (clickedSlot == validInventorySize && page > 0) {
                --page;
                refresh();
            } else if (clickedSlot == inventory.getSize() - 5 && selectedItemGroup != null) {
                player.openInventory(new CustomCreativeInventory(plugin, player.isOp(), null).getInventory());
                this.inventory.close();
            } else if (clickedSlot == inventory.getSize() - 1 && page < maxPage) {
                ++page;
                refresh();
            }
            return;
        }

        ItemStack currentItem = event.getCurrentItem();

        if (uiClick && selectedItemGroup == null) {
            event.setCancelled(true);
            if (currentItem == null || currentItem.isEmpty())
                return;
            Map<NamespacedKey, CustomItemGroup> itemGroups = CustomItemGroups.getItemGroups();
            String group = CraftItemStack.unwrap(currentItem).get(DataComponents.CUSTOM_DATA).getUnsafe().getString("group");
            player.openInventory(new CustomCreativeInventory(plugin, player.isOp(), itemGroups.get(NamespacedKey.fromString(group))).getInventory());
            this.inventory.close();
            return;
        }

        if (currentItem != null) {
            currentItem = currentItem.clone();
            event.setCurrentItem(currentItem);
        }
        boolean sameStack = currentItem != null && currentItem.isSimilar(event.getCursor()); // Gave an error if empty, so I did this ;3
        boolean emptyCursor = event.getCursor().isEmpty();

        switch (event.getClick()) {
            case DOUBLE_CLICK:
                event.setCancelled(true);
                return;
            case SHIFT_LEFT, SHIFT_RIGHT:
                if (uiClick) {
                    if (emptyCursor) {
                        assert currentItem != null;
                        event.getView().setCursor(currentItem.asQuantity(currentItem.getMaxStackSize()));
                    } else {
                        event.getView().setCursor(sameStack ? currentItem.asQuantity(currentItem.getMaxStackSize()) : ItemStack.empty());
                    }
                } else {
                    event.setCurrentItem(ItemStack.empty());
                }

                event.setCancelled(true);
                break;
            case SWAP_OFFHAND:
                if (!uiClick || !emptyCursor)
                    break;
                event.setCancelled(true);
                assert currentItem != null;
                player.getInventory().setItemInOffHand(currentItem.asQuantity(currentItem.getMaxStackSize()));
                break;
            case NUMBER_KEY:
                if (!uiClick)
                    break;
                event.setCancelled(true);
                assert currentItem != null;
                player.getInventory().setItem(event.getHotbarButton(), currentItem.asQuantity(currentItem.getMaxStackSize()));
                return;
            case MIDDLE:
                return;
        }

        if (!uiClick)
            return;

        switch (event.getAction()) {
            case SWAP_WITH_CURSOR:
                event.setCancelled(true);
                if (event.getClick().isLeftClick()) {
                    event.getCursor().setAmount(0);
                } else {
                    event.getCursor().subtract();
                }
                break;
            case PLACE_ALL, PLACE_ONE, PLACE_SOME:
                event.setCancelled(true);
                switch (event.getClick()) {
                    case LEFT:
                        if (sameStack) {
                            event.getCursor().add();
                        } else {
                            event.getCursor().setAmount(0);
                        }
                        break;
                    case RIGHT:
                        event.getCursor().subtract();
                }
                break;
            case PICKUP_ALL, PICKUP_HALF:
                event.setCancelled(true);
                event.getView().setCursor(event.getCurrentItem());
                break;
            case MOVE_TO_OTHER_INVENTORY:
                event.setCancelled(true);
                break;
        }
    }
}
