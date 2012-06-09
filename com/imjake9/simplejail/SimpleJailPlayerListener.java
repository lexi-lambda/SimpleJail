package com.imjake9.simplejail;

import com.imjake9.simplejail.SimpleJail.JailMessage;
import com.imjake9.simplejail.SimpleJail.JailStatus;
import com.imjake9.simplejail.utils.Messaging;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class SimpleJailPlayerListener implements Listener {
    
    private final SimpleJail plugin;
    
    public SimpleJailPlayerListener(SimpleJail plugin) {
        
        this.plugin = plugin;
        
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerRespawn(final PlayerRespawnEvent event) {
        
        Player player = event.getPlayer();
        
        if(!plugin.playerIsJailed(player)) return;
        
        event.setRespawnLocation(plugin.getJailLocation(player.getName()));
        
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        
        final Player player = event.getPlayer();
        JailStatus status = plugin.getPlayerStatus(player);
        
        if(!plugin.playerIsJailed(player)) return;

        if (plugin.playerIsTempJailed(player) && status != JailStatus.PENDING) {

            double tempTime = plugin.getTempJailTime(player);
            long currentTime = System.currentTimeMillis();

            if (tempTime <= currentTime) {
                try {
                    plugin.unjailPlayer(player.getName());
                } catch (JailException ex) {
                    // Should never happen
                    ex.printStackTrace();
                    return;
                }
                SimpleJail.getMessager().info(JailMessage.UNTEMPJAILED, player.getName());
                return;
            }
            
        }
        
        // If player is still jailed, check status:
        if (status == JailStatus.JAILED) {
            
            Messaging.send(JailMessage.PLAYER_IS_JAILED, player);
            if(plugin.playerIsTempJailed(player)) {
                int minutes = (int) ((plugin.getTempJailTime(player) - System.currentTimeMillis()) / 60000);
                Messaging.send(JailMessage.JAIL_TIME, player, plugin.prettifyMinutes(minutes));
            }
            
        } else if (status == JailStatus.PENDING) {
            
            player.teleport(plugin.getJailLocation(player.getName()));
            plugin.setPlayerStatus(player, JailStatus.JAILED);
            
            // Send message
            if (plugin.playerIsTempJailed(player)) {
                int minutes = (int) ((plugin.getTempJailTime(player) - System.currentTimeMillis()) / 60000);
                Messaging.send(JailMessage.TEMPJAILED, player, plugin.prettifyMinutes(minutes));
            } else {
                Messaging.send(JailMessage.JAILED, player);
            }
            
        } else if (status == JailStatus.FREED) {
            try {
                plugin.unjailPlayer(player.getName());
            } catch (JailException ex) {
                // Should never happen
                ex.printStackTrace();
            }
        }
        
    }
    
}
