package de.cjdev.papermodapi.listener;

import de.cjdev.papermodapi.api.item.CustomItem;
import de.cjdev.papermodapi.api.item.CustomItems;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.inventory.ItemStack;

public class ItemDespawnEventListener implements Listener {
    @EventHandler
    public void onItemDespawn(ItemDespawnEvent event){
        ItemStack stack = event.getEntity().getItemStack();
        CustomItem customItem = CustomItems.getItemByStack(stack);
        if (customItem == null)
            return;
        customItem.onItemEntityDestroyed(event.getEntity());
    }
}
