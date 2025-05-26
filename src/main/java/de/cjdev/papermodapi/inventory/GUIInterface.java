package de.cjdev.papermodapi.inventory;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public interface GUIInterface {
    default void onClickEvent(InventoryClickEvent event) {
    }

    default void onDragEvent(InventoryDragEvent event) {
    }

    default void onCloseEvent(InventoryCloseEvent event) {
    }
}
