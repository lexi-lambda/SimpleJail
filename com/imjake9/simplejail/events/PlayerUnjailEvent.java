package com.imjake9.simplejail.events;

import com.imjake9.simplejail.api.JailInfo;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerUnjailEvent extends Event {
    
    private static final HandlerList handlers = new HandlerList();
    
    private Location unjailLocation;
    
    private boolean cancelled = false;
    
    private JailInfo info;
    
    public PlayerUnjailEvent(JailInfo info, Location unjailLocation) {
        this.info = info;
        this.unjailLocation = unjailLocation;
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
        return this.unjailLocation;
    }
    
    /**
     * Sets the location where the player will be unjailed.
     * 
     * @param unjailLocation 
     */
    public void setUnjailLocation(Location unjailLocation) {
        this.unjailLocation = unjailLocation;
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