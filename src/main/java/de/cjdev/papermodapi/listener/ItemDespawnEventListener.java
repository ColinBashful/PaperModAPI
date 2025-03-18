package de.cjdev.papermodapi.listener;

import de.cjdev.papermodapi.api.item.CustomItem;
import de.cjdev.papermodapi.api.item.CustomItems;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemDespawnEvent;

public class ItemDespawnEventListener implements Listener {
    @EventHandler
    public void onItemDespawn(ItemDespawnEvent event){
        CustomItem customItem;
        if ((customItem = CustomItems.getItemByStack(event.getEntity().getItemStack())) != null)
            customItem.onItemEntityDestroyed(event.getEntity());
    }
}
