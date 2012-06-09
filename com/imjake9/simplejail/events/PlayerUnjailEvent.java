package com.imjake9.simplejail.events;

import com.imjake9.simplejail.api.JailInfo;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerUnjailEvent extends Event {
    
    private static final HandlerList handlers = new HandlerList();
    
    private boolean cancelled = false;
    
    private JailInfo info;
    
    public PlayerUnjailEvent(JailInfo info) {
        this.info = info;
    }
    
    /**
     * Gets the jail information.
     * 
     * @return 
     */
    public JailInfo getInfo() {
        return info;
    }
    
    /**
     * Gets the location where the player will be unjailed.
     * 
     * @return 
     */
    public Location getUnjailLocation() {
        return info.getUnjailLocation();
    }
    
    /**
     * Sets the location where the player will be unjailed.
     * 
     * @param unjailLocation 
     */
    public void setUnjailLocation(Location unjailLocation) {
        info.setUnjailLocation(unjailLocation);
    }
    
    public boolean isCancelled() {
        return this.cancelled;
    }
    
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
    
}