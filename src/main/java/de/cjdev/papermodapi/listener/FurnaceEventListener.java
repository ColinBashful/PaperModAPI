package de.cjdev.papermodapi.listener;

import de.cjdev.papermodapi.PaperModAPI;
import de.cjdev.papermodapi.api.item.CustomItem;
import de.cjdev.papermodapi.api.item.CustomItems;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.FurnaceStartSmeltEvent;

public class FurnaceEventListener implements Listener {
    /// Calls when the Furnace wants to burn an item for fuel
    @EventHandler
    public void onFurnaceBurn(FurnaceBurnEvent event) {
        CustomItem customFuel = CustomItems.getItemByStack(event.getFuel());
        if (customFuel == null)
            return;
        Integer fuelTicks = PaperModAPI.FuelItems.get(customFuel);
        if (fuelTicks == null)
            return;
        event.setBurnTime(fuelTicks);
    }

    @EventHandler
    public void onFurnaceStartSmelt(FurnaceStartSmeltEvent event) {

    }

    @EventHandler
    public void onFurnaceSmelt(FurnaceSmeltEvent event) {

    }

    @EventHandler
    public void onFurnaceExtract(FurnaceExtractEvent event) {

    }
}
