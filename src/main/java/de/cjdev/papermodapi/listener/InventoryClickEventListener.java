package de.cjdev.papermodapi.listener;

import de.cjdev.papermodapi.inventory.CustomCreativeInventory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClickEventListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder(false) instanceof CustomCreativeInventory inventory)
            inventory.onClickEvent(event);
    }
}
