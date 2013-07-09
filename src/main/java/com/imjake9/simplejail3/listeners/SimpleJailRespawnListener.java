package com.imjake9.simplejail3.listeners;

import com.imjake9.simplejail3.SimpleJail;
import com.imjake9.simplejail3.api.SimpleJailAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;


public class SimpleJailRespawnListener implements Listener {
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        SimpleJailAPI api = SimpleJail.getAPI();
        Player player = event.getPlayer();
        String playerName = player.getName();
        
        if (!api.isPlayerJailed(playerName)) return;
        
        event.setRespawnLocation(api.getJailLocation());
    }
    
}
