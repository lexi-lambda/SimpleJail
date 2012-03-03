package com.imjake9.simplejail.api;

import org.bukkit.command.CommandSender;

public interface SimpleJailCommandListener {
    
    /**
     * Called when a player issues a /jail or /unjail command.
     * 
     * To handle this command, return true. To allow SimpleJail or
     * other SimpleJail plugins to handle it, return false.
     * 
     * @param sender the sender of the command
     * @param command the name of the command
     * @param args the arguments for the command
     * @return whether or not the command was handled
     */
    boolean handleJailCommand(CommandSender sender, String command, String[] args);
    
    /**
     * Controls the priority of event handling.
     * 
     * Works differently from Bukkit's EventPriority system. Higher-level
     * listeners are called first, and if they handle the command,
     * the lower levels won't get called. The exception is MONITOR, which
     * is always called, and returning true/false does nothing in that level.
     */
    public enum Priority {
        
        HIGHEST(0),
        HIGH(1),
        NORMAL(2),
        LOW(3),
        LOWEST(4),
        MONITOR(5);
        
        private int slot;
        
        Priority (int slot) {
            this.slot = slot;
        }
        
        int getSlot() {
            return slot;
        }
        
    }
    
}
