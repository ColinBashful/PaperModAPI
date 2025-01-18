package de.cjdev.papermodapi.listener;

import de.cjdev.papermodapi.PaperModAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinEventListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        event.getPlayer().sendMessage("JOINED?");
        PaperModAPI.refreshResourcePack(event.getPlayer());
    }
}
