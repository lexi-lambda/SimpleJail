package com.imjake9.simplejail3.listeners;

import com.imjake9.simplejail3.SimpleJail;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class SimpleJailLoginListener implements Listener {
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerLogin(PlayerLoginEvent event) {
        SimpleJail.getAPI().handlePlayerStatus(event.getPlayer());
    }
    
}
