package com.imjake9.simplejail;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class SimpleJailPlayerListener extends PlayerListener {
    
    private SimpleJail plugin;
    
    public SimpleJailPlayerListener(SimpleJail plugin) {
        
        this.plugin = plugin;
        
    }
    
    @Override
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        
        Player player = event.getPlayer();
        
        if(!plugin.playerIsJailed(player)) return;
        
        event.setRespawnLocation(plugin.getJailLocation(player));
        
    }
    
}
