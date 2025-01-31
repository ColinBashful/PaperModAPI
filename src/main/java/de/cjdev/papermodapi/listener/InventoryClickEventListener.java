package de.cjdev.papermodapi.listener;

import de.cjdev.papermodapi.api.component.CustomDataComponents;
import de.cjdev.papermodapi.inventory.CustomCreativeInventory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.view.BrewingStandView;

public class InventoryClickEventListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder(false) instanceof CustomCreativeInventory inventory) {
            inventory.onClickEvent(event);
            return;
        }
        if(event.getClickedInventory() instanceof BrewingStandView brewingStandView){

        }
        if (event.getClickedInventory() instanceof FurnaceInventory furnaceInventory) {
            if (event.getSlotType() != InventoryType.SlotType.FUEL) return;
            switch (event.getClick()) {
                case LEFT:
                case RIGHT:
                    break;
                default:
                    return;
            }
            boolean rightClick = event.getClick() == ClickType.RIGHT;
            assert event.getCurrentItem() != null;
            ItemStack currentItem = event.getCurrentItem();
            ItemStack cursorItem = event.getCursor();
            if (cursorItem.isEmpty())
                return;
            if(currentItem.getType().isFuel()){
                return;
            }
            if(!CustomDataComponents.FUEL_COMPONENT.has(cursorItem))
                return;
            event.setCancelled(true);
            if (rightClick) {
                if (currentItem.isEmpty()) {
                    furnaceInventory.setFuel(cursorItem.asOne());
                } else if (cursorItem.isSimilar(currentItem)) {
                    furnaceInventory.getFuel().add();
                } else {
                    return;
                }
                cursorItem.subtract();
                return;
            }
            if (currentItem.isEmpty()) {
                furnaceInventory.setFuel(cursorItem);
                cursorItem.subtract(cursorItem.getAmount());
            } else if (cursorItem.isSimilar(currentItem)) {
                if (currentItem.getAmount() < currentItem.getMaxStackSize()) {
                    int oldFuelAmount = currentItem.getAmount();
                    currentItem.add(cursorItem.getAmount());
                    cursorItem.subtract(currentItem.getAmount() - oldFuelAmount);
                }
            } else {
                event.setCurrentItem(cursorItem);
                event.setCursor(currentItem);
            }
        } else if (event.getInventory() instanceof FurnaceInventory furnaceInventory) {
            switch (event.getClick()) {
                case SHIFT_LEFT:
                case SHIFT_RIGHT:
                    break;
                default:
                    return;
            }
            assert event.getCurrentItem() != null;
            ItemStack currentItem = event.getCurrentItem();
            if (currentItem.isEmpty())
                return;
            if(currentItem.getType().isFuel()){
                return;
            }

            if(!CustomDataComponents.FUEL_COMPONENT.has(currentItem))
                return;
            event.setCancelled(true);
            ItemStack fuelStack = furnaceInventory.getFuel();
            if (fuelStack == null) {
                furnaceInventory.setFuel(event.getCurrentItem());
                event.setCurrentItem(ItemStack.empty());
            } else if (fuelStack.isSimilar(currentItem) && fuelStack.getAmount() < fuelStack.getMaxStackSize()) {
                int oldFuelAmount = fuelStack.getAmount();
                fuelStack.add(currentItem.getAmount());
                currentItem.subtract(fuelStack.getAmount() - oldFuelAmount);
            } else {
                event.setCancelled(false);
            }
        }
    }
}
