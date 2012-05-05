package com.imjake9.simplejail.events;

import com.imjake9.simplejail.api.JailInfo;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerJailEvent extends Event {
    
    private static final HandlerList handlers = new HandlerList();
    
    private int length;
    private Location jailLocation;
    
    private boolean cancelled = false;
    
    private JailInfo info;
    
    public PlayerJailEvent(JailInfo info, Location jailLocation) {
        this(info, jailLocation, -1);
    }
    
    public PlayerJailEvent(JailInfo info, Location jailLocation, int length) {
        this.info = info;
        this.jailLocation = jailLocation;
        this.length = length;
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
     * Gets the location where the player will be jailed.
     * 
     * @return 
     */
    public Location getJailLocation() {
        return this.jailLocation;
    }
    
    /**
     * Sets the location where the player will be jailed.
     * 
     * @param jailLocation 
     */
    public void setJailLocation(Location jailLocation) {
        this.jailLocation = jailLocation;
    }
    
    /**
     * Returns true if this jail is a tempjail.
     * 
     * @return 
     */
    public boolean isTempJail() {
        return length > 0;
    }
    
    /**
     * Gets the length of the jail in minutes.
     * Returns -1 if not a tempjail.
     * 
     * @return 
     */
    public int getLength() {
        return this.length;
    }
    
    /**
     * Sets the tempjail length. Setting to -1 will make it a permajail.
     * 
     * @param length 
     */
    public void setLength(int length) {
        this.length = length;
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
