package com.imjake9.simplejail;

import com.imjake9.simplejail.SimpleJail.JailMessage;
import com.imjake9.simplejail.SimpleJail.JailStatus;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
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
        
        event.setRespawnLocation(plugin.getJailLocation());
        
    }
    
    @Override
    public void onPlayerJoin(PlayerJoinEvent event) {
        
        final Player player = event.getPlayer();
        JailStatus status = plugin.getPlayerStatus(player);
        
        if(!plugin.playerIsJailed(player)) return;

        if (plugin.playerIsTempJailed(player)) {

            double tempTime = plugin.getTempJailTime(player);
            long currentTime = System.currentTimeMillis();

            if (tempTime <= currentTime) {
                plugin.unjailPlayer(plugin.console, new String[]{player.getName()}, true);
            }
            
        }
        
        if (!plugin.playerIsJailed(player)) return;
        
        // If player is still jailed, check status:
        if (status == JailStatus.PENDING) {
            
            if (plugin.playerIsTempJailed(player)) {
                int minutes = (int) ((plugin.getTempJailTime(player) - System.currentTimeMillis()) / 60000);
                JailMessage.TEMPJAILED.send(player, plugin.prettifyMinutes(minutes));
            } else {
                JailMessage.JAILED.send(player);
            }
            player.teleport(plugin.getJailLocation());
            
            
        } else if (status == JailStatus.FREED) {
            plugin.unjailPlayer(plugin.console, new String[]{player.getName()}, false);
        }
        
    }
    
}
